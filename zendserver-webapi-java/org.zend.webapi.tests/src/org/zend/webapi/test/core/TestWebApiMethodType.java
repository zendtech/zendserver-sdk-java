package org.zend.webapi.test.core;

import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.service.WebApiMethodType;

public class TestWebApiMethodType {

	@Test
	public void testWebApiMethodType() throws ParseException {
		Assert.assertEquals(WebApiMethodType.GET_SYSTEM_INFO,
				WebApiMethodType.valueOf("GET_SYSTEM_INFO"));
		Assert.assertEquals(WebApiMethodType.GET_SYSTEM_INFO.getName(),
				WebApiMethodType.valueOf("GET_SYSTEM_INFO").getName());
		Assert.assertEquals(WebApiMethodType.GET_SYSTEM_INFO.getRequestClass(),
				WebApiMethodType.valueOf("GET_SYSTEM_INFO").getRequestClass());
	}
}
