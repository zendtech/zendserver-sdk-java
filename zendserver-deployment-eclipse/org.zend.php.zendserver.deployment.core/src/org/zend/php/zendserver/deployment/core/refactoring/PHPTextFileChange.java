package org.zend.php.zendserver.deployment.core.refactoring;

import java.text.MessageFormat;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ContentStamp;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.internal.core.refactoring.BasicElementLabels;
import org.eclipse.ltk.internal.core.refactoring.BufferValidationState;
import org.eclipse.ltk.internal.core.refactoring.Changes;
import org.eclipse.ltk.internal.core.refactoring.ContentStamps;
import org.eclipse.ltk.internal.core.refactoring.Lock;
import org.eclipse.ltk.internal.core.refactoring.RefactoringCorePlugin;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.UndoEdit;
import org.zend.php.zendserver.deployment.core.Messages;

public class PHPTextFileChange extends TextChange{
	IFile oldFile;
	IFile newFile;
	boolean isPerforming=false;
//	public PHPTextFileChange(String name, IFile oldFile, IFile newFile) {
//		super(name, oldFile);
//		this.oldFile=oldFile;
//		this.newFile=newFile;
//	}


	/**
	 * Flag (value 1) indicating that the file's save state has to be kept. This means an
	 * unsaved file is still unsaved after performing the change and a saved one
	 * will be saved.
	 */
	public static final int KEEP_SAVE_STATE= 1 << 0;

	/**
	 * Flag (value 2) indicating that the file is to be saved after the change has been applied.
	 */
	public static final int FORCE_SAVE= 1 << 1;

	/**
	 * Flag (value 4) indicating that the file will not be saved after the change has been applied.
	 */
	public static final int LEAVE_DIRTY= 1 << 2;


	// the file to change
	private IFile fFile;
	private int fSaveMode= KEEP_SAVE_STATE;

	// the mapped text buffer
	private int fAcquireCount;
	private ITextFileBuffer fBuffer;
	private BufferValidationState fValidationState;
	private ContentStamp fContentStamp;

	/**
	 * Creates a new <code>TextFileChange</code> for the given file.
	 *
	 * @param name the change's name mainly used to render the change in the UI
	 * @param file the file this text change operates on
	 */
	public PHPTextFileChange(String name,IFile oldFile, IFile newFile) {
		super(name);
		Assert.isNotNull(oldFile);
		fFile= oldFile;
		this.oldFile=oldFile;
		this.newFile=newFile;
	}

	/**
	 * Sets the save state. Must be one of <code>KEEP_SAVE_STATE</code>,
	 * <code>FORCE_SAVE</code> or <code>LEAVE_DIRTY</code>.
	 *
	 * @param saveMode indicating how save is handled when the document
	 *  gets committed
	 */
	public void setSaveMode(int saveMode) {
		fSaveMode= saveMode;
	}

	/**
	 * Returns the save state set via {@link #setSaveMode(int)}.
	 *
	 * @return the save state
	 */
	public int getSaveMode() {
		return fSaveMode;
	}

	/**
	 * Returns the <code>IFile</code> this change is working on.
	 *
	 * @return the file this change is working on
	 */
	public IFile getFile() {
		return fFile;
	}

	/**
	 * Hook to create an undo change for the given undo edit and content stamp.
	 * This hook gets called while performing the change to construct the
	 * corresponding undo change object.
	 *
	 * @param edit the {@link UndoEdit} to create an undo change for
	 * @param stampToRestore the content stamp to restore when the undo
	 *  edit is executed.
	 *
	 * @return the undo change or <code>null</code> if no undo change can
	 *  be created. Returning <code>null</code> results in the fact that
	 *  the whole change tree can't be undone. So returning <code>null</code>
	 *  is only recommended if an exception occurred during creating the
	 *  undo change.
	 */
	protected Change createUndoChange(UndoEdit edit, ContentStamp stampToRestore) {
		return new PHPUndoTextFileChange(getName(), fFile, edit, stampToRestore, fSaveMode);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getModifiedElement(){
		return fFile;
	}

	public Object[] getAffectedObjects() {
		Object modifiedElement= getModifiedElement();
		if (modifiedElement == null)
			return null;
		return new Object[] { modifiedElement };
	}

	/**
	 * {@inheritDoc}
	 */
	public void initializeValidationData(IProgressMonitor monitor) {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask("", 1); //$NON-NLS-1$
			fValidationState= BufferValidationState.create(fFile);
		} finally {
			monitor.done();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public RefactoringStatus isValid(IProgressMonitor monitor) throws CoreException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask("", 1); //$NON-NLS-1$
			if (fValidationState == null)
				throw new CoreException(new Status(IStatus.ERROR, RefactoringCorePlugin.getPluginId(), "TextFileChange has not been initialialized")); //$NON-NLS-1$

			boolean needsSaving= needsSaving();
			RefactoringStatus result= fValidationState.isValid(needsSaving);
			if (needsSaving) {
				result.merge(Changes.validateModifiesFiles(new IFile[] { fFile}));
			} else {
				// we are reading the file. So it should be at least in sync
				result.merge(Changes.checkInSync(new IFile[] { fFile}));
			}
			return result;
		} finally {
			monitor.done();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		if (fValidationState != null) {
			fValidationState.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IDocument acquireDocument(IProgressMonitor pm) throws CoreException {
		fAcquireCount++;
		if (fAcquireCount > 1)
			return fBuffer.getDocument();

		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		if (isPerforming) {
			fFile=newFile;
		}
		IPath path= fFile.getFullPath();
		manager.connect(path, LocationKind.IFILE, pm);
		fBuffer= manager.getTextFileBuffer(path, LocationKind.IFILE);
		IDocument result= fBuffer.getDocument();
		fContentStamp= ContentStamps.get(fFile, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation of this method only commits the underlying buffer if
	 * {@link #needsSaving()} and {@link #isDocumentModified()} returns <code>true</code>.
	 * </p>
	 */
	protected void commit(IDocument document, IProgressMonitor pm) throws CoreException {
		if (needsSaving()) {
			fBuffer.commit(pm, false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void releaseDocument(IDocument document, IProgressMonitor pm) throws CoreException {
		Assert.isTrue(fAcquireCount > 0);
		if (fAcquireCount == 1) {
			ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
			manager.disconnect(fFile.getFullPath(), LocationKind.IFILE, pm);
		}
		fAcquireCount--;
 	}

	/**
	 * {@inheritDoc}
	 */
	protected final Change createUndoChange(UndoEdit edit) {
		return createUndoChange(edit, fContentStamp);
	}
	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		isPerforming=true;
		return super.perform(pm);
	}
	/*
	 * @see org.eclipse.ltk.core.refactoring.TextChange#performEdits(org.eclipse.jface.text.IDocument)
	 * @since 3.5
	 */
	protected UndoEdit performEdits(final IDocument document) throws BadLocationException, MalformedTreeException {
		
		if (! fBuffer.isSynchronizationContextRequested()) {
			return super.performEdits(document);
		}
		
		ITextFileBufferManager fileBufferManager= FileBuffers.getTextFileBufferManager();
		
		/** The lock for waiting for computation in the UI thread to complete. */
		final Lock completionLock= new Lock();
		final UndoEdit[] result= new UndoEdit[1];
		final BadLocationException[] exception= new BadLocationException[1];
		Runnable runnable= new Runnable() {
			public void run() {
				synchronized (completionLock) {
					try {
						result[0]= PHPTextFileChange.super.performEdits(document);
					} catch (BadLocationException e) {
						exception[0]= e;
					} finally {
						completionLock.fDone= true;
						completionLock.notifyAll();
					}
				}
			}
		};
		
		synchronized (completionLock) {
			fileBufferManager.execute(runnable);
			while (! completionLock.fDone) {
				try {
					completionLock.wait(500);
				} catch (InterruptedException x) {
				}
			}
		}
		
		if (exception[0] != null) {
			throw exception[0];
		}
		
		return result[0];
	}

	/**
	 * Is the document currently acquired?
	 *
	 * @return <code>true</code> if the document is currently acquired,
	 *         <code>false</code> otherwise
	 * @since 3.2
	 */
	protected boolean isDocumentAcquired() {
		return fAcquireCount > 0;
	}

	/**
	 * Has the document been modified since it has been first acquired by the change?
	 *
	 * @return Returns true if the document has been modified since it got acquired by the change.
	 * <code>false</code> is returned if the document has not been acquired yet, or has been released
	 * already.
	 *
	 * @since 3.3
	 */
	protected boolean isDocumentModified() {
		if (fAcquireCount > 0) {
			ContentStamp currentStamp= ContentStamps.get(fFile, fBuffer.getDocument());
			return !currentStamp.equals(fContentStamp);
		}
		return false;
	}

	/**
	 * Does the text file change need saving?
	 * <p>
	 * The implementation of this method returns <code>true</code> if the
	 * <code>FORCE_SAVE</code> flag is enabled, or the underlying file is not
	 * dirty and <code>KEEP_SAVE_STATE</code> is enabled.
	 * </p>
	 *
	 * @return <code>true</code> if it needs saving according to its dirty
	 *         state and the save mode flags, <code>false</code> otherwise
	 * @since 3.3
	 */
	protected boolean needsSaving() {
		if ((fSaveMode & FORCE_SAVE) != 0) {
			return true;
		}
		if ((fSaveMode & KEEP_SAVE_STATE) != 0) {
			return fValidationState == null || !fValidationState.wasDirty();
		}
		return false;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
//		return super.getName();
		IFile file= getFile();
		return MessageFormat.format(
				Messages.PreviewWizardPage_changeElementLabelProvider_textFormat,
			new String[] { BasicElementLabels.getResourceName(file), BasicElementLabels.getPathLabel(file.getParent().getFullPath(), false)});
	}
}
