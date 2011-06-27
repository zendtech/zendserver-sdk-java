package org.zend.sdk.test.sdklib.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;
import org.zend.sdklib.internal.mapping.DefaultMappingLoader;
import org.zend.sdklib.internal.mapping.Mapping;
import org.zend.sdklib.mapping.IMapping;
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
	public void testModelAdd() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		int size = model.getInclusion(folder).size();
		model.addInclude(folder, new Mapping("test1", true, false));
		assertEquals(size + 1, model.getInclusion(folder).size());
		size = model.getExclusion(folder).size();
		model.addExclude(folder, new Mapping("test1", true, false));
		assertEquals(size + 1, model.getExclusion(folder).size());
	}

	@Test
	public void testModelAddNullFolder() throws IOException {
		IMappingModel model = getModel();
		assertFalse(model.addInclude(null, new Mapping("test1", true, false)));
		assertFalse(model.addExclude(null, new Mapping("test1", true, false)));
	}

	@Test
	public void testModelAddNullMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertFalse(model.addInclude(folder, null));
		assertFalse(model.addExclude(folder, null));
	}

	@Test
	public void testModelRemove() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		int size = model.getInclusion(folder).size();
		model.addInclude(folder, new Mapping("test1", true, false));
		assertEquals(size + 1, model.getInclusion(folder).size());
		model.removeInclude(folder, "test1");
		assertEquals(size, model.getInclusion(folder).size());

		size = model.getExclusion(folder).size();
		model.addExclude(folder, new Mapping("test1", true, false));
		assertEquals(size + 1, model.getExclusion(folder).size());
		model.removeExclude(folder, "test1");
		assertEquals(size, model.getExclusion(folder).size());
	}

	@Test
	public void testModelRemoveNullFolder() throws IOException {
		IMappingModel model = getModel();
		assertFalse(model.removeInclude(null, "test1"));
		assertFalse(model.removeExclude(null, "test1"));
	}

	@Test
	public void testModelRemoveNullMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertFalse(model.removeInclude(folder, null));
		assertFalse(model.removeExclude(folder, null));
	}

	@Test
	public void testModelModify() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		int size = model.getInclusion(folder).size();
		model.addInclude(folder, new Mapping("test1", true, false));
		assertEquals(size + 1, model.getInclusion(folder).size());

		model.modifyInclude(folder, new Mapping("test1", false, false));
		Set<IMapping> includes = model.getInclusion(folder);
		for (IMapping mapping : includes) {
			if (mapping.getPath().equals("test1")) {
				assertFalse(mapping.isContent());
			}
		}

		size = model.getExclusion(folder).size();
		model.addExclude(folder, new Mapping("test1", true, false));
		assertEquals(size + 1, model.getExclusion(folder).size());
		model.modifyExclude(folder, new Mapping("test1", false, false));
		Set<IMapping> excludes = model.getExclusion(folder);
		for (IMapping mapping : excludes) {
			if (mapping.getPath().equals("test1")) {
				assertFalse(mapping.isContent());
			}
		}
	}

	@Test
	public void testModelModifyNullFolder() throws IOException {
		IMappingModel model = getModel();
		assertFalse(model
				.modifyInclude(null, new Mapping("test1", true, false)));
		assertFalse(model
				.modifyExclude(null, new Mapping("test1", true, false)));
	}

	@Test
	public void testModelModifyNullMapping() throws IOException {
		IMappingModel model = getModel();
		String folder = "data";
		assertFalse(model.modifyInclude(folder, null));
		assertFalse(model.modifyExclude(folder, null));
	}

	private IMappingModel getModel() throws IOException {
		IMappingModel model = MappingModelFactory.createDefaultModel(new File(
				FOLDER));
		assertNotNull(model);
		return model;
	}

}
