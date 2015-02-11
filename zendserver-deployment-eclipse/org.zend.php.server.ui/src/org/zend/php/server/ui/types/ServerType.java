package org.zend.php.server.ui.types;

public enum ServerType {

	LOCAL_ZEND_SERVER("org.zend.php.server.ui.types.LocalZendServerType"),

	ZEND_SERVER("org.zend.php.server.ui.types.ZendServerType"),

	OPENSHIFT("org.zend.php.server.ui.types.OpenShiftServerType"),

	PHPCLOUD("org.zend.php.server.ui.types.PhpcloudServerType"),

	LOCAL_APACHE("org.zend.php.server.ui.types.LocalApacheType");

	private String id;

	private ServerType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public static ServerType byId(String id) {
		if (id != null) {
			ServerType[] values = values();
			for (ServerType serverType : values) {
				if (serverType.getId().equals(id)) {
					return serverType;
				}
			}
		}
		return null;
	}

}
