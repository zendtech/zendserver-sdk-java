package org.zend.sdk.test.sdkcli.update.parser;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;
import org.zend.sdk.test.sdkcli.update.AbstractXMLTest;
import org.zend.sdkcli.update.UpdateException;
import org.zend.sdkcli.update.parser.Delta;
import org.zend.sdkcli.update.parser.DeltaParser;

public class TestDelta extends AbstractXMLTest {

	@Test
	public void testExecuteDelta() throws UpdateException, IOException {
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<delta file=\"package.zip\" size=\"833\">"
				+ "<add file=\"toCopy\" dest=\"a/b/c\"/>"
				+ "<add file=\"folder/\" dest=\"a/b\"/>"
				+ "<remove file=\"toRemove\"/>" + "</delta>";
		DeltaParser parser = new DeltaParser(new ByteArrayInputStream(
				xmlString.getBytes()));
		assertNotNull(parser);
		assertNotNull(parser.getDelta());
		Delta delta = Mockito.spy(parser.getDelta());
		Mockito.doReturn("file:///"
						+ new File("test/config/update/package.zip")
								.getAbsolutePath()).when(delta)
				.getZipLocation();
		copyFile(new File("test/config/update/package.zip"),
				new File(delta.getTemp(), "package.zip"));
		createFile(new File(tmp, "toRemove"));
		assertFileExists(tmp, "toRemove");
		delta.execute(tmp);
		assertFileNotExists(tmp, "toRemove");
		assertFileExists(tmp, "a/b/c/toCopy");
		assertFileExists(tmp, "a/b/folder/innerFile");
		assertFileExists(tmp, "a/b/folder/innerFolder/nextFile");
	}

}
