package org.zend.php.zendserver.deployment.core.refactoring;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class DeploymentRefactoring {

	private String name;

	public DeploymentRefactoring(String name) {
		this.name = name;
	}
	
	public TextFileChange createTextChange(IDescriptorContainer container) {
		int origLength = container.getModelSerializer().getDocumentLength();
		IDocument resultDocument = new Document();
		container.connect(resultDocument);
		container.save();
		
		TextFileChange change = new TextFileChange(name, container.getFile());
		change.setEdit(new ReplaceEdit(0, origLength, resultDocument.get()));
		return change;
	}

	public boolean updatePathInDescriptor(String oldFullPath, String newFullPath,
			IDeploymentDescriptor descriptor) {
		boolean updated = false;
		
		updated |= updateIfEquals(descriptor, DeploymentDescriptorPackage.EULA, oldFullPath, newFullPath);
		updated |= updateIfEquals(descriptor, DeploymentDescriptorPackage.ICON, oldFullPath, newFullPath);
		
		return updated;
	}

	public boolean updateIfEquals(IModelObject object, Feature f, String oldValue, String newValue) {
		String path = object.get(f);
		if (oldValue.equals(path)) {
			object.set(f, newValue);
			return true;
		}
		
		return false;
	}
	
}
