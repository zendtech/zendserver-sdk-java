/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.repository;

import java.io.OutputStream;

/**
 * 
 * @author Roy, 2011
 */
public interface IRepository {


	/**
	 * Lists all available applications in the site 
	 * 
	 * @return
	 */
	public Application[] getAvailableApplications();
	
	/**
	 * Returns a sequence of packages that 
	 * @param applicationId
	 * @return
	 */
	public OutputStream[] getApplication(String applicationId, String version);
	
	
	/**
	 * Represents provide information <br>
	 * {@link http ://code.google.com/p/zend-sdk/wiki/RepositorySpec}
	 */
	class Provider {

		private String name;
		private String icon;
		private String URL;
		private String description;

		public String getName() {
			return name;
		}

		public String getIcon() {
			return icon;
		}

		public String getURL() {
			return URL;
		}

		public String getDescription() {
			return description;
		}
	}

	/**
	 * Represents a Category <br>
	 * {@link http ://code.google.com/p/zend-sdk/wiki/RepositorySpec}
	 */
	class Category {

		private String name;
		private String label;
		private String description;

		public String getName() {
			return name;
		}

		public String getLabel() {
			return label;
		}

		public String getDescription() {
			return description;
		}
	}

	public class Application {

	}


}
