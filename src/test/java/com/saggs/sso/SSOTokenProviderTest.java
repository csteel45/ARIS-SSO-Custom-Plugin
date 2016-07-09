/*
 * @(#)SSOTokenProviderTest.java $Date: Jul 8, 2016 11:08:02 PM $
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
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Christopher Steel - FortMoon Consulting, Inc.
 *
 * @since Jul 8, 2016 11:08:02 PM
 */
@FixMethodOrder(MethodSorters.DEFAULT)
public class SSOTokenProviderTest {
	SSOTokenProvider provider = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOTokenProvider#SSOTokenProvider()}.
	 */
	@Test
	public void testSSOTokenProvider() {
		provider = new SSOTokenProvider();
		assertFalse(provider.properties.getProperty("sso.provider.userkey", "def").equals("def"));
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOTokenProvider#acquireSsoToken(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAcquireSsoToken() {
		provider = new SSOTokenProvider();
		byte[] token = provider.acquireSsoToken("tenant", "host");
		String user = System.getenv(provider.USER_KEY);
		assertTrue(new String(token).endsWith(user) && user.length() > 1);
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOTokenProvider#isSupported()}.
	 */
	@Test
	public void testIsSupported() {
		provider = new SSOTokenProvider();
		assertTrue(provider.isSupported());
	}

}
