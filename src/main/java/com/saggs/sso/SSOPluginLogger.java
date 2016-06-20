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

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;

/**
 * This is a wrapper class for Log4j that creates a log appender
 * for the com.saggs package independent of the Log4J configuration
 * that is picked up by default when run inside the runnables or the
 * download client. It loads properties from the SSOProperties class
 * and will dynamically pick up changes to the properties files so that
 * changing log level and file patterns do not require restarts.
 * 
 * @author Christopher Steel - Software AG Government Solutions
 *
 * @since Jan 15, 2015 9:28:26 PM
 * @version 1.0
 */
public class SSOPluginLogger implements Observer {
	private static final String FILE_LOGGER = "SSOFileLogger";
	protected String fileName = "sso-custom-plugin.log";
	private String MAX_FILE_SIZE = "10MB";
	private int MAX_BACKUPS = 1;
	private String pattern = "%d %-5p [%c{1}] %C{1}.%M - %m%n";
	private static RollingFileAppender appender = new RollingFileAppender();
	private SSOProperties props = null;
	private static Logger logger = null;
	private static SSOPluginLogger instance = null;
	
	/**
	 * Private default constructor intended only for use by the getInstance method.
	 * 
	 */
	private SSOPluginLogger() {
		logger = Logger.getLogger("com.saggs");
		logger.setLevel(Level.TRACE);
		//logger.addAppender(new ConsoleAppender(new PatternLayout(pattern), "System.out"));
	}
	
	/**
	 * Singleton pattern accessor method. If the instance attribute is null, it will
	 * instantiate a new SSOPluginLogger and assign it to the instance attribute.
	 * It then loads the properties, assigns the instance attribute as an observer 
	 * and adds the RollingFileAppender, initiated with the loaded properties.
	 * 
	 * @return SSOPluginLogger static instance
	 */
	public synchronized static SSOPluginLogger getInstance() {
		if(instance == null) {
			instance = new SSOPluginLogger();
			instance.props = SSOProperties.getInstance();
			instance.props.addObserver(instance);
			logger.addAppender(instance.getAppender());
			appender.activateOptions();
		}
		return instance;
	}
	
	/**
	 * Get the Log4j Logger instance for a class. This method wraps the Log4j
	 * to ensure the Logger with the sso file appender attached in order to
	 * override the Log4j properties set in the runnables and download client.
	 * 
	 * @param clazz The class that will be using the Logger.
	 * @return The log4j Logger instance
	 */
	@SuppressWarnings("static-access")
	public static Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
		// Need to use our logger to get Logger for the class, NOT static Logger.
		return getInstance().logger.getLogger(clazz);
	}
	
	/**
	 * Get a Log4j RollingFileAppender that logs to the specified filename
	 * at the DEBUG level and rollover MAX_BACKUPS times when the log file
	 * exceed MAX_FILE_SIZE.
	 * 
	 * @return Log4j RollingFileAppender
	 */
	protected FileAppender getAppender() {
		logger.debug("called");
		appender.setName(FILE_LOGGER + System.currentTimeMillis());
		appender.setFile(props.getProperty("sso.logger.file_name", fileName));
		loadProperties();
		appender.activateOptions();
		return appender;
	}
	
	/**
	 * Update is called when the properties file is changed. This will cause the SSOPluginLogger
	 * to remove the current appender and hen call <code>loadProperties()</code>.
	 * The appender will be updated with the new properties (except for the log file name).
	 * In order to log to a different file, you must restart the application.
	 */
	@Override
	public void update(Observable o, Object arg) {
		logger.info("Update called. Reloading properties.");

		loadProperties();
		appender.activateOptions();
	}
	
	/**
	 * Loads the <code>RollingFileAppender</code> properties and then updates the appender. 
	 * Changes to the appender will appear in the log file at the return of this method.
	 * 
	 */
	@SuppressWarnings("deprecation")
	protected synchronized void loadProperties() {
		logger.info("Loading logger properties");
		appender.setLayout(new PatternLayout(props.getProperty("sso.logger.pattern", pattern)));
		appender.setThreshold(Priority.toPriority(props.getProperty("sso.logger.threshold", "DEBUG")));
		appender.setMaxFileSize(props.getProperty("sso.logger.max_file_size", MAX_FILE_SIZE));
		appender.setMaxBackupIndex(Integer.parseInt(props.getProperty("sso.logger.max_backup_index", Integer.toString(MAX_BACKUPS))));
		appender.setAppend(Boolean.getBoolean(props.getProperty("sso.logger.append", "true")));	
		appender.setImmediateFlush(true);
		logger.info("Loaded logger properties");
	}

	/**
	 * This is a simple test harness for testing rollover capability.
	 * Set the MAX-FILE_SIZE field to around 2KB and then run this class directly.
	 * 
	 * @param args Not Used.
	 */
	public static void main(String[] args) {
		Logger log = SSOPluginLogger.getLogger(SSOPluginLogger.class);
		for (int i = 0; i < 20; i++) {
			log.debug("Hello World");
			log.debug("Hello Again World");
			log.debug("Goodbye World");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
