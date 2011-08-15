package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IParameter;

public class ParametersCategory {

	public static final String SEPARATOR = "\\."; //$NON-NLS-1$

	private IParameter parameter;
	private String name;
	private List<ParametersCategory> categories;

	public ParametersCategory(IParameter parameter, String name) {
		super();
		this.parameter = parameter;
		this.name = name;
		this.categories = new ArrayList<ParametersCategory>();
	}

	public ParametersCategory(String name) {
		super();
		this.name = name;
		this.categories = new ArrayList<ParametersCategory>();
	}

	public String getName() {
		return name;
	}

	public IParameter getParameter() {
		return parameter;
	}

	public List<ParametersCategory> getCategories() {
		return categories;
	}

	public void addCategory(ParametersCategory category) {
		categories.add(category);
	}

}
