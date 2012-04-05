/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.zend.php.zendserver.monitor.core.EventSource;

/**
 * Job responsible for opening source file in the editor.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenInEditorJob extends Job {

	private EventSource eventSource;

	public OpenInEditorJob(EventSource eventSource) {
		super(Messages.OpenInEditorJob_0);
		this.eventSource = eventSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String sourceFile = eventSource.getSourceFile();
				if (sourceFile == null || sourceFile.isEmpty()
						|| eventSource.getLine() < 1) {
					showNotAvailable();
				} else {
					try {
						openEditor();
					} catch (Exception e) {
						Activator.log(e);
						showNotAvailable();
					}
				}
			}
		});
		return Status.OK_STATUS;
	}

	private void openEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IFile file = eventSource.getResource();
		if (file != null) {
			IEditorInput input = new FileEditorInput(file);
			String editorId = getEditorId(file);
			try {
				IEditorPart editor = window.getActivePage().openEditor(input,
						editorId);
				if (editor instanceof ITextEditor) {
					gotoLine((ITextEditor) editor);
					return;
				}
			} catch (PartInitException e) {
				Activator.log(e);
			}
		}
	}

	private void gotoLine(ITextEditor editor) {
		int line = (int) (eventSource.getLine() <= Long
				.valueOf(Integer.MAX_VALUE) ? eventSource.getLine() : -1);
		if (line != -1) {
			IDocumentProvider provider = editor.getDocumentProvider();
			IDocument document = provider.getDocument(editor.getEditorInput());
			try {
				line = document.getLineOffset((int) line - 1);
			} catch (BadLocationException e) {
				line = 0;
			}
			editor.selectAndReveal((int) line, 0);
		}
		IWorkbenchPage page = editor.getSite().getPage();
		page.activate(editor);
	}

	private IContentType getContentType(IFile fileStore) {
		if (fileStore == null)
			return null;
		return Platform.getContentTypeManager().findContentTypeFor(
				fileStore.getName());
	}

	private String getEditorId(IFile fileStore) {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbench workbench = window.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(
				fileStore.getName(), getContentType(fileStore));

		// check the OS for in-place editor (OLE on Win32)
		if (descriptor == null
				&& editorRegistry.isSystemInPlaceEditorAvailable(fileStore
						.getName())) {
			descriptor = editorRegistry
					.findEditor(IEditorRegistry.SYSTEM_INPLACE_EDITOR_ID);
		}

		// check the OS for external editor
		if (descriptor == null
				&& editorRegistry.isSystemExternalEditorAvailable(fileStore
						.getName())) {
			descriptor = editorRegistry
					.findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
		}

		return (descriptor == null) ? "" : descriptor.getId(); //$NON-NLS-1$
	}

	private void showNotAvailable() {
		MessageDialog.openInformation(org.zend.core.notifications.Activator
				.getDefault().getParent(),
				Messages.OpenInEditorJob_UnavailableTitle,
				Messages.OpenInEditorJob_UnavailableMessage);
	}

}
