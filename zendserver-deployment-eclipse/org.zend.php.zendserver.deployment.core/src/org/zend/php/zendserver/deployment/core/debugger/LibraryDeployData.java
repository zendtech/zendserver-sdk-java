/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.debugger;

import java.io.File;

import org.eclipse.core.resources.IProject;

/**
 * Library deployment data.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryDeployData {

	private IProject project;
	private String targetId;
	private String name;
	private String version;
	private File root;
	private boolean addPHPLibrary;
	private boolean warnSynchronize;
	private boolean enableAddLibrary;
	private boolean zpkPackage;
	private boolean makeDefault;
	private int versionId;
	private boolean isVersionDefault;

	public LibraryDeployData() {
		this.enableAddLibrary = true;
		this.versionId = -1;
		this.isVersionDefault = false;
	}

	public String getTargetId() {
		return targetId;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public File getRoot() {
		return root;
	}

	public boolean isAddPHPLibrary() {
		return addPHPLibrary;
	}

	public boolean isWarnSynchronize() {
		return warnSynchronize;
	}

	public boolean isEnableAddLibrary() {
		return enableAddLibrary;
	}
	
	public IProject getProject() {
		return project;
	}
	
	public boolean isZpkPackage() {
		return zpkPackage;
	}

	public boolean makeDefault() {
		return makeDefault;
	}
	
	public int getVersionId() {
		return versionId;
	}
	
	public boolean isVersionDefault() {
		return this.isVersionDefault;	
	}
	
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setRoot(File root) {
		this.root = root;
	}

	public void setAddPHPLibrary(boolean addPHPLibrary) {
		this.addPHPLibrary = addPHPLibrary;
	}

	public void setWarnSynchronize(boolean warnSynchronize) {
		this.warnSynchronize = warnSynchronize;
	}

	public void setEnableAddLibrary(boolean enableAddLibrary) {
		this.enableAddLibrary = enableAddLibrary;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
	
	public void setZpkPackage(boolean zpkPackage) {
		this.zpkPackage = zpkPackage;
	}

	public void setMakeDefault(boolean makeDefault) {
		this.makeDefault = makeDefault;
	}
	
	public void setVersionId(int id) {
		this.versionId = id;
	}
	
	public void setIsVersionDefault(boolean isDefault) {
		this.isVersionDefault = isDefault;
	}
}
