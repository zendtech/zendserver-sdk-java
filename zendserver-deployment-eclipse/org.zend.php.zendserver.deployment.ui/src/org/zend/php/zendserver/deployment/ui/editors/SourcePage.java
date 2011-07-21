package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.IDE;
import org.zend.php.zendserver.deployment.ui.editors.text.ColorManager;
import org.zend.php.zendserver.deployment.ui.editors.text.DescriptorSourceViewerConfiguration;


public class SourcePage extends TextEditor implements IFormPage {

	private int fIndex;
	private Control fControl;
	private DeploymentDescriptorEditor fEditor;
	private String fId;
	private ColorManager colorManager;

	public SourcePage(String pageId, DeploymentDescriptorEditor editor) {
		super();
		fEditor = editor;
		fId = pageId;
		colorManager = new ColorManager();

		setDocumentProvider(editor.getDocumentProvider());
		setSourceViewerConfiguration(new DescriptorSourceViewerConfiguration(editor, colorManager));
	}
	
	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		Control[] children = parent.getChildren();
		fControl = children[children.length - 1];

		//PlatformUI.getWorkbench().getHelpSystem().setHelp(fControl, IHelpContextIds.MANIFEST_SOURCE_PAGE);
	}

	public void initialize(FormEditor editor) {
		fEditor = (DeploymentDescriptorEditor) editor;
	}

	public FormEditor getEditor() {
		return fEditor;
	}

	public IManagedForm getManagedForm() {
		// not a form page
		return null;
	}

	public void setActive(boolean active) {
		if (active) {
			// refresh content
		}
	}

	public boolean isActive() {
		return this.equals(fEditor.getActivePageInstance());
	}

	public boolean canLeaveThePage() {
		return true;
	}

	public Control getPartControl() {
		return fControl;
	}

	public String getId() {
		return fId;
	}

	public int getIndex() {
		return fIndex;
	}

	public void setIndex(int index) {
		fIndex = index;
	}

	public boolean isEditor() {
		return true;
	}

	public boolean selectReveal(Object object) {
		if (object instanceof IMarker) {
			IDE.gotoMarker(this, (IMarker) object);
			return true;
		}
		return false;
	}
}