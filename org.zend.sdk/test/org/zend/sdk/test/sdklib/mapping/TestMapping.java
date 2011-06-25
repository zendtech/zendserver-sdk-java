package org.zend.sdk.test.sdklib.mapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;
import org.zend.sdklib.internal.mapping.ResourceMappingParser;
import org.zend.sdklib.mapping.IResourceMapping;
import org.zend.sdklib.mapping.ResourceMapper;

public class TestMapping {

	private static final String FOLDER = "test/config/apps/";

	@Test
	public void testParser() throws IOException {
		ResourceMappingParser parser = new ResourceMappingParser();
		IResourceMapping mapping = parser.load(new File(FOLDER));
		assertTrue(mapping.getExclusion().size() == 1);
		assertTrue(mapping.getInclusion().size() == 1);
		assertTrue(mapping.getExclusion().get("data").size() == 2);
		assertTrue(mapping.getInclusion().get("data").size() == 2);
	}

	@Test
	public void testParserFileNotExist() throws IOException {
		ResourceMappingParser parser = new ResourceMappingParser();
		IResourceMapping mapping = parser.load(new File(FOLDER + File.separator
				+ new Random().nextInt()));
	}

	@Test
	public void testMapper() throws IOException {
		ResourceMappingParser parser = new ResourceMappingParser();
		ResourceMapper mapper = new ResourceMapper(new File(FOLDER),
				parser.load(new File(FOLDER)));
		assertNotNull(mapper.getExclusion("data"));
		assertNotNull(mapper.getInclusion("data"));
		assertNotNull(mapper.getExclusion("notExist"));
		assertNotNull(mapper.getInclusion("notExist"));
		assertTrue(mapper.getFolders().size() == 1);
		assertTrue(mapper.getFolders().size() == 1);
		assertTrue(mapper.isExcluded(new File(FOLDER + File.separator
				+ "public/exclude_it").getCanonicalPath(), "data"));
		assertTrue(mapper.isExcluded(new File(FOLDER + File.separator
				+ "public/.svn").getCanonicalPath(), "data"));
		assertFalse(mapper.isExcluded(new File(FOLDER + File.separator
				+ "not_exist").getCanonicalPath(), "data"));
		assertNotNull(mapper.getFolder(new File(FOLDER + File.separator
				+ "public").getCanonicalPath()));
		assertNotNull(mapper.getFolder(new File(FOLDER + File.separator
				+ "public/aaa").getCanonicalPath()));
		assertNull((mapper.getFolder(new File(FOLDER + File.separator
				+ "not_exist").getCanonicalPath())));
	}
}
