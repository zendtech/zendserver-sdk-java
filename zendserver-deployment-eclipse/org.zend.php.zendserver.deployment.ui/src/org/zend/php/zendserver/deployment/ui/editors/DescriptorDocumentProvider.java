package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.zend.php.zendserver.deployment.ui.editors.propertiestext.IPropertiesFilePartitions;
import org.zend.php.zendserver.deployment.ui.editors.propertiestext.PropertiesFilePartitionScanner;
import org.zend.php.zendserver.deployment.ui.editors.text.XMLPartitionScanner;


public class DescriptorDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		IFileEditorInput input = (IFileEditorInput) element;
		input.getName();
		if (document != null) {
			IDocumentPartitioner partitioner = null;
			if (input.getName().endsWith("xml")) { //$NON-NLS-1$
				partitioner = new FastPartitioner(new XMLPartitionScanner(), new String[] {
						XMLPartitionScanner.XML_TAG, XMLPartitionScanner.XML_COMMENT });
				partitioner.connect(document);
				document.setDocumentPartitioner(partitioner);
			} else {
				partitioner = new FastPartitioner(new PropertiesFilePartitionScanner(),
						IPropertiesFilePartitions.PARTITIONS);
				partitioner.connect(document);
				((AbstractDocument) document).setDocumentPartitioner(IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING, partitioner);
			}			
		}
		
		return document;
	}
}