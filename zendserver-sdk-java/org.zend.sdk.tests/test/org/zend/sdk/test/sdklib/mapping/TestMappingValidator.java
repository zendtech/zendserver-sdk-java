package org.zend.sdk.test.sdklib.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.zend.sdklib.mapping.MappingModelFactory;
import org.zend.sdklib.mapping.validator.IMappingValidator;
import org.zend.sdklib.mapping.validator.MappingParseException;
import org.zend.sdklib.mapping.validator.MappingParseMessage;
import org.zend.sdklib.mapping.validator.MappingParseStatus;
import org.zend.sdklib.mapping.validator.MappingValidator;

public class TestMappingValidator {

	private static final String FOLDER = "test/config/apps/";

	private static final String FILE_NOT_EXIST = "appdir.includes = new";
	private static final String INVALID_FOLDER = "data.includes = public";
	private static final String INVALID_SUFFIX = "appdir.invalid = public";
	private static final String EMPTY_VALUE = "appdir.includes = ";
	private static final String INVALID_KEY = "d ata.inc lud es = public";

	@Test
	public void testValidator() throws IOException, MappingParseException {
		File container = new File(FOLDER + "Project1");
		IMappingValidator validator = new MappingValidator(container);
		assertTrue(validator.parse(new FileInputStream(new File(container,
				MappingModelFactory.DEPLOYMENT_PROPERTIES))));

	}

	@Test
	public void testValidatorFileNotExist() throws IOException {
		File container = new File(FOLDER + "Project1");
		IMappingValidator validator = new MappingValidator(container);
		try {
			validator.parse(getStream(FILE_NOT_EXIST));
			fail();
		} catch (MappingParseException e) {
			assertEquals(1, e.getErrors().size());
			MappingParseStatus status = e.getErrors().get(0);
			assertEquals(MappingParseMessage.NOT_EXIST.getMessage(), status.getMessage());
		}
	}

	@Test
	public void testValidatorFileInvalidFolder() throws IOException {
		File container = new File(FOLDER + "Project1");
		IMappingValidator validator = new MappingValidator(container);
		try {
			validator.parse(getStream(INVALID_FOLDER));
			fail();
		} catch (MappingParseException e) {
			assertEquals(2, e.getErrors().size());
			MappingParseStatus status1 = e.getErrors().get(0);
			assertEquals(MappingParseMessage.INVALID_FOLDER.getMessage(),
					status1.getMessage());
			MappingParseStatus status2 = e.getErrors().get(1);
			assertEquals(MappingParseMessage.NO_APPDIR.getMessage(), status2.getMessage());
		}
	}

	@Test
	public void testValidatorFileInvalidSuffix() throws IOException {
		File container = new File(FOLDER + "Project1");
		IMappingValidator validator = new MappingValidator(container);
		try {
			validator.parse(getStream(INVALID_SUFFIX));
			fail();
		} catch (MappingParseException e) {
			assertEquals(2, e.getErrors().size());
			MappingParseStatus status1 = e.getErrors().get(0);
			assertEquals(MappingParseMessage.INVALID_SUFFIX.getMessage(),
					status1.getMessage());
			MappingParseStatus status2 = e.getErrors().get(1);
			assertEquals(MappingParseMessage.NO_APPDIR.getMessage(), status2.getMessage());
		}
	}

	@Test
	public void testValidatorEmptyValue() throws IOException {
		File container = new File(FOLDER + "Project1");
		IMappingValidator validator = new MappingValidator(container);
		try {
			validator.parse(getStream(EMPTY_VALUE));
			fail();
		} catch (MappingParseException e) {
			assertEquals(1, e.getErrors().size());
			MappingParseStatus status = e.getErrors().get(0);
			assertEquals(MappingParseMessage.EMPTY_MAPPING.getMessage(), status.getMessage());
		}
	}

	@Test
	public void testValidatorEmptyFile() throws IOException {
		File container = new File(FOLDER + "Project1");
		IMappingValidator validator = new MappingValidator(container);
		try {
			validator.parse(getStream(""));
			fail();
		} catch (MappingParseException e) {
			assertEquals(1, e.getErrors().size());
			MappingParseStatus status = e.getErrors().get(0);
			assertEquals(MappingParseMessage.EMPTY_FILE.getMessage(), status.getMessage());
		}
	}

	@Test
	public void testValidatorInvalidKey() throws IOException {
		File container = new File(FOLDER + "Project1");
		IMappingValidator validator = new MappingValidator(container);
		try {
			validator.parse(getStream(INVALID_KEY));
			fail();
		} catch (MappingParseException e) {
			assertEquals(2, e.getErrors().size());
			MappingParseStatus status1 = e.getErrors().get(0);
			assertEquals(MappingParseMessage.INVALID_FOLDER.getMessage(),
					status1.getMessage());
			MappingParseStatus status2 = e.getErrors().get(1);
			assertEquals(MappingParseMessage.NO_APPDIR.getMessage(), status2.getMessage());
		}
	}

	private InputStream getStream(String content) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(content.getBytes());
		return new ByteArrayInputStream(out.toByteArray());
	}

}
