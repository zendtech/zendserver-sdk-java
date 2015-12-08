/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPUserLibraryElement;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.zend.php.zendserver.deployment.core.utils.DLTKLibraryUtils;
import org.zend.php.zendserver.deployment.core.utils.LibraryUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
@SuppressWarnings("restriction")
public class ImportZpkWizard extends Wizard {

	private ImportZpkPage importPage;

	private BPUserLibraryElement element;

	private List bpElements;

	public ImportZpkWizard(List bpElements) {
		setWindowTitle(Messages.ImportZpkWizard_Title);
		setDefaultPageImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_LIBRARY));
		setNeedsProgressMonitor(true);

		this.bpElements = bpElements;
		if (this.bpElements == null)
			this.bpElements = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * addPages()
	 */
	public void addPages() {
		importPage = new ImportZpkPage();
		addPage(importPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		final ImportZpkData data = importPage.getData();
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(Messages.ImportZpkWizard_TaskTitle, IProgressMonitor.UNKNOWN);
					if (alreadyExist(data)) {
						importPage.statusChanged(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								MessageFormat.format(Messages.ImportZpkWizard_AlreadyExistsError, data.getName())));
						return;
					}
					try {
						File root = LibraryUtils.unzipPackageToSharedFolder(data.getName(), data.getVersion(),
								new File(data.getPath()));
						Map<String, String> attributes = new HashMap<String, String>();
						attributes.put(DLTKLibraryUtils.TAG_LIBRARYVERSION, data.getVersion());
						BPUserLibraryElement element = new BPUserLibraryElement(data.getName(), false,
								new BPListElement[0], attributes);
						BPListElement curr = new BPListElement(element, null, IBuildpathEntry.BPE_LIBRARY,
								EnvironmentPathUtils.getFullPath(EnvironmentManager.getLocalEnvironment(),
										new Path(root.getAbsolutePath())),
								null, true);
						element.add(curr);
						ImportZpkWizard.this.element = element;
					} catch (IOException e) {
						throw new InvocationTargetException(e, Messages.ImportZpkWizard_CouldNotGetLibraryParams_Error);
					}
				}
			});
		} catch (InvocationTargetException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return element != null;
	}

	public BPUserLibraryElement getElement() {
		return element;
	}

	private boolean alreadyExist(ImportZpkData zpkData) {
		for (Object object : bpElements) {
			if (!(object instanceof BPUserLibraryElement))
				continue;

			BPUserLibraryElement libraryElement = (BPUserLibraryElement) object;
			if (libraryElement.getName().equals(zpkData.getName()))
				return true;
		}
		return false;
	}

}
