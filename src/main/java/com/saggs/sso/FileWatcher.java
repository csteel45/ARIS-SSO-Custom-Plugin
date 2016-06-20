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
import java.util.Observable;

/**
 * This class can be used to watch for modifications to a file and then
 * alert observers to the change, via the Observer/Observable pattern.
 * This class is compatible with Java 1.6 and does not use the new nio
 * classes.
 * 
 * @author Christopher Steel - Software AG Government Solutions
 * 
 * @since Jan 24, 2015 11:56:30 AM
 * @version 1.0
 */
public class FileWatcher extends Observable implements Runnable {
	private File file = null;
	private long sleepTime = 2000; // 2 seconds

	/**
	 * Private constructor.
	 */
	private FileWatcher() {
		super();
	}
	
	/**
	 * Private overloaded constructor.
	 * 
	 * @param file The file to be watched
	 */
	private FileWatcher(File file) {
		this();
		this.file = file;
	}

	/**
	 * The run method of the implemented <code>Runnable</code> interface.
	 * This method is not meant to be called directly. It is intended
	 * to be called by a <code>Thread</code> via its <code>start</code>
	 * method.
	 */
	@Override
	public void run() {
		long lastTime = file.lastModified();
		while (true) {
			try {
				Thread.sleep(this.getSleepTime());
				if (file.lastModified() > lastTime) {
					lastTime = file.lastModified();
					this.setChanged();
					this.notifyObservers(file);
				}
			} catch (InterruptedException e) {
				// ignore. We don't care if sleep is interrupted.
			}
		}
	}
	
	/**
	 * @return the sleepTime
	 */
	public long getSleepTime() {
		return sleepTime;
	}

	/**
	 * @param sleepTime the sleepTime to set
	 */
	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	/**
	 * This is the only public interface method intended for use. Calling
	 * classes should pass in the File object they wish to watch and then
	 * call the <addObserver> method from the parent class, to be notified
	 * when a file's last modification time has changed. This method will 
	 * create a FileWatcher instance for each invocation and start a daemon
	 * <code>Thread</code> that will poll the file every 5 seconds for changes.
	 * 
	 * @param file The file to be watched.
	 * @return An instance of a FileWatcher to observe via the 
	 * <code>addObserver</code> interface
	 */
	public static FileWatcher watch(File file) {
		if(file == null || !file.exists()) {
			System.err.println("FileWatcher.watch NULL file passed in. Skipping watcher.");
			return null;
		}
		FileWatcher fw = new FileWatcher(file);
		Thread watcher = new Thread(fw, "FileWatcher:" + file.getName());
		watcher.setDaemon(true);
		watcher.start();
		return fw;
	}

}
