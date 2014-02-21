package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class CalculateOffsets {

	public static final String NODE_OFFSET = "node.offset"; //$NON-NLS-1$
	
	private String cachedDoc;
	
	public CalculateOffsets(InputStream src) throws IOException {
		int count = 0;
		byte[] buf = new byte[4096];
		
		StringBuilder sb = new StringBuilder();
		
		while ((count = src.read(buf)) > 0) {
			sb.append(new String(buf, 0, count));
		}
		
		cachedDoc = sb.toString();
	}

	public void traverse(Document document) {
		int offset = 0;
		
		// this cast is checked on Apache implementation (Xerces):
	    DocumentTraversal traversal = (DocumentTraversal) document;
	    TreeWalker tw = traversal.createTreeWalker(document.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, false);
	    
	    Node node = tw.getCurrentNode();
	    while (node != null) {
	    	String nodeName = node.getNodeName();
	    	offset = cachedDoc.indexOf('<'+nodeName, offset) + 1;
	    	if (offset > 0) {
	    		node.setUserData(NODE_OFFSET, offset, null);
	    	}
	    	node = tw.nextNode();
	    }
	}

	public int getDocumentLength() {
		return cachedDoc.length();
	}
}
