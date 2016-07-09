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

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.saggs.sso.*;

/**
 * @author Christopher Steel - Software AG Government Solutions
 *
 * @since Jan 25, 2015 5:51:05 AM
 * @version 1.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SSOPropertiesTest {
	SSOProperties props;
	boolean updated = false;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
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
		props = SSOProperties.getInstance("src/resources/base/conf/sso-plugin.properties");
		assertTrue("getInstance != null", props != null);
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOProperties#getProperty(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetProperty() {
		props = SSOProperties.getInstance("src/resources/base/conf/sso-plugin.properties");
		assertTrue(props.getProperty("sso.validator.use_domain", "def").equalsIgnoreCase("true"));
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOProperties#load(java.io.File)}.
	 */
	@Test
	public void testLoad() {
		props = SSOProperties.getInstance("src/resources/base/conf/sso-plugin.properties");
		assertTrue(props.getProperty("sso.validator.use_domain", "def").equalsIgnoreCase("true"));
		
	}
	
	@Test
	public void testLoadFileNotExists() {
		boolean exception = false;
		props = SSOProperties.getInstance("src/resources/base/conf/sso-plugin.properties");
		try {
			props.load(new File("NotExists"));
		}
		catch (IOException e) {
			exception = true;
		}
		assertTrue(exception);
		assertFalse(props.getProperty("sso.validator.use_domain", "def").equalsIgnoreCase("def"));
		assertTrue(props.getProperty("null", "def").equalsIgnoreCase("def"));
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOProperties#saveDefaults()}.
	 */
	@Test
	public void testSaveDefaults() {
		File file = new File("src/resources/base/conf/test-sso-plugin.properties");
		assertFalse(file.exists());
		props = SSOProperties.getInstance("src/resources/base/conf/test-sso-plugin.properties");
		props.getProperty("X", "def");
		File fileNew = new File("src/resources/base/conf/test-sso-plugin.properties");
		assertTrue(fileNew.exists());
		file.delete();
	}

	/**
	 * Test method for {@link com.saggs.sso.SSOProperties#update(java.util.Observable, java.lang.Object)}.
	 */
	@Test
	public void testUpdate() {
		props = SSOProperties.getInstance("src/resources/base/conf/sso-plugin.properties");
		assertFalse(updated);
		Observer o = new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				updated = true;
			}
			
		};
		props.addObserver(o);
		props.saveDefaults();
		try {
			Thread.sleep(2500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(updated);
	}
	
}
