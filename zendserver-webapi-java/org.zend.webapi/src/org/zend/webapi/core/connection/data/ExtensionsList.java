package org.zend.webapi.core.connection.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of 0 or more extensions info.
 * 
 * @author Bartlomiej Laczkowski
 */
public class ExtensionsList extends AbstractResponseData {

	private static final String EXTENSIONS = "/extensions"; //$NON-NLS-1$

	private List<ExtensionInfo> extensionsInfo = new ArrayList<ExtensionInfo>();

	public ExtensionsList() {
		super(ResponseType.CONFIGURATION_EXTENSIONS_LIST, BASE_PATH + EXTENSIONS, EXTENSIONS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.webapi.core.connection.data.IResponseData#accept(org.zend.webapi
	 * .core.connection.data.IResponseDataVisitor)
	 */
	@Override
	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			if (getExtensionsInfo() != null) {
				for (ExtensionInfo info : getExtensionsInfo()) {
					info.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * Returns extensions info list
	 * 
	 * @return extensions info list
	 */
	public List<ExtensionInfo> getExtensionsInfo() {
		return extensionsInfo;
	}

	protected void setExtensionsInfo(List<ExtensionInfo> extensionsInfo) {
		if (extensionsInfo != null) {
			this.extensionsInfo = extensionsInfo;
		}
	}

}
