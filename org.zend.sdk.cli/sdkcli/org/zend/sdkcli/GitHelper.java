package org.zend.sdkcli;

import java.net.URISyntaxException;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;
import org.zend.sdklib.internal.target.ZendDevCloud;

public class GitHelper {

	public static final String ZEND_CLOUD_REMOTE = "zendCloudRemote";

	public static String getRemote(String url) {
		try {
			URIish uri = new URIish(url);
			if (uri.getHost().endsWith(ZendDevCloud.DEVPASS_HOST)) {
				return ZEND_CLOUD_REMOTE;
			}
		} catch (URISyntaxException e) {
			// return default Constants.DEFAULT_REMOTE_NAME
		}
		return Constants.DEFAULT_REMOTE_NAME;
	}

}
