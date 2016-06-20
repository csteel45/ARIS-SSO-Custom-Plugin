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
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.saggs.sso.FileWatcher;

/**
 * @author Christopher Steel - Software AG Government Solutions
 *
 * @since Jan 24, 2015 9:05:11 PM
 * @version 1.0
 */
public class FileWatcherTest {
	private FileWatcher fw = null;
	private File file = null;
	private Observer o;
	private boolean changed = false;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		file = File.createTempFile("test", ".properties", new File("."));
		assertTrue("File exists", file != null);
		fw = FileWatcher.watch(file);
		o = new Observer() {
			@Override
			public void update(Observable arg0, Object arg1) {
				changed = true;				
			}
		};
		fw.addObserver(o);
	}

	/**
	 * Test method for {@link com.saggs.sso.FileWatcher#watch(java.io.File)}.
	 */
	@Test
	public void testWatch() {
		// Set it slightly in the future since just added observer.
		file.setLastModified(System.currentTimeMillis() + 2000);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		assertTrue("File Watcher notify called", changed);
	}
	
	@After
	public void tearDown() throws Exception {
		file.delete();
	}

}
