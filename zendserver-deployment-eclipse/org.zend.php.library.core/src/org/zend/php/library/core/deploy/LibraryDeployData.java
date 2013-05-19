/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.core.deploy;

import java.io.File;

/**
 * Library deployment data.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryDeployData {

	private String targetId;
	private String name;
	private String version;
	private File root;
	private boolean addPHPLibrary;
	private boolean warnSynchronize;
	private boolean enableAddLibrary;

	public LibraryDeployData() {
		this.enableAddLibrary = true;
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

}
