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

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import com.aris.umc.authentication.spi.ISsoTokenProvider;

/**
 * This is the custom SSO implementation of the ISsoTokenProvider class.
 * It is used by the down client (Architect) during connection setup to
 * create an SSO token in the form of a <code>byte[]</code> that is sent
 * to the Design Server for validation.</BR>
 * This implementation retrieves the user logon name from the environment
 * and sends it as the SSO Token. Ideally, the SSO token would be passed in to
 * the download client from the server via a WebStart applet parameter.
 * 
 * @author Christopher Steel - Software AG Government Solutions
 *
 * @since Jan 15, 2015 11:10:06 PM
 * @version 1.0
 */
public class SSOTokenProvider implements ISsoTokenProvider {
	private static Logger log = SSOPluginLogger.getLogger(SSOTokenProvider.class);
	private static String USER_KEY = "USERNAME";
	private static String DOMAIN_KEY = "USERDOMAIN";
	private static String UNKNOWN = "Unknown";

	/**
	 * Default constructor. Logs that it has been called and does nothing else.
	 */
	public SSOTokenProvider() {
		log.error("constructor called.");
		System.err.println("################################### LOGGING TO FILE: " + ((FileAppender)log.getAllAppenders().nextElement()).getFile());
	}
	
	/**
	 * Acquires the SSO token created by returning the <code>USERNAME</code> environment
	 * variable from within the download client. In the future, this should be enhanced
	 * to retrieve a WebStart parameter passed from the server.
	 * 
	 * @param tenant The tenant the user is being authenticated to.
	 * @param serviceHost The fully qualified hostname of the Design Server being authenticated to.
	 * 
	 * @return The SSO token as a <code>byte[]</code>. This is the login user name on the download client machine.
	 * 
	 * @see com.aris.umc.authentication.spi.ISsoTokenProvider#acquireSsoToken(java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] acquireSsoToken(String tenant, String serviceHost) {
		log.debug("called with tenant: " + tenant + " serviceHost: " + serviceHost);
		//TODO: Attempt to retrieve the token from a parameter passed by WebStart
		
		log.debug(System.getenv().toString());
		String user = System.getenv(USER_KEY);
		if(user == null) {
			log.warn("unable to retrieve username from env., setting user to: " + UNKNOWN);
			user = UNKNOWN;
		}
		String domain = System.getenv(DOMAIN_KEY);
		String token = null;
		if(domain == null) {
			token = user;
		}
		else {
			token = domain + "\\" + user;
		}
		log.debug("returning token: " + token);
		return token.getBytes();
	}

	/**
	 * Implemented method of the <code>ISsoTokenProvider</code> interface.
	 * This method does not appear to be called. The current implementation will
	 * log that it has been called and then return true.
	 * 
	 * @return Always returns true.
	 * @see com.aris.umc.authentication.spi.ISsoTokenProvider#isSupported()
	 */
	@Override
	public boolean isSupported() {
		log.debug("called. Returning: " + true);
		return true;
	}
	
	/**
	 * Test harness for testing aquireSsoToken method.
	 * 
	 * @param args Not Used.
	 */
	public static void main(String[] args) {
		SSOTokenProvider provider = new SSOTokenProvider();
		System.out.println("Token: " + new String(provider.acquireSsoToken("default", "alfabet123")));
	}

}
