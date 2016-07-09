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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Wrapper class for loading <code>Properties</code> from a pre-defined file (sso-plugin.proeprties).
 * Uses a Singleton pattern to obtain the instance and then can be used to get Properties 
 * as the properties file is loaded on construction of the instance. This class is not 
 * intended to be used to store properties, only to load them from the existing file.
 * 
 * @author Christopher Steel - Software AG Government Solutions
 *
 * @since Jan 24, 2015 10:03:18 AM
 * @version %I%, %G% 
 */
public class SSOProperties extends Observable implements Observer {
	private static SSOProperties instance = null;
	private static String fileName = "base/conf/sso-plugin.properties";
	private Properties properties = new Properties();
	private static Logger log = Logger.getLogger(SSOProperties.class);  // Use ARIS logger
	private FileWatcher watcher;

	/**
	 * Private constructor used by the {@link #getInstance() getInstance} method.
	 * Uses {@link #getFile(String) getFile} method to get the file object
	 * for the properties filename and then loads the file using the 
	 * {@link #load(File) load} method and then adding a FileWatcher to the
	 * file. Not intended for public use.
	 * 
	 * @see FileWatcher#addObserver(Observer)
	 */
	SSOProperties() {
		super();
	}

	public synchronized static SSOProperties getInstance() {
		log.info("called");
		if(instance == null) {
			log.info("instance is null, initializing.");
			instance = new SSOProperties();
			instance.init();
		}
		return instance;
	}
	
	/**
	 * Singleton pattern method for obtaining the singleton instance.
	 * In this implementation, the properties are loaded when the method is 
	 * first invoked and the underlying static instance is constructed.
	 * 
	 * @return The static instance of the <code>SSOProperties</code> class
	 */
	public synchronized static SSOProperties getInstance(String filePath) {
		log.info("getInstance(" + filePath + ") called");
		instance = null; // Force reload.
		fileName = filePath;
		
		return getInstance();
	}
	
	protected void init() {
		try {
			File file = new File(fileName);
			if(file == null || !file.exists()) {
				log.info("file does not exist: " + file.getAbsolutePath() + ", loading defaults and creating file.");
				loadDefaults();
				saveDefaults();
			}
			else {	
				log.info("Loading properties file: " + file.getAbsolutePath());
				load(file);
				watcher = FileWatcher.watch(file);
				if(watcher != null) {
					log.info("Added file watcher on file: " + file.getAbsolutePath());
					watcher.addObserver(this);
				}
			}
		}
		catch (Exception e) {
			log.error("Exception loading properties: " + e);
			instance.loadDefaults();
		}
	}
	
	/**
	 * This method loads the properties from the input file name.
	 * It assumes a relative path and calls 
	 * <code>ClassLoader.getSystemClassLoader().getResource(fileName)</code> to
	 * get the full path of the file. If it can't find the file, it will log an
	 * error message and return without exception.
	 * 
	 * @param filename The name of the properties file to load.
	 * @throws FileNotFoundException 
	 */
	protected void load(File file) throws IOException {
		log.debug("called for file: " + file.getAbsolutePath());

		try {
			FileInputStream fis = new FileInputStream(file);
			properties.load(fis);
			log.debug("Loaded properties file: " + file.getAbsolutePath());
		} catch (IOException e) {
			log.error("Error loading file: " + file.getAbsolutePath());
			throw e;
		}
	}
	
	protected void loadDefaults() {
		log.info("Loading sso property defaults");
		instance.properties.put("sso.validator.trim_domain", "true");
		instance.properties.put("sso.validator.use_domain", "true");
		instance.properties.put("sso.validator.header_name", "boeingWSSOW2K");
		instance.properties.put("sso.provider.userkey", "USERNAME");
		instance.properties.put("sso.provider.domainkey", "USERDOMAIN");
		instance.properties.put("sso.logger.file_name", "sso-custom-plugin.log");
		instance.properties.put("sso.logger.pattern", "%d %-5p [%c{1}] %C{1}.%M - %m%n");
		instance.properties.put("sso.logger.threshold", "DEBUG");
		instance.properties.put("sso.logger.max_file_size", "10MB");
		instance.properties.put("sso.logger.append", "true");
		instance.properties.put("sso.logger.immediateFlush", "true");		
	}
	
	protected void saveDefaults() {
		log.info("Saving property defaults...");
		try {
			File file = new File(fileName);
			file.getParentFile().mkdirs();
			file.createNewFile();
			OutputStream os = new FileOutputStream(file);
			instance.properties.store(os, "Defaults created and saved.");
			os.close();
			os.flush();
			log.debug("Successfully saved default sso properties to file: " + file.getAbsolutePath());
		}
		catch (Exception e) {
			log.error("Exception saving default sso properties file: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets a property value, given the corresponding key. If the
	 * key is not found, return the defaultVal.
	 * 
	 * @param key The properties file key
	 * @param defaultVal The default value to return if the key is not found.
	 * @return The String value for the key in the properties file, otherwise the defaultVal.
	 * @see Properties#getProperty(String, String)
	 */
	public String getProperty(String key, String defaultVal) {
		return properties.getProperty(key, defaultVal);
	}
	
	/**
	 * Implementation of the Observer interface. Allows the <code>FileWatcher</code> to 
	 * notify this class of updates to the properties files. This method is invoked on
	 * file change and then loads properties from the file passed in as the file variable.
	 * Subsequently, this class extends Observable and will in turn notify its observers
	 * that the file has changed.
	 * 
	 *  @param callingClass The object that has invoked the method
	 *  @param changedFile The properties file that has changed
	 */
	@Override
	public void update(Observable arg0, Object changedFile) {
		try {
			load((File)changedFile);
			this.setChanged();
			this.notifyObservers(changedFile);
		} catch (Exception e) {
			log.error("Exception loading updated file: " + e, e);
			e.printStackTrace();
		}
	}
	
	/**
	 * This is a test harness to test loading and retrieval of properties.
	 * 
	 * @param args Not Used.
	 */
	public static void main(String[] args) {
		final SSOProperties props = SSOProperties.getInstance("base/conf/sso-plugin.properties");
		System.out.println("Default property for non-existent: " + props.getProperty("sso.logger.file_name", "default_value"));
		Observer o = new Observer() {
			@Override
			public void update(Observable arg0, Object arg1) {
				System.out.println("Foo: " + props.getProperty("foo", "default"));				
			}			
		};
		props.addObserver(o);
	}

}
