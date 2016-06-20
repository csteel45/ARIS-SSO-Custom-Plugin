/*
 * Copyright 2016 Software AG Government Solutions
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

/**
 * @author Christopher Steel - Software AG Government Solutions
 *
 * @since Jun 10, 2016 12:55:58 PM
 * @version 1.0
 */
public class JarUpdate {
	private static Logger log = SSOPluginLogger.getLogger(JarUpdate.class);
	
	public static void update(String jarName, String fileName) throws Exception {
		// Create file descriptors for the jar and a temp jar.
		File jarFile = new File(jarName);
		File tempJarFile = new File(jarName + ".tmp");

		// Open the jar file.
		JarFile jar = new JarFile(jarFile);
		log.debug(jarName + " opened.");
		// Initialize a flag that will indicate that the jar was updated.
		boolean jarUpdated = false;

		try {
			// Create a temp jar file with no manifest. (The manifest will be copied when the entries are copied.)

			Manifest jarManifest = jar.getManifest();
			JarOutputStream tempJar = new JarOutputStream(new FileOutputStream(tempJarFile));

			// Allocate a buffer for reading entry data.
			byte[] buffer = new byte[1024];
			int bytesRead = 0;

			try {
				// Open the given file.
				FileInputStream file = new FileInputStream(fileName);

				// Loop through the jar entries and add them to the temp jar,
				// skipping the entry that was added to the temp jar already.
				for (Enumeration entries = jar.entries(); entries.hasMoreElements();) {
					// Get the next entry.
					JarEntry entry = (JarEntry) entries.nextElement();
					// If the entry has not been added already, add it.


//					if (! entry.getName().startsWith(fileName.substring(0, fileName.lastIndexOf('-')))) {
					if (! entry.getName().startsWith("y-umc-auth-provider-custom")) {
						tempJar.putNextEntry(entry);
						// Get an input stream for the entry.
						InputStream entryStream = jar.getInputStream(entry);
						// Read the entry and write it to the temp jar.
						while ((bytesRead = entryStream.read(buffer)) != -1) {
							tempJar.write(buffer, 0, bytesRead);
						}
					}
					else {
						// Read the new file and write it to the jar.
						JarEntry newEntry = new JarEntry(entry.getName());
						tempJar.putNextEntry(newEntry);
						while ((bytesRead = file.read(buffer)) != -1) {
							tempJar.write(buffer, 0, bytesRead);
						}
						log.debug(entry.getName() + "  new file added.");						
					}
				}

				jarUpdated = true;
			}
			catch (Exception ex) {
				log.error("Exception adding entries to jar: " + ex, ex);
				// Add a stub entry here, so that the jar will close without an exception.
				tempJar.putNextEntry(new JarEntry("stub"));
			}
			finally {
				tempJar.close();
			}
		}
		finally {
			jar.close();
			log.debug(jarName + " closed.");
			// If the jar was not updated, delete the temp jar file.
			if (!jarUpdated) {
				log.warn("Jar not updated. Deleting temp jar.");
				tempJarFile.delete();
			}
		}

		// If the jar was updated, delete the original jar file and rename the
		// temp jar file to the original name.

		if (jarUpdated) {
			jarFile.delete();
			tempJarFile.renameTo(jarFile);
			log.debug(jarName + " updated.");
		}
	}

	/**
	 * Entry point for patching the client.zip with the custom SSO pack file.
	 * 
	 * @param args Arg[0]: path to the client.zip file for patching. Arg[1]: path to y-umc-auth-provider-xxx.jar.pack.gz file.
	 * If neither are specified, program will look in current directory for client.zip and y-umc-auth-provider-custom-98.3.0.jar.pack.gz.
	 * @throws Exception If anything bad happens.
	 */
	public static void main(String[] args) throws Exception {
		String jarName = null;
		String fileName = null;
		log.debug("Starting...");
		if(args == null || args.length < 2) {
			jarName = "client.zip";
			fileName = "y-umc-auth-provider-custom-98.3.0.jar.pack.gz";
		}
		else {
			jarName = args[0];
			fileName = args[1];			
		}
		JarUpdate.update(jarName, fileName);
		log.debug("Complete.");
	}
}
