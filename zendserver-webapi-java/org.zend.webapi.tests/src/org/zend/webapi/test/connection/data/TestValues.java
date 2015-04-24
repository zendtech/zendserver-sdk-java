package org.zend.webapi.test.connection.data;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;
import org.zend.webapi.core.connection.data.values.LicenseInfoStatus;
import org.zend.webapi.core.connection.data.values.ServerStatus;
import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.core.connection.data.values.SystemStatus;
import org.zend.webapi.core.connection.data.values.WebApiVersion;

public class TestValues {

	@Test
	public void testServerStatus() {
		ServerStatus status = ServerStatus.OK;
		Assert.assertEquals(ServerStatus.OK,
				ServerStatus.byName(status.getName()));
		Assert.assertEquals(ServerStatus.UNKNOWN, ServerStatus.byName(null));
		Assert.assertEquals(ServerStatus.UNKNOWN,
				ServerStatus.byName(String.valueOf(Math.random() * 100000)));
	}

	@Test
	public void testLicenseInfoStatus() {
		LicenseInfoStatus status = LicenseInfoStatus.OK;
		Assert.assertEquals(LicenseInfoStatus.OK,
				LicenseInfoStatus.byName(status.getName()));
		Assert.assertEquals(LicenseInfoStatus.UNKNOWN,
				LicenseInfoStatus.byName(null));
		Assert.assertEquals(LicenseInfoStatus.UNKNOWN, LicenseInfoStatus
				.byName(String.valueOf(Math.random() * 100000)));
		Assert.assertNotNull(status.getDescription());
	}

	@Test
	public void testSystemEdition() {
		SystemEdition edition = SystemEdition.byName("ZendServer");
		Assert.assertEquals(SystemEdition.ZEND_SERVER, edition);
		edition = SystemEdition.byName(String.valueOf(Math.random() * 100000));
		Assert.assertEquals(SystemEdition.UNKNOWN, edition);
		edition = SystemEdition.byName(null);
		Assert.assertEquals(SystemEdition.UNKNOWN, edition);
	}

	@Test
	public void testSystemStatus() {
		SystemStatus status = SystemStatus.OK;
		Assert.assertEquals(SystemStatus.OK,
				SystemStatus.byName(status.getTitle()));
		Assert.assertEquals(SystemStatus.UNKNOWN, SystemStatus.byName(null));
		Assert.assertEquals(SystemStatus.OK.getDescription(),
				status.getDescription());
		Assert.assertEquals(SystemStatus.UNKNOWN,
				SystemStatus.byName(String.valueOf(Math.random() * 100000)));
	}

	@Test
	public void testWebApiVersion() {
		WebApiVersion version = WebApiVersion.V1;
		Assert.assertEquals(WebApiVersion.V1,
				WebApiVersion.byFullName(version.getFullName()));
		Assert.assertEquals(WebApiVersion.UNKNOWN,
				WebApiVersion.byFullName(null));
		Assert.assertEquals(WebApiVersion.V1.getVersionName(),
				version.getVersionName());
		Assert.assertEquals(WebApiVersion.V1.getFullName(),
				version.getFullName());
		Assert.assertEquals(WebApiVersion.UNKNOWN, WebApiVersion
				.byFullName(String.valueOf(Math.random() * 100000)));
	}

	@Test
	public void testApplicationStatus() {
		ApplicationStatus status = ApplicationStatus.DEPLOYED;
		Assert.assertEquals(ApplicationStatus.DEPLOYED,
				ApplicationStatus.byName(status.getName()));
		Assert.assertEquals(ApplicationStatus.UNKNOWN,
				ApplicationStatus.byName(null));
		Assert.assertEquals(ApplicationStatus.UNKNOWN, ApplicationStatus
				.byName(String.valueOf(Math.random() * 100000)));
	}

}
