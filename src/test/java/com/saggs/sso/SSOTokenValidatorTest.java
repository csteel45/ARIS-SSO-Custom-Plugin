/*
 * Copyright 2015 Software AG Government Solutions
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Software AG
 * Government Solutions ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Software AG Government Solutions.
 *
 * SOFTWARE AG GS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SOFTWARE AG GS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.saggs.sso;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Christopher Steel - Software AG Government Solutions
 *
 * @since Feb 4, 2015 3:17:13 PM
 * @version 1.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SSOTokenValidatorTest {
	SSOTokenValidator validator = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		validator = new SSOTokenValidator();
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOTokenValidator#getSsoHeaderName()}.
	 */
	@Test
	public void testGetSsoHeaderName() {
		assertTrue(validator.getSsoHeaderName().equals(
				SSOProperties.getInstance("src/resources/base/conf/sso-plugin.properties").getProperty("sso.validator.header_name", "SM_USER")));
	}
	
	/**
	 * Test method for {@link com.saggs.sso.SSOTokenValidator#verifySsoToken(java.lang.String, byte[])}.
	 */
	@Test
	public void testVerifySsoTokenStringByteArray() {
		assertTrue(validator.verifySsoToken("default", "12345678".getBytes()).isAuthenticated());
		assertTrue(validator.verifySsoToken("default", "rvki12".getBytes()).isAuthenticated());
		assertTrue(validator.verifySsoToken("default", "nw\\rvki12".getBytes()).isAuthenticated());
		assertTrue(validator.verifySsoToken("default", "nw.boeing.com\\rvki12".getBytes()).isAuthenticated());
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOTokenValidator#verifySsoToken(java.lang.String, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testVerifySsoTokenStringMapOfStringStringMapOfStringString() {
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("name", new String[] { "val1", "Val2" });
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("boeingWSSOW2K", new String[] { "rvki12" });
		assertTrue(validator.verifySsoToken("http://www.myuri.com", parameters, headers).isAuthenticated());;
		headers.put("boeingWSSOW2K", new String[] { "nw\\rvki12" });
		assertTrue(validator.verifySsoToken("http://www.myuri.com", parameters, headers).isAuthenticated());;
		headers.put("boeingWSSOW2K", new String[] { "nw.boeing.com\\rvki12" });
		assertFalse(validator.verifySsoToken("http://www.myuri.com", parameters, headers).isAuthenticated());

	}

}
