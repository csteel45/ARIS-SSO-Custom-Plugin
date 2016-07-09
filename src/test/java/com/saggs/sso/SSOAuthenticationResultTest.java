/*
 * @(#)SSOAuthenticationResultTest.java $Date: Jul 8, 2016 7:16:12 PM $
 * 
 * Copyright © 2016 FortMoon Consulting, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of FortMoon
 * Consulting, Inc. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with FortMoon Consulting.
 * 
 * FORTMOON MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. FORTMOON SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */
package com.saggs.sso;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;

/**
 * @author Christopher Steel - FortMoon Consulting, Inc.
 *
 * @since Jul 8, 2016 7:16:12 PM
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SSOAuthenticationResultTest {

	SSOAuthenticationResult result = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		result =  new SSOAuthenticationResult(true, "tenant", "user", SSOPluginLogger.getLogger(SSOAuthenticationResultTest.class));
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOAuthenticationResult#getTenant()}.
	 */
	@Test
	public void testGetTenant() {
		assertTrue(result.getTenant().equals("tenant"));
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOAuthenticationResult#getUserName()}.
	 */
	@Test
	public void testGetUserName() {
		assertTrue(result.getUserName().equals("user"));
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOAuthenticationResult#isAuthenticated()}.
	 */
	@Test
	public void testIsAuthenticated() {
		assertTrue(result.isAuthenticated());
		SSOAuthenticationResult resultFalse =  new SSOAuthenticationResult(false, "tenant", "user", SSOPluginLogger.getLogger(SSOAuthenticationResultTest.class));
		assertFalse(resultFalse.isAuthenticated());
	}

}
