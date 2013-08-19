/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.zend.sdkcli.update.UpdateException;
import org.zend.sdkcli.update.UpdateStatus;
import org.zend.sdkcli.update.parser.SdkVersion;
import org.zend.sdkcli.update.parser.Version;
import org.zend.sdkcli.update.parser.VersionParser;

/**
 * 
 * Represents manager which can be used to perform update process.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class UpdateManager {

	private static final String VERSION_LOCATION = "/lib/";
	private static final String VERSIONS_URL = "file:///Users/galAnonim/zend/workspaces/new_webapi/updates/versions.xml";

	private Version sdkVersion;
	private Version newSdkVersion;
	private List<SdkVersion> versions;
	private File root;
	private String versionUrl;

	public UpdateManager(String root, String versionUrl) throws IOException {
		this.root = new File(root);
		this.versionUrl = versionUrl;
		this.sdkVersion = readCurrentVersion(this.root.getAbsolutePath()
				+ VERSION_LOCATION);
	}

	public UpdateManager(String root) throws IOException {
		this(root, VERSIONS_URL);
	}

	/**
	 * @return version of the local Zend SDK
	 */
	public Version getSdkVersion() {
		return sdkVersion;
	}
	
	/**
	 * @return new Zend SDK version
	 */
	public Version getNewSdkVersion() {
		return newSdkVersion;
	}

	/**
	 * 
	 * Performs update of Zend SDK.
	 * 
	 * @return final status of update process
	 * @throws UpdateException
	 */
	public UpdateStatus performUpdate() throws UpdateException {
		initVersions();
		SdkVersion selectedVersion = null;
		for (SdkVersion version : versions) {
			if (getSdkVersion().compareTo(version.getVersion()) < 0) {
				if (version.getRange().isAllowed(getSdkVersion())) {
					if (selectedVersion == null) {
						selectedVersion = version;
					} else {
						if (selectedVersion.getVersion().compareTo(
								version.getVersion()) < 0) {
							selectedVersion = version;
						}
					}
				}
			}
		}
		if (selectedVersion != null) {
			if (selectedVersion.getDelta().execute(root)) {
				if (writeNewVersion(root.getAbsolutePath() + VERSION_LOCATION,
						selectedVersion.getVersion())) {
					newSdkVersion = selectedVersion.getVersion();
					return UpdateStatus.SUCCESS;
				}
			}
		}
		return UpdateStatus.UP_TO_DATE;
	}

	private void initVersions() throws UpdateException {
		VersionParser versionParser = new VersionParser(versionUrl);
		versions = versionParser.getAvailableVersions();
	}

	private Version readCurrentVersion(String versionDir) throws IOException {
		File versionFile = new File(versionDir, "sdk.version");
		if (!versionFile.exists()) {
			throw new IllegalArgumentException(
					"sdk.version file does not exist");
		}
		Properties props = new Properties();
		props.load(new FileInputStream(versionFile));
		return new Version(props.getProperty("sdk.version"));
	}

	private boolean writeNewVersion(String string, Version version)
			throws UpdateException {
		Properties props = new Properties();
		try {
			props.setProperty("sdk.version", version.getStringValue());
			props.store(new FileOutputStream(new File(string + File.separator
					+ "sdk.version")), null);
			return true;
		} catch (IOException e) {
			throw new UpdateException(e);
		}
	}

}
