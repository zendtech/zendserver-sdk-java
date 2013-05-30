package org.zend.php.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.ui.ElementQueryDescriptor;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.internal.p2.ui.ProvUIMessages;
import org.eclipse.equinox.internal.p2.ui.dialogs.ApplyProfileChangesDialog;
import org.eclipse.equinox.internal.p2.ui.model.InstalledIUElement;
import org.eclipse.equinox.internal.p2.ui.model.ProfileElement;
import org.eclipse.equinox.internal.p2.ui.query.InstalledIUElementWrapper;
import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.RepositoryTracker;
import org.eclipse.equinox.p2.query.Collector;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.Policy;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

public class ProfileModificationHelper {

	private static final String LISTENERS_EXT_POINT = "org.zend.php.customization.profileModificationListeners";

	private List<IInstallableUnit> toInstall;
	private List<IInstallableUnit> toUninstall;

	private static List<IProfileModificationListener> listeners;

	private static StatusHandler customStatusHandler;

	public static interface Callback {
		void runCallback();
	}

	public IStatus modify(IProgressMonitor monitor,
			final Collection<CatalogItem> toAdd,
			final Collection<CatalogItem> toRemove, final int restartPolicy) {
		return modify(monitor, toAdd, toRemove, restartPolicy, "");
	}

	public IStatus modify(IProgressMonitor monitor,
			final Collection<CatalogItem> toAdd,
			final Collection<CatalogItem> toRemove, final int restartPolicy,
			String jobName) {

		Set<String> setToAdd = getDescriptorIds(toAdd);
		Set<String> setToRemove = getDescriptorIds(toRemove);

		List<IProfileModificationListener> listeners = getModificationListeners();
		for (IProfileModificationListener listener : listeners) {
			IStatus status = listener.aboutToChange(setToAdd, setToRemove);
			if ((status != null) && (status.getSeverity() == IStatus.CANCEL)) {
				return status;
			}
		}

		IStatus status = modify(monitor, setToAdd, setToRemove, restartPolicy,
				jobName);

		for (IProfileModificationListener listener : listeners) {
			listener.profileChanged(setToAdd, setToRemove, status);
		}
		return status;
	}

	public static boolean isFeatureInstalled(Object pluginid) {
		ProvisioningUI pui = ProvisioningUI.getDefaultUI();

		ProfileElement element = new ProfileElement(null, pui.getProfileId());

		IProfileRegistry pregistry = ProvUI
				.getProfileRegistry(pui.getSession());
		if (pregistry == null) { // for developers
			return true;
		}

		IProfile profile = pregistry.getProfile(pui.getProfileId());

		if (profile == null) { // for developers
			return true;
		}

		IQuery<IInstallableUnit> query = QueryUtil.createIUAnyQuery(); 
		ElementQueryDescriptor queryDescriptor = new ElementQueryDescriptor(
				profile, query,
				new Collector<IInstallableUnit>(),
				new InstalledIUElementWrapper(profile, element));

		if (queryDescriptor != null) {
			Collection<?> results = queryDescriptor
					.performQuery(new NullProgressMonitor());
			for (Object item : results) {
				if (item instanceof InstalledIUElement) {
					if (((InstalledIUElement) item).getIU().getId()
							.equals(pluginid)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param toAdd
	 *            - ids of InstallableUnit
	 * @param toRemove
	 *            - ids of InstallableUnit
	 * @param restartPolicy
	 */
	public IStatus modify(IProgressMonitor monitor, final Set<String> toAdd,
			final Set<String> toRemove, final int restartPolicy, String jobName) {
		try {
			if ((toAdd == null || toAdd.isEmpty())
					&& (toRemove == null || toRemove.isEmpty())) {
				return Status.CANCEL_STATUS;
			}
			ProvisioningSession session = ProvisioningUI.getDefaultUI()
					.getSession();
			if (null != toAdd && !toAdd.isEmpty()) {
				toInstall = queryRepoInstallableUnits(toAdd);
			}
			if (null != toRemove && !toRemove.isEmpty()) {
				toUninstall = queryProfileInstallableUnits(toRemove);
			}
			ZendProfileChangeOperation op = new ZendProfileChangeOperation(
					session, toInstall, toUninstall, jobName);
			IStatus result = op.resolveModal(monitor);
			if (result.getSeverity() < IStatus.ERROR) {
				if (result.isMultiStatus()) {
					handle(result, StatusManager.SHOW);
				}
				result = op.getProvisioningJob(monitor).runModal(monitor);
			}
			if (result.getSeverity() < IStatus.ERROR) {
				requestRestart(restartPolicy);
			}
			return result;

		} catch (URISyntaxException e) {
			return handleException(e);
		} catch (MalformedURLException e) {
			return handleException(e);
		} catch (ProvisionException e) {
			return handleException(e);
		}
	}

	private void handle(IStatus result, int show) {
		if (customStatusHandler != null) {
			customStatusHandler.show(result, result.getMessage());
		} else {
			StatusManager.getManager().handle(result, StatusManager.SHOW);
		}
	}

	private void applyProfileChanges() {
		Configurator configurator = (Configurator) ServiceHelper.getService(
				ProvUIActivator.getContext(), Configurator.class.getName());
		try {
			configurator.applyConfiguration();
		} catch (IOException e) {
			ProvUI.handleException(e,
					ProvUIMessages.ProvUI_ErrorDuringApplyConfig,
					StatusManager.LOG | StatusManager.BLOCK);
		} catch (IllegalStateException e) {
			IStatus illegalApplyStatus = new Status(
					IStatus.WARNING,
					ProvUIActivator.PLUGIN_ID,
					0,
					ProvUIMessages.ProvisioningOperationRunner_CannotApplyChanges,
					e);
			ProvUI.reportStatus(illegalApplyStatus, StatusManager.LOG
					| StatusManager.BLOCK);
		}
	}

	public void requestRestart(final int restartPolicy, final Callback callback) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				boolean restart = false;
				if (restartPolicy == Policy.RESTART_POLICY_FORCE) {
					PlatformUI.getWorkbench().restart();
					restart = true;
				} else if (restartPolicy == Policy.RESTART_POLICY_FORCE_APPLY) {
					applyProfileChanges();
					return;
				} else if (PlatformUI.getWorkbench().isClosing()) {
					restart = true;
					return;
				} else {
					int retCode = ApplyProfileChangesDialog.promptForRestart(
							ProvUI.getDefaultParentShell(),
							restartPolicy == Policy.RESTART_POLICY_PROMPT);
					if (retCode == ApplyProfileChangesDialog.PROFILE_APPLYCHANGES) {
						applyProfileChanges();
					} else if (retCode == ApplyProfileChangesDialog.PROFILE_RESTART) {
						restart = true;
						PlatformUI.getWorkbench().restart();
					}
				}
				if (!restart && callback != null) {
					callback.runCallback();
				}
			}
		});
	}

	public void requestRestart(final int restartPolicy) {
		requestRestart(restartPolicy, null);
	}

	private IStatus handleException(Exception e) {
		Activator.log(e);
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage());
	}

	static public Set<String> getDescriptorIds(
			Collection<CatalogItem> toAddItems) {
		Set<String> installableUnits = new HashSet<String>();
		for (Object connector : toAddItems) {
			if (connector instanceof CatalogItem) {
				installableUnits.addAll(((CatalogItem) connector)
						.getInstallableUnits());
			}
		}
		return installableUnits;
	}

	static private IMetadataRepository loadRepository(URI repo)
			throws MalformedURLException, URISyntaxException,
			ProvisionException {
		ProvisioningSession session = ProvisioningUI.getDefaultUI()
				.getSession();
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) session
				.getProvisioningAgent().getService(
						IMetadataRepositoryManager.SERVICE_NAME);
		return manager.loadRepository(repo, null);
	}

	static private List<IInstallableUnit> queryRepoInstallableUnits(
			Set<String> installableUnitIds) throws URISyntaxException,
			MalformedURLException, ProvisionException {

		List<String> toFind = new ArrayList<String>(installableUnitIds);

		URI[] repos = getRepositories();
		IQueryResult<IInstallableUnit> allIUs = null;
		List<IInstallableUnit> result = new ArrayList<IInstallableUnit>();
		for (URI repo : repos) {
			IMetadataRepository repository = loadRepository(repo);
			allIUs = repository.query(QueryUtil.createIUGroupQuery(), null);

			getIUsFromQueryResult(toFind, allIUs, result);
			if (toFind.isEmpty()) {
				break;
			}
		}
		return result;
	}

	static public List<IInstallableUnit> queryProfileInstallableUnits(
			Set<String> installableUnitIds) throws URISyntaxException,
			MalformedURLException, ProvisionException {
		IProfile profile = ProvUI.getProfileRegistry(
				ProvisioningUI.getDefaultUI().getSession()).getProfile(
				IProfileRegistry.SELF);
		IQueryResult<IInstallableUnit> queryresult = profile.query(
				QueryUtil.createIUGroupQuery(), null);

		List<IInstallableUnit> result = new ArrayList<IInstallableUnit>();
		getIUsFromQueryResult(new ArrayList(installableUnitIds), queryresult,
				result);
		return result;
	}

	private static List<IInstallableUnit> getIUsFromQueryResult(
			List<String> installableUnitIds,
			IQueryResult<IInstallableUnit> queryResult,
			List<IInstallableUnit> installableUnits) {
		for (Iterator<IInstallableUnit> iter = queryResult.iterator(); iter
				.hasNext();) {
			IInstallableUnit iu = iter.next();
			String id = iu.getId();
			if (installableUnitIds.contains(id)) {
				installableUnitIds.remove(id);
				installableUnits.add(iu);
			}

			if (installableUnitIds.contains(id.concat(".feature.group"))) {
				installableUnitIds.remove(id);
				installableUnits.add(iu);
			}
		}
		return installableUnits;
	}

	private static URI[] getRepositories() {
		URI location = null;
		RepositoryTracker repositoryTracker = ProvisioningUI.getDefaultUI()
				.getRepositoryTracker();
		ProvisioningSession session = ProvisioningUI.getDefaultUI()
				.getSession();
		if (repositoryTracker == null || session == null) {
			Activator
					.logErrorMessage("RepositoryTracker or ProvisioningSession was null.");
			return null;
		}

		URI[] uris = null;
		try {
			uris = Customization.getSiteUris();
		} catch (URISyntaxException e) {
			Activator.log(e);
		}

		if (uris == null || uris.length == 0) {
			uris = repositoryTracker.getKnownRepositories(session);
		}

		for (URI uri : uris) {
			repositoryTracker.addRepository(uri, null, session);
		}

		return uris;
	}

	public static URI getExtraRepository() {
		URI[] repos = getRepositories();
		if (repos == null) {
			return null;
		}
		for (URI uri : repos) {
			if (isZendExtraFeatures(uri)) {
				return uri;
			}
		}

		return null;
	}

	private static boolean isZendExtraFeatures(URI uri) {
		// TODO how to recognize Zend Extra Features Update Site???
		return uri.toString().contains("extra");
	}

	private List<IProfileModificationListener> getModificationListeners() {
		if (listeners == null) {
			List<IProfileModificationListener> result = new ArrayList<IProfileModificationListener>();
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(LISTENERS_EXT_POINT);
			for (IConfigurationElement element : elements) {
				if ("modificationListener".equals(element.getName())) { //$NON-NLS-1$
					try {
						Object listener = element
								.createExecutableExtension("class"); //$NON-NLS-1$
						if (listener instanceof IProfileModificationListener) {
							result.add((IProfileModificationListener) listener);
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
			listeners = result;
		}
		return listeners;
	}

	private static List<String> getIds(List<CatalogItem> items) {
		List<String> result = new ArrayList<String>();
		for (CatalogItem item : items) {
			result.add(item.getId());
		}
		return result;
	}

	public static void setStatusHandler(StatusHandler handler) {
		customStatusHandler = handler;
	}
}
