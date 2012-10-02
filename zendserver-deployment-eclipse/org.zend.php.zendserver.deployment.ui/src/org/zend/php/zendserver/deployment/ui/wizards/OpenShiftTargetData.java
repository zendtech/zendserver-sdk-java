package org.zend.php.zendserver.deployment.ui.wizards;

import java.util.List;

import org.zend.sdklib.internal.target.OpenShiftTarget;

public class OpenShiftTargetData {

	private String name;
	private String gearProfile;
	private boolean hasMySQLSupport;
	private OpenShiftTarget target;
	private List<String> gearProfiles;
	private List<String> zendTargets;

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

}
