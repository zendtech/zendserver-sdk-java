package org.zend.php.zendserver.deployment.ui.targets;

public class Contribution {
	
	String name;
	
	String image;
	
	Class<? extends AbstractTargetDetailsComposite> control;
	
	public Contribution(String name, String image, Class<? extends AbstractTargetDetailsComposite> control) {
		this.name = name;
		this.image = image;
		this.control = control;
	}
}