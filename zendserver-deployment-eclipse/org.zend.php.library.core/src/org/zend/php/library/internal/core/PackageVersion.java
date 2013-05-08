package org.zend.php.library.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageVersion {

	private String name;
	private String description;
	private List<String> keywords;
	private String homepage;
	private String version;
	private String versionNormalized;
	private String license;
	private String type;
	private String time;
	private String autoload;
	Map<String, String> requires;

	public PackageVersion() {
		this.keywords = new ArrayList<String>();
		this.requires = new HashMap<String, String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersionNormalized() {
		return versionNormalized;
	}

	public void setVersionNormalized(String versionNormalized) {
		this.versionNormalized = versionNormalized;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAutoload() {
		return autoload;
	}

	public void setAutoload(String autoload) {
		this.autoload = autoload;
	}

	public Map<String, String> getRequires() {
		return requires;
	}

	public void addRequire(String key, String value) {
		requires.put(key, value);
	}

}
