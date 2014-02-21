package org.zend.webapi.test.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.internal.core.Utils;

public class TestUtils {

	private static final String KEY = "9dc7f8c5ac43bb2ab36120861b4aeda8f9bb6c521e124360fd5821ef279fd9c7";

	@Test
	public void testHashMac() throws WebApiException {
		String result = Utils.hashMac("test", KEY);
		Assert.assertEquals(
				"3776134656b5e25b0aa5c14ab2fd2136af4f12737022165dc05d68c5be59b9c6",
				result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testHashMacEmptyKey() throws WebApiException {
		Utils.hashMac("test", "");
	}

	@Test
	public void testFormattedDate() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"E, dd MMM yyyy HH:mm:ss z", Locale.US);
		formatter.setTimeZone(Utils.GMT_ZONE);
		Date date = Calendar.getInstance().getTime();
		String dateString = formatter.format(date);
		String result = Utils.getFormattedDate(date);
		Assert.assertEquals(dateString, result);
	}
}
