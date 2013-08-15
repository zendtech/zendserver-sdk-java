package org.zend.webapi.internal.core.connection;

import org.restlet.engine.Engine;
import org.restlet.engine.connector.HttpServerHelper;
import org.restlet.engine.local.RiapClientHelper;
import org.restlet.engine.local.RiapServerHelper;
import org.restlet.ext.httpclient.HttpClientHelper;

public class WebApiEngine extends Engine {

	public void registerDefaultConnectors() {
		getRegisteredClients().add(new HttpClientHelper(null));
		getRegisteredClients().add(new WebApiHttpClientHelper(null));
		getRegisteredClients().add(
				new org.restlet.engine.local.ClapClientHelper(null));
		getRegisteredClients().add(
				new org.restlet.engine.local.FileClientHelper(null));
		getRegisteredClients().add(
				new org.restlet.engine.local.ZipClientHelper(null));
		getRegisteredClients().add(
				new RiapClientHelper(null));
		getRegisteredServers().add(
				new RiapServerHelper(null));
		getRegisteredServers().add(
				new HttpServerHelper(null));
	}

}
