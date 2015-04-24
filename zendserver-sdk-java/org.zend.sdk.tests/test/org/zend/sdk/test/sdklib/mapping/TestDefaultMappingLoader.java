package org.zend.sdk.test.sdklib.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.zend.sdklib.internal.mapping.DefaultMappingLoader;
import org.zend.sdklib.mapping.IMappingEntry;

public class TestDefaultMappingLoader {

	private static final String FOLDER = "test/config/apps/";

	@Test
	public void testDefaultMappingLoader() throws FileNotFoundException,
			IOException {
		DefaultMappingLoader loader = new DefaultMappingLoader();
		File mappingFile = new File(FOLDER, "duplicated_keys.properties");
		List<IMappingEntry> result = loader.load(new FileInputStream(
				mappingFile));
		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testDefaultMappingLoaderMultiLines()
			throws FileNotFoundException,
			IOException {
		DefaultMappingLoader loader = new DefaultMappingLoader();
		File mappingFile = new File(FOLDER, "multi_lines.properties");
		List<IMappingEntry> result = loader.load(new FileInputStream(
				mappingFile));
		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test(expected = FileNotFoundException.class)
	public void testDefaultMappingLoaderFileNotFound()
			throws FileNotFoundException, IOException {
		DefaultMappingLoader loader = new DefaultMappingLoader();
		File mappingFile = new File(FOLDER, "does_not_exists");
		loader.load(new FileInputStream(mappingFile));
	}

}
