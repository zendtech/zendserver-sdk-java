/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.core.sdk;

import java.util.ArrayList;
import java.util.List;

import org.zend.sdklib.event.IStatusChangeListener;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.library.ILibrary;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;

public class SdkTargetsManager implements ILibrary {

	private TargetsManager manager;

	public SdkTargetsManager() {
		ITargetLoader loader = new UserBasedTargetLoader();
		this.manager = new TargetsManager(loader);
	}

	public List<SdkTarget> getTargets() {
		List<SdkTarget> result = new ArrayList<SdkTarget>();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget target : targets) {
			result.add(new SdkTarget(target));
		}
		return result;
	}

	public SdkTarget getTargetById(String id) {
		return new SdkTarget(manager.getTargetById(id));
	}

	public void addStatusChangeListener(IStatusChangeListener listener) {
		manager.addStatusChangeListener(listener);
	}

	public void removeStatusChangeListener(IStatusChangeListener listener) {
		manager.removeStatusChangeListener(listener);
	}

}
