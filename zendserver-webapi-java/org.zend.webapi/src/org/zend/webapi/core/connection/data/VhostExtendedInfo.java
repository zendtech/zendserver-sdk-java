package org.zend.webapi.core.connection.data;

public class VhostExtendedInfo  extends AbstractResponseData {
	private static final String VHOST_EXTENDED_INFO = "/vhostExtended"; //$NON-NLS-1$
	
	private String docRoot;
	
	protected VhostExtendedInfo(){
		super(ResponseType.VHOST_EXTENDED_INFO, BASE_PATH + VHOST_EXTENDED_INFO, VHOST_EXTENDED_INFO);		
	}

	protected VhostExtendedInfo(String prefix) {
		super(ResponseType.VHOST_EXTENDED_INFO, prefix, VHOST_EXTENDED_INFO);
	}

	@Override
	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	public String getDocRoot() {
		return docRoot;
	}

	protected void setDocRoot(String docRoot) {
		this.docRoot = docRoot;
	}

	
}
