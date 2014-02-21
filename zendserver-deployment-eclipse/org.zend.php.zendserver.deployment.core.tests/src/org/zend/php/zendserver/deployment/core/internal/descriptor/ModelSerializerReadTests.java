package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDirectiveDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IExtensionDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IPHPDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.tests.Activator;


public class ModelSerializerReadTests extends TestCase {

	private IDeploymentDescriptor descr;

	public void setUp() throws CoreException, IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		URL entry = Activator.getInstance().getBundleContext().getBundle().getEntry("example1.xml");
		ModelSerializer lm = new ModelSerializer();
		
		descr = new DeploymentDescriptor();
		lm.load(entry.openStream(), entry.openStream(), descr);
	}
	
	
	public void testBasicInfo() {
		assertEquals("Magento", descr.getName());
		assertEquals("Magento short description", descr.getSummary());
		assertEquals("Magento long description", descr.getDescription());
		assertEquals("data/LICENSE.txt", descr.getEulaLocation());
		assertEquals("", descr.getDocumentRoot());
		assertEquals("", descr.getScriptsRoot());
		assertEquals("1.4.1.1", descr.getReleaseVersion());
	}
	
	public void testVariable0() {
		List<IVariable> vars = descr.getVariables();
		IVariable var = vars.get(0);
		assertEquals("LS", var.getName());
		assertEquals("ls -l", var.getValue());
	}
	
	public void testVariable1() {
		List<IVariable> vars = descr.getVariables();
		IVariable var = vars.get(1);
		assertEquals("UNAME", var.getName());
		assertEquals("uname -a -l", var.getValue());
	}
	
	
	public void testPrereq0() {
		List<IPHPDependency> prereqs = descr.getPHPDependencies();
		IPHPDependency php = prereqs.get(0);
		assertEquals("5.2.13", php.getMin());
	}
	
	
	public void testPrereq1() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(0);
		assertEquals("pdo_mysql", dep.getName());
	}
	
	
	public void testPrereq2() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(1);
		assertEquals("curl", dep.getName());
	}
	
	
	public void testPrereq3() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(2);
		assertEquals("gd", dep.getName());
	}
	
	
	public void testPrereq4() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(3);
		assertEquals("mcrypt", dep.getName());
	}
	
	
	public void testPrereq5() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(4);
		assertEquals("pdo", dep.getName());
	}
	
	
	public void testPrereq6() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(5);
		assertEquals("dom", dep.getName());
	}
	
	
	public void testPrereq7() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(6);
		assertEquals("hash", dep.getName());
	}
	
	
	public void testPrereq8() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(7);
		assertEquals("iconv", dep.getName());
	}
	
	
	public void testPrereq9() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(8);
		assertEquals("pcre", dep.getName());
	}
	
	
	public void testPrereq10() {
		List<IExtensionDependency> prereqs = descr.getExtensionDependencies();
		IExtensionDependency dep = prereqs.get(9);
		assertEquals("simplexml", dep.getName());
	}
	
	
	public void testPrereq11() {
		List<IDirectiveDependency> prereqs = descr.getDirectiveDependencies();
		IDirectiveDependency dep = prereqs.get(0);
		assertEquals("safe_mode", dep.getName());
		assertEquals("off", dep.getEquals());
	}
	
	
	public void testParams0() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(0);
		assertEquals("locale", param.getId());
		assertEquals("Localization.Locale Settings.Locale", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("ID_LOCALE", param.getType());
		assertEquals("", param.getDescription());
	}
	
	
	public void testParams1() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(1);
		assertEquals("timezone", param.getId());
		assertEquals("Localization.Locale Settings.Time Zone", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("ID_TIMEZONE", param.getType());
		assertEquals("", param.getDescription());
	}
	
	
	public void testParams2() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(2);
		assertEquals("currency", param.getId());
		assertEquals("Localization.Locale Settings.Default Currency", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("ID_CURRENCY", param.getType());
		assertEquals("", param.getDescription());
	}
	
	
	public void testParams3() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(3);
		assertEquals("db_host", param.getId());
		assertEquals("Configuration.Database Connection.Host", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals("You can specify server port, ex.: localhost:3307 If you are not using default UNIX socket, you can specify it here instead of host, ex.: /var/run/mysqld/mysqld.sock", param.getDescription());
	}
	
	
	public void testParams4() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(4);
		assertEquals("db_name", param.getId());
		assertEquals("Configuration.Database Connection.Database Name", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals("", param.getDescription());
		assertEquals("magento", param.getDefaultValue());
	}
	
	
	public void testParams5() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(5);
		assertEquals("db_username", param.getId());
		assertEquals("Configuration.Database Connection.User Name", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals("", param.getDescription());
		assertEquals("root", param.getDefaultValue());
	}
	
	
	public void testParams6() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(6);
		assertEquals("db_password", param.getId());
		assertEquals("Configuration.Database Connection.User Password", param.getDisplay());
		assertFalse(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals("", param.getDescription());
		assertEquals("", param.getDefaultValue());
	}
	
	
	public void testParams7() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(7);
		assertEquals("db_tables_prefix", param.getId());
		assertEquals("Configuration.Database Connection.Tables Prefix", param.getDisplay());
		assertFalse(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals("(Optional. Leave blank for no prefix)", param.getDescription());
		assertEquals("", param.getDefaultValue());
	}
	
	
	public void testParams8() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(8);
		assertEquals("base_url", param.getId());
		assertEquals("Configuration.Web access options.Base URL", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals("", param.getDescription());
		assertEquals("$(WEBSERVER_SCHEMA)://$(WEBSERVER_HOSTNAME)/$(WEBSERVER_PATH)", param.getDefaultValue());
	}
	
	
	public void testParams9() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(9);
		assertEquals("admin_path", param.getId());
		assertEquals("Configuration.Web access options.Admin Path", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals("Additional path added after Base URL to access your Administrative Panel (e.g. admin, backend, control etc.)", param.getDescription());
		assertEquals("admin", param.getDefaultValue());
	}
	
	
	public void testParams10() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(10);
		assertEquals("skip_base", param.getId());
		assertEquals("Configuration.Web access options.Skip Base URL Validation Before the Next Step", param.getDisplay());
		assertFalse(param.isRequired());
		assertEquals("checkbox", param.getType());
		assertEquals("Check this box only if it is not possible to automatically validate the Base URL.", param.getDescription());
		assertEquals("false", param.getDefaultValue());
	}
	
	
	public void testParams11() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(11);
		assertEquals("use_apache_rewrites", param.getId());
		assertEquals("Configuration.Web access options.Skip Base URL Validation Before the Next Step", param.getDisplay());
		assertFalse(param.isRequired());
		assertEquals("checkbox", param.getType());
		assertEquals("You could enable this option to use web server rewrites functionality for improved search engines optimization. Please make sure that mod_rewrite is enabled in Apache configuration.", param.getDescription());
		assertEquals("false", param.getDefaultValue());
	}
	
	
	public void testParams12() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(12);
		assertEquals("use_ssl", param.getId());
		assertEquals("Configuration.Web access options.Use Secure URLs (SSL)", param.getDisplay());
		assertFalse(param.isRequired());
		assertEquals("checkbox", param.getType());
		assertEquals("false", param.getDefaultValue());
		assertEquals("Enable this option only if you have SSL available.", param.getDescription());
	}
	
	
	public void testParams13() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(13);
		assertEquals("session_save_data", param.getId());
		assertEquals("Configuration.Session Storage Options.Save Session Data In", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("choice", param.getType());
		assertEquals("File System", param.getDefaultValue());
		assertEquals("", param.getDescription());
		assertEquals(Arrays.asList(new String[] {"File System", "Database"}), param.getValidValues());
	}
	
	
	public void testParams14() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(14);
		assertEquals("first_name", param.getId());
		assertEquals("Create Admin Account.Personal Information.First Name", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals(null, param.getDefaultValue());
		assertEquals("", param.getDescription());
	}
	
	
	public void testParams15() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(15);
		assertEquals("last_name", param.getId());
		assertEquals("Create Admin Account.Personal Information.Last Name", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals(null, param.getDefaultValue());
		assertEquals("", param.getDescription());
	}
	
	
	public void testParams16() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(16);
		assertEquals("email", param.getId());
		assertEquals("Create Admin Account.Personal Information.Email", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("email", param.getType());
		assertEquals(null, param.getDefaultValue());
		assertEquals("", param.getDescription());
	}
	
	
	public void testParams17() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(17);
		assertEquals("login_username", param.getId());
		assertEquals("Create Admin Account.Login Information.Username", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals(null, param.getDefaultValue());
		assertEquals("", param.getDescription());
	}
	
	
	public void testParams18() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(18);
		assertEquals("login_password", param.getId());
		assertEquals("Create Admin Account.Login Information.Password", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("password", param.getType());
		assertEquals(null, param.getDefaultValue());
		assertEquals("", param.getDescription());
	}
	
	
	public void testParams19() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(19);
		assertEquals("login_confirm_password", param.getId());
		assertEquals("Create Admin Account.Login Information.Confirm Password", param.getDisplay());
		assertTrue(param.isRequired());
		assertEquals("password", param.getType());
		assertEquals(null, param.getDefaultValue());
		assertEquals("", param.getDescription());
	}
	
	
	public void testParams20() {
		List<IParameter> params = descr.getParameters();
		IParameter param = params.get(20);
		assertEquals("encription_key", param.getId());
		assertEquals("Create Admin Account.Encription Key", param.getDisplay());
		assertFalse(param.isRequired());
		assertEquals("string", param.getType());
		assertEquals(null, param.getDefaultValue());
		assertEquals("", param.getDescription());
	}
	

}
