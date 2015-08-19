package org.zend.webapi.core.connection.data;

public class VhostDetails  extends AbstractResponseData{

	private static final String VHOST_DETAILS = "/vhostDetails"; //$NON-NLS-1$
	
	private VhostInfo info;
	private VhostExtendedInfo extendedInfo; 
	
	protected VhostDetails(){
		super(ResponseType.VHOST_DETAILS, BASE_PATH + VHOST_DETAILS, VHOST_DETAILS);		
	}
	
	@Override
	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			getInfo().accept(visitor);
			getExtendedInfo().accept(visitor);
			return visitor.visit(this);
		}
		return false;
	}

	public VhostInfo getInfo() {
		return info;
	}

	protected void setInfo(VhostInfo info) {
		this.info = info;
	}

	public VhostExtendedInfo getExtendedInfo() {
		return extendedInfo;
	}

	protected void setExtendedInfo(VhostExtendedInfo extendedInfo) {
		this.extendedInfo = extendedInfo;
	}

}
