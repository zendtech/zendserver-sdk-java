/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.zend.php.library.internal.json.JSONArray;
import org.zend.php.library.internal.json.JSONException;
import org.zend.php.library.internal.json.JSONObject;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class PackagistService {

	private static final String URL_TAG = "url";
	private static final String RESULTS_TAG = "results";
	private static final String TOTAL_TAG = "total";
	private static final String VERSIONS_TAG = "versions";
	private static final String TIME_TAG = "time";
	private static final String HOMEPAGE_TAG = "homepage";
	private static final String REQUIRE_TAG = "require";
	private static final String VERSION_NORMALIZED_TAG = "version_normalized";
	private static final String VERSION_TAG = "version";
	private static final String DESCRIPTION_TAG = "description";
	private static final String NAME_TAG = "name";
	private static final String PACKAGE_TAG = "package";

	private static final String BASE_URL = "https://packagist.org/";

	public static List<RepositoryPackage> getPackages(String query, int page) {
		List<RepositoryPackage> result = new ArrayList<RepositoryPackage>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("q", query);
		if (page > 1) {
			params.put("page", String.valueOf(page));
		}
		String response = executeGetRequest(BASE_URL + "search.json", params,
				null, 200);
		result.addAll(parsePackages(response));
		return result;
	}

	public static int getPagesSize(String query) {
		Map<String, String> params = new TreeMap<String, String>();
		params.put("q", query);
		String response = executeGetRequest(BASE_URL + "search.json", params,
				null, 200);
		return getPageNumber(response);
	}

	public static RepositoryPackage getPackageInfo(String name) {
		String response = executeGetRequest(BASE_URL + "packages/" + name
				+ ".json", null, null, 200);
		return parseRepositoryPackage(response);
	}

	private static int getPageNumber(String input) {
		try {
			JSONObject json = new JSONObject(input);
			Object object = json.get(TOTAL_TAG);
			if (object != null) {
				return (Integer) object;
			}
		} catch (JSONException e) {
			// TODO handle it
			System.out.println();
		}
		return 0;
	}

	private static List<RepositoryPackage> parsePackages(String input) {
		List<RepositoryPackage> result = new ArrayList<RepositoryPackage>();
		try {
			JSONObject json = new JSONObject(input);
			Object object = json.get(RESULTS_TAG);
			JSONArray versionsArray = (JSONArray) object;
			for (int i = 0; i < versionsArray.length(); i++) {
				Object v = versionsArray.get(i);
				JSONObject jsonObject = (JSONObject) v;
				RepositoryPackage repoPackage = new RepositoryPackage();
				repoPackage.setName(jsonObject.getString(NAME_TAG));
				repoPackage.setDescription((String) jsonObject
						.get(DESCRIPTION_TAG));
				repoPackage.setUrl(jsonObject.getString(URL_TAG));
				result.add(repoPackage);
			}

		} catch (JSONException e) {
			// TODO handle it
			System.out.println();
		}
		return result;
	}

	private static RepositoryPackage parseRepositoryPackage(String input) {
		RepositoryPackage result = new RepositoryPackage();
		try {
			JSONObject json = new JSONObject(input);
			Object object = json.get(PACKAGE_TAG);
			JSONObject container = (JSONObject) object;
			result.setName(container.getString(NAME_TAG));
			result.setDescription(container.getString(DESCRIPTION_TAG));
			result.setTime(container.getString(TIME_TAG));
			Object versionsJson = container.get(VERSIONS_TAG);
			JSONObject versionsArray = (JSONObject) versionsJson;
			Iterator<?> keys = versionsArray.keys();
			while (keys.hasNext()) {
				Object v = versionsArray.get((String) keys.next());
				JSONObject jsonObject = (JSONObject) v;
				PackageVersion version = new PackageVersion();
				version.setName(jsonObject.getString(NAME_TAG));
				version.setDescription(jsonObject.getString(DESCRIPTION_TAG));
				version.setHomepage(jsonObject.getString(HOMEPAGE_TAG));
				version.setVersion(jsonObject.getString(VERSION_TAG));
				version.setVersionNormalized((String) jsonObject
						.get(VERSION_NORMALIZED_TAG));
				Object requiresObject = jsonObject.get(REQUIRE_TAG);
				JSONObject requires = (JSONObject) requiresObject;
				Iterator requiresKeys = requires.keys();
				while (requiresKeys.hasNext()) {
					String k = (String) requiresKeys.next();
					String val = requires.getString(k);
					version.addRequire(k, val);
				}
				result.addVersion(version);
			}

		} catch (JSONException e) {
			// TODO handle it
		}
		return result;
	}

	private static String executeGetRequest(String url,
			Map<String, String> parameters, Map<String, String> cookies,
			int expectedCode) {
		HttpClient client = new HttpClient();
		HttpMethodBase method = createGetRequest(url, parameters);
		setCookies(method, cookies);
		if (method != null) {
			int statusCode = -1;
			try {
				statusCode = client.executeMethod(method);
				if (statusCode == expectedCode) {
					String responseContent = new String(
							method.getResponseBody());
					return responseContent;
				}
			} catch (IOException e) {
				// TODO handle it
			} finally {
				method.releaseConnection();
			}
		}
		return null;
	}

	private static HttpMethodBase createGetRequest(String url,
			Map<String, String> params) {
		GetMethod method = new GetMethod(url);
		if (params != null) {
			NameValuePair[] query = new NameValuePair[params.size()];
			Set<String> keyList = params.keySet();
			int i = 0;
			for (String key : keyList) {
				query[i++] = new NameValuePair(key, params.get(key));
			}
			method.setQueryString(query);
		}
		return method;
	}

	private static HttpMethodBase setCookies(HttpMethodBase method,
			Map<String, String> params) {
		if (params != null) {
			StringBuilder builder = new StringBuilder();
			Set<String> keyList = params.keySet();
			for (String key : keyList) {
				builder.append(key);
				builder.append("="); //$NON-NLS-1$
				builder.append(params.get(key));
				builder.append(";"); //$NON-NLS-1$
			}
			String value = builder.toString();
			if (value.length() > 0) {
				value = value.substring(0, value.length() - 1);
				method.setRequestHeader("Cookie", value); //$NON-NLS-1$
			}
		}
		return method;
	}

}
