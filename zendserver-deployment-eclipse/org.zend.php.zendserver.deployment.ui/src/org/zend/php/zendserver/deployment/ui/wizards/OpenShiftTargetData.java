/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import java.util.List;

import org.zend.sdklib.internal.target.OpenShiftTarget;

/**
 * New OpenShift target wizard data.
 * 
 * @author Wojciech Galanciak, 2012
 *
 */
public class OpenShiftTargetData {

	private String name;
	private String gearProfile;
	private boolean hasMySQLSupport;
	private OpenShiftTarget target;
	private List<String> gearProfiles;
	private List<String> zendTargets;
	private List<String> zendCartridges;
	private boolean eula;
	private String password;
	private String confirmPassword;
	private OpenShiftTarget.Type cartridge;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGearProfile() {
		return gearProfile;
	}

	public void setGearProfile(String gearProfile) {
		this.gearProfile = gearProfile;
	}

	public boolean hasMySQLSupport() {
		return hasMySQLSupport;
	}

	public void setMySQLSupport(boolean hasMySQLSupport) {
		this.hasMySQLSupport = hasMySQLSupport;
	}

	public OpenShiftTarget getTarget() {
		return target;
	}

	public void setTarget(OpenShiftTarget target) {
		this.target = target;
	}

	public List<String> getGearProfiles() {
		return gearProfiles;
	}

	public void setGearProfiles(List<String> gearProfiles) {
		this.gearProfiles = gearProfiles;
	}

	public List<String> getZendTargets() {
		return zendTargets;
	}

	public void setZendTargets(List<String> zendTargets) {
		this.zendTargets = zendTargets;
	}

	public boolean isEula() {
		return eula;
	}

	public void setEula(boolean eula) {
		this.eula = eula;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public List<String> getZendCartridges() {
		return zendCartridges;
	}
	
	public void setZendCartridges(List<String> zendCartridges) {
		this.zendCartridges = zendCartridges;
	}

	public OpenShiftTarget.Type getCartridge() {
		return cartridge;
	}
	
	public void setCartridge(OpenShiftTarget.Type cartridge) {
		this.cartridge = cartridge;
	}
	
}
