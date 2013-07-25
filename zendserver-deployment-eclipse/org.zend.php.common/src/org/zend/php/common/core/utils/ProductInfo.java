package org.zend.php.common.core.utils;

import java.util.Collection;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.internal.p2.ui.ElementQueryDescriptor;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.internal.p2.ui.model.InstalledIUElement;
import org.eclipse.equinox.internal.p2.ui.model.ProfileElement;
import org.eclipse.equinox.internal.p2.ui.query.InstalledIUElementWrapper;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.ILicense;
import org.eclipse.equinox.p2.query.Collector;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.osgi.framework.Version;

public class ProductInfo {
	public static Version getVersion(String productId) {
		Collection<?> results = getIU(productId);
		for (Object item : results) {
			if (item instanceof InstalledIUElement) {
				return Version.parseVersion(((InstalledIUElement) item).getIU()
						.getVersion().toString());
			}
		}
		return Platform.getProduct().getDefiningBundle().getVersion();
	}

	public static String getLicense(String productId) {
		Collection<?> results = getIU(productId);
		for (Object item : results) {
			if (item instanceof InstalledIUElement) {
				Collection<ILicense> licenses = ((InstalledIUElement) item)
						.getIU().getLicenses();
				for (ILicense iLicense : licenses) {
					// There is only 1 license for product
					return iLicense.toString();
				}
			}
		}
		return "";
	}

	private static Collection<?> getIU(String id) {
		ProvisioningUI pui = ProvisioningUI.getDefaultUI();

		ProfileElement element = new ProfileElement(null, pui.getProfileId());

		IProfileRegistry pregistry = ProvUI
				.getProfileRegistry(pui.getSession());
		if (pregistry == null) { // for developers
			return null;
		}

		IProfile profile = pregistry.getProfile(pui.getProfileId());

		if (profile == null) { // for developers
			return null;
		}

		IQuery<IInstallableUnit> query = QueryUtil.createIUQuery(id);

		ElementQueryDescriptor queryDescriptor = new ElementQueryDescriptor(
				profile, query, new Collector<IInstallableUnit>(),
				new InstalledIUElementWrapper(profile, element));

		return queryDescriptor.performQuery(new NullProgressMonitor());
	}
}
