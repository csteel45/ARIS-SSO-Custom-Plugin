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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.saggs.sso.*;

/**
 * @author Christopher Steel - Software AG Government Solutions
 *
 * @since Jan 25, 2015 5:51:05 AM
 * @version 1.0
 */
public class SSOPropertiesTest {
	SSOProperties props;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			props = SSOProperties.getInstance();
		}
		catch(Exception e) {
			
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOProperties#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		assertTrue("getInstance != null", props != null);
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOProperties#getProperty(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetProperty() {
		assertTrue(props.getProperty("sso.validator.use_domain", "def").equalsIgnoreCase("true"));
		//assertTrue(true);
	}
	
	//public void testLoadFileNotExists() {
		//assertTrue(props.getProperty("sso.validator.use_domain", "def").equalsIgnoreCase("def"));
		//assertTrue(true);
	//}

	/**
	 * Test method for {@link com.saggs.sso.SSOProperties#load(java.io.File)}.
	 */
	@Test
	public void testLoad() {
		assertTrue(true);
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOProperties#update(java.util.Observable, java.lang.Object)}.
	 */
	@Test
	public void testUpdate() {
		assertTrue(true);
	}

}
