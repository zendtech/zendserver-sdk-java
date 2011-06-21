package org.zend.php.zendserver.deployment.ui.editors.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.descriptor.DeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ModelSerializer;
import org.zend.php.zendserver.deployment.ui.editors.DeploymentDescriptorEditor;


public class DescriptorReconcilingStrategy implements IReconcilingStrategy {

	private IDocument fDocument;
	private DeploymentDescriptorEditor fEditor;

	public DescriptorReconcilingStrategy(DeploymentDescriptorEditor editor) {
		this.fEditor = editor;
	}
	
	public void setDocument(IDocument document) {
		this.fDocument = document;
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(subRegion);
	}

	public void reconcile(IRegion partition) {
		IDescriptorContainer container = fEditor.getDescriptorContainer();
		DeploymentDescriptor model = (DeploymentDescriptor) container.getDescriptorModel();
		ModelSerializer ms = new ModelSerializer();
		
		
		ByteArrayInputStream charArray = new ByteArrayInputStream(fDocument.get().getBytes()); // TODO get document encoding
		try {
			ms.load(charArray, model);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
