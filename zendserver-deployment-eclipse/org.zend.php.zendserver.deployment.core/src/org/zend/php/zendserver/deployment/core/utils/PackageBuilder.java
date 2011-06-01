package org.zend.php.zendserver.deployment.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IMapping;
import org.zend.php.zendserver.deployment.core.descriptor.ResourceMapper;


public class PackageBuilder {

	private static final String EXTENSION = ".zpk";
	private static final int BUFFER = 1024;

	private ResourceMapper mapper;
	private IProject project;
	private String name;
	private IProgressMonitor monitor;
	private ZipOutputStream out;

	public PackageBuilder(IDescriptorContainer container) {
		this.project = container.getFile().getProject();
		this.mapper = new ResourceMapper(container);
		IDeploymentDescriptor model = container.getDescriptorModel();
		this.name = model.getName() + "-" + model.getReleaseVersion();
	}

	/**
	 * Creates compressed zpk package with a given name.
	 * 
	 * @param name
	 *            - package name
	 * @param monitor
	 *            - progress monitor
	 * @return zpk package file
	 * @throws IOException
	 */
	public File createDeploymentPackage(String name, IProgressMonitor monitor)
			throws IOException, CoreException {
		this.monitor = monitor;
		File result = File.createTempFile(name, EXTENSION);
		out = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(result)));
		monitor.beginTask(Messages.packageTask_description,
				calculateTotalWork());
		addFileToZip(project, null, null);
		Set<IPath> folders = mapper.getFolders();
		for (IPath folder : folders) {
			IMapping[] mappings = mapper.getMappings(folder);
			for (IMapping mapping : mappings) {
				IResource resource = mapper.getResource(mapping.getPath());
				if (resource != null) {
					addFileToZip(resource, folder, mapping);
				}
			}
		}
		out.close();
		monitor.done();
		return result;
	}

	/**
	 * Creates compressed zpk package with default name. Default name is
	 * application_name-version.zpk, e.g. myApplication-1.0.0.zpk.
	 * 
	 * @param monitor
	 *            - progress monitor
	 * @return zpk package file
	 * @throws IOException
	 */
	public File createDeploymentPackage(IProgressMonitor monitor)
			throws IOException, CoreException {
		return createDeploymentPackage(name, monitor);
	}

	private int countFiles(IResource file) throws CoreException {
		int counter = 0;
		if (!checkExclude(file)) {
			if (file instanceof IContainer) {
				IContainer folder = (IContainer) file;
				IResource[] members = folder.members();
				for (IResource member : members) {
					counter += countFiles(member);
				}
			} else {
				counter++;
			}
		}
		return counter;
	}

	private boolean checkExclude(IResource resource) {
		return mapper.isExcluded(resource.getFullPath());
	}

	private void addFileToZip(IResource root, IPath mappedFolder,
			IMapping mapping) throws IOException, CoreException {
		boolean isMapped = mappedFolder == null ? mapper.getFolders().contains(
				root.getProjectRelativePath()) : false;
		if (!checkExclude(root) && !isMapped) {
			if (root instanceof IContainer) {
				IContainer container = (IContainer) root;
				IResource[] members = container.members();
				for (IResource member : members) {
					addFileToZip(member, mappedFolder, mapping);
				}
			} else {
				String location = root.getLocation().toOSString();
				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(location), BUFFER);
				String path = null;
				if (mappedFolder != null && mapping != null) {
					path = root.getFullPath().toOSString();
					int position = 0;
					String rootPath = mapper.getResource(mapping.getPath())
							.getFullPath().toOSString();
					if (mapping.isContent()) {
						position = rootPath.length();
					} else {
						position = rootPath.lastIndexOf(File.separator);
					}
					String destFolder = path.substring(position);
					path = mappedFolder + destFolder;
				} else {
					path = root.getProjectRelativePath().toOSString();
				}
				ZipEntry entry = new ZipEntry(path);
				out.putNextEntry(entry);
				int count;
				byte data[] = new byte[BUFFER];
				while ((count = in.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				in.close();
				monitor.worked(1);
			}
		}
	}

	private int calculateTotalWork() throws CoreException {
		int totalWork = countFiles(project);
		Set<IPath> folders = mapper.getFolders();
		for (IPath folder : folders) {
			IMapping[] mappings = mapper.getMappings(folder);
			for (IMapping mapping : mappings) {
				IResource resource = mapper.getResource(mapping.getPath());
				if (resource != null) {
					totalWork += countFiles(resource);
				}
			}
		}
		return totalWork;
	}

}
