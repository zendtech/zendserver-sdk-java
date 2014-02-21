package org.zend.php.zendserver.deployment.core.descriptor;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

public class DescriptorContainerManagerTest extends TestCase {

	private IProject testProject;

	public void setUp() throws CoreException {
		IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();

		IProgressMonitor mon = new NullProgressMonitor();
		testProject = ws.getProject("example");
		if (testProject.exists()) {
			testProject.delete(true, mon);
		}
		
		testProject.create(mon);
		testProject.open(mon);
	}
	
	public void testOpenNonExistingFile() {
		DescriptorContainerManager service = DescriptorContainerManager.getService();
		
		IFile nonExistingFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("nonExistentProject/nonExistingFile.xml"));
		IDescriptorContainer descr = service.openDescriptorContainer(nonExistingFile);
		
		assertNotNull(descr);
	}

	public void testCreateDescriptorFile() throws CoreException {
		DescriptorContainerManager service = DescriptorContainerManager.getService();
		
		IFile nonExistingFile = testProject.getFile("descriptor.xml");
		assertFalse(nonExistingFile.exists());
		
		IDescriptorContainer descr = service.openDescriptorContainer(nonExistingFile);
		descr.save();
		
		assertTrue(nonExistingFile.exists());
	}
	
	public void testEmptyDescriptorFile() throws CoreException {
		DescriptorContainerManager service = DescriptorContainerManager.getService();
		
		IFile nonExistingFile = testProject.getFile("descriptor.xml");
		assertFalse(nonExistingFile.exists());
		
		IDescriptorContainer descr = service.openDescriptorContainer(nonExistingFile);
		IDeploymentDescriptor model = descr.getDescriptorModel();
		assertEquals(null, model.getName());
		assertEquals(null, model.getDescription());
		assertEquals(null, model.getSummary());
		assertEquals(null, model.getDocumentRoot());
		assertEquals(null, model.getEulaLocation());
		assertEquals(null, model.getApiVersion());
		assertEquals(null, model.getHealthcheck());
		assertEquals(null, model.getIconLocation());
		assertEquals(null, model.getReleaseVersion());
		assertEquals(null, model.getScriptsRoot());
		assertEquals(0, model.getPHPDependencies().size());
		assertEquals(0, model.getDirectiveDependencies().size());
		assertEquals(0, model.getExtensionDependencies().size());
		assertEquals(0, model.getZendServerDependencies().size());
		assertEquals(0, model.getZendFrameworkDependencies().size());
		assertEquals(0, model.getZendComponentDependencies().size());
		assertEquals(0, model.getPersistentResources().size());
		assertEquals(0, model.getParameters().size());
		assertEquals(0, model.getVariables().size());
	}
	
	public void testEditDescriptorFile() throws CoreException {
		DescriptorContainerManager service = DescriptorContainerManager.getService();
		
		IFile nonExistingFile = testProject.getFile("descriptor.xml");
		assertFalse(nonExistingFile.exists());
		
		IDescriptorContainer descr = service.openDescriptorContainer(nonExistingFile);
		IDeploymentDescriptor model = descr.getDescriptorModel();
		model.setName("new name");
		descr.save();
		
		assertTrue(nonExistingFile.exists());
	}
}
