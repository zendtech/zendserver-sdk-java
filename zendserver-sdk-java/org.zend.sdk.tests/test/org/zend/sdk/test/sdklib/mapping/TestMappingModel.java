package org.zend.sdk.test.sdklib.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.zend.sdklib.internal.mapping.DefaultMappingLoader;
import org.zend.sdklib.internal.mapping.Mapping;
import org.zend.sdklib.internal.mapping.MappingEntry;
import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingEntry.Type;
import org.zend.sdklib.mapping.IMappingModel;
import org.zend.sdklib.mapping.MappingModelFactory;

public class TestMappingModel {

	private static final String FOLDER = "test/config/apps/";

	@Test
	public void testModelCreation() throws IOException {
		IMappingModel model = MappingModelFactory.createDefaultModel(new File(
				FOLDER));
		assertNotNull(model);
		model = MappingModelFactory.createModel(new DefaultMappingLoader(),
				new File(FOLDER));
		assertNotNull(model);
	}

	@Test
	public void testModelCreationFileNotExist() throws IOException {
		IMappingModel model = MappingModelFactory.createDefaultModel(new File(
				FOLDER + "not_exists"));
		assertNull(model);
		model = MappingModelFactory.createModel(new DefaultMappingLoader(),
				new File(FOLDER + "not_exists"));
		assertNull(model);
	}

	@Test
	public void testGetEntry() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertNotNull(model.getEntry(folder, Type.INCLUDE));
	}

	@Test
	public void testGetEntryNotExist() throws IOException {
		IMappingModel model = getModel();
		String folder = "test";
		assertNull(model.getEntry(folder, Type.INCLUDE));
	}

	@Test
	public void testAddEntry() throws IOException {
		IMappingModel model = getModel();
		String folder = "test";
		int size = model.getEnties().size();
		assertTrue(model.addEntry(new MappingEntry(folder,
				new ArrayList<IMapping>(), Type.INCLUDE)));
		assertEquals(size + 1, model.getEnties().size());
	}

	@Test
	public void testAddEntryNull() throws IOException {
		IMappingModel model = getModel();
		assertFalse(model.addEntry(null));
	}

	@Test
	public void testAddEntryDuplicate() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		int size = model.getEnties().size();
		assertFalse(model.addEntry(new MappingEntry(folder,
				new ArrayList<IMapping>(), Type.INCLUDE)));
		assertEquals(size, model.getEnties().size());
	}

	@Test
	public void testRemoveEntry() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		int size = model.getEnties().size();
		assertTrue(model.removeEntry(folder, Type.INCLUDE));
		assertEquals(size - 1, model.getEnties().size());
	}

	@Test
	public void testRemoveEntryNull() throws IOException {
		IMappingModel model = getModel();
		int size = model.getEnties().size();
		assertFalse(model.removeEntry(null, Type.INCLUDE));
		assertEquals(size, model.getEnties().size());
	}

	@Test
	public void testRemoveEntryNotInModel() throws IOException {
		IMappingModel model = getModel();
		String folder = "test";
		int size = model.getEnties().size();
		assertFalse(model.removeEntry(folder, Type.INCLUDE));
		assertEquals(size, model.getEnties().size());
	}

	@Test
	public void testAddMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		int size = getSize(model, folder, Type.INCLUDE);
		assertTrue(model.addMapping(folder, Type.INCLUDE, "test1", false));
		assertEquals(size + 1, getSize(model, folder, Type.INCLUDE));
	}

	@Test
	public void testAddMappingDuplicate() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		int size = getSize(model, folder, Type.INCLUDE);
		assertFalse(model.addMapping(folder, Type.INCLUDE, "public", false));
		assertEquals(size, getSize(model, folder, Type.INCLUDE));
	}

	@Test
	public void testAddMappingNoEntry() throws IOException {
		IMappingModel model = getModel();
		String folder = "test";
		assertTrue(model
.addMapping(folder, Type.INCLUDE, "public", false));
		assertEquals(1, getSize(model, folder, Type.INCLUDE));
	}

	@Test
	public void testAddMappingNullFolder() throws IOException {
		IMappingModel model = getModel();
		assertFalse(model.addMapping(null, Type.INCLUDE, "test1", false));
	}

	@Test
	public void testAddMappingNullMapping() throws IOException {
		IMappingModel model = getModel();
		assertFalse(model.addMapping(null, Type.INCLUDE, null, true));
	}

	@Test
	public void testRemoveMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		int size = getSize(model, folder, Type.INCLUDE);
		assertTrue(model.addMapping(folder, Type.INCLUDE, "test1", false));
		assertEquals(size + 1, getSize(model, folder, Type.INCLUDE));
		assertTrue(model.removeMapping(folder, Type.INCLUDE, "test1"));
		assertEquals(size, getSize(model, folder, Type.INCLUDE));
	}

	@Test
	public void testRemoveMappingNoMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertFalse(model.removeMapping(folder, Type.INCLUDE, "test1"));
	}

	@Test
	public void testRemoveMappingNullFolder() throws IOException {
		IMappingModel model = getModel();
		assertFalse(model.removeMapping(null, Type.INCLUDE, "test1"));
	}

	@Test
	public void testRemoveMappingNullMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertFalse(model.removeMapping(folder, Type.INCLUDE, null));
	}

	@Test
	public void testModifyMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		int size = getSize(model, folder, Type.INCLUDE);
		assertTrue(model.addMapping(folder, Type.INCLUDE, "test1", false));
		assertEquals(size + 1, getSize(model, folder, Type.INCLUDE));
		assertTrue(model.modifyMapping(folder, Type.INCLUDE, new Mapping("test1", false)));
		List<IMapping> includes = model.getEntry(folder, Type.INCLUDE).getMappings();
		for (IMapping mapping : includes) {
			if (mapping.getPath().equals("test1")) {
				assertFalse(mapping.isGlobal());
			}
		}
	}

	@Test
	public void testModifyMappingNoMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertFalse(model.modifyMapping(folder, Type.INCLUDE, new Mapping("aaa", true)));
	}

	@Test
	public void testModifyMappingNullFolder() throws IOException {
		IMappingModel model = getModel();
		assertFalse(model.modifyMapping(null, Type.INCLUDE, new Mapping("test1", false)));
	}

	@Test
	public void testModifyMappingNullMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertFalse(model.modifyMapping(folder, Type.INCLUDE, null));
	}

	@Test
	public void testIsExcluded() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertTrue(model.isExcluded(folder, "test\\.svn"));
	}

	@Test
	public void testIsExcludedNullPath() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertFalse(model.isExcluded(folder, null));
	}

	@Test
	public void testGetFolders() throws IOException {
		IMappingModel model = getModel();
		assertEquals(2, model.getFolders().size());
	}

	@Test
	public void testGetFolder() throws IOException {
		IMappingModel model = getModel();
		String folder = "public";
		String[] folders = model.getFolders(folder);
		assertEquals(1, folders.length);
		assertEquals("data", folders[0]);
	}

	@Test
	public void testGetFolderIsContent() throws IOException {
		IMappingModel model = getModel();
		String folder = "public/abc";
		String[] folders = model.getFolders(folder);
		assertEquals(1, folders.length);
		assertEquals("data", folders[0]);
	}

	@Test
	public void testGetFolderDouble() throws IOException {
		IMappingModel model = getModel();
		String folder = "public/double";
		String[] folders = model.getFolders(folder);
		assertEquals(2, folders.length);
	}

	@Test
	public void testGetPath() throws IOException {
		IMappingModel model = MappingModelFactory.createDefaultModel(new File(FOLDER, "Project1"));
		assertNotNull(model.getPath("inner_public"));
	}

	@Test
	public void testGetPackagePath() throws IOException {
		IMappingModel model = MappingModelFactory.createDefaultModel(new File(FOLDER, "Project1"));
		assertTrue(model.getPackagePath(IMappingModel.APPDIR, "public//inner_public").startsWith(
				"appdir"));
	}
	
	@Test
	public void testGetPackagePathLongPath() throws IOException {
		IMappingModel model = MappingModelFactory.createDefaultModel(new File(FOLDER, "Project1"));
		assertTrue(model.getPackagePath(IMappingModel.APPDIR, "public//a//inner_public").startsWith(
				"appdir"));
	}
	
	@Test
	public void testGetPackagePathInvalid() throws IOException {
		IMappingModel model = MappingModelFactory.createDefaultModel(new File(FOLDER, "Project1"));
		assertNull(model.getPackagePath(IMappingModel.APPDIR, "c//public//inner_public"));
	}

	private int getSize(IMappingModel model, String folder, Type type) {
		return model.getEntry(folder, type).getMappings().size();
	}

	private IMappingModel getModel() throws IOException {
		IMappingModel model = MappingModelFactory.createDefaultModel(new File(
				FOLDER));
		assertNotNull(model);
		return model;
	}

}
