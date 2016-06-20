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

import org.apache.log4j.Logger;

import com.aris.umc.authentication.spi.ISsoResult;

/**
 * This is the implementation of the ISsoResutl class, intended to be passed
 * to the Design Server runnables to indicate if a user has been authenticated
 * via SSO or not. Even if the result indicates the user is authenticated, the
 * userName must exist as a userid within UMC or the user will have no privileges.
 * 
 * @author Christopher Steel - Software AG Government Solutions
 *
 * @since Jan 15, 2015 9:27:14 PM
 * @version 1.0
 */
public class SSOAuthenticationResult implements ISsoResult {
	protected String tenant = "default";
	protected String userName = "Unknown";
	protected boolean authenticated = false;
	private static Logger log = SSOPluginLogger.getLogger(SSOAuthenticationResult.class);
	
	/**
	 * Default constructor. Logs that it has been called, but nothing else.
	 */
	public SSOAuthenticationResult() {
		super();
		log.debug("constructor called.");	
	}
	
	/**
	 * Overloaded constructor meant for mainstream use. Constructed by the TokenValidator
	 * to indicate whether or not the user was authenticated via SSO either through a
	 * header variable in a web request, or via the TokenProvider class in the download client.
	 * 
	 * @param authenticated True of user has been authenticated. false otherwise.
	 * @param tenant The tenant that the user was authenticated to.
	 * @param userName The name of the user that was authenticated, should match the userid in UMC.
	 */
	public SSOAuthenticationResult(boolean authenticated, String tenant, String userName) {
		log.debug("constructor called with authenticated: " + authenticated + " tenant:" + tenant + " userName:" + userName);	
		this.tenant = tenant;
		this.userName = userName;
		this.authenticated = authenticated;
	}

	/**
	 * Retrieves the tenant set in the overloaded constructor.
	 * 
	 * @see com.aris.umc.authentication.spi.ISsoResult#getTenant()
	 */
	@Override
	public String getTenant() {
		log.debug("getTenant called. Returning: " + tenant);	
		return tenant;
	}

	/**
	 * Retrieves the user set in the overloaded constructor.
	 * 
	 * @see com.aris.umc.authentication.spi.ISsoResult#getUserName()
	 */
	@Override
	public String getUserName() {
		log.debug("getUserName called. Returning: " + userName);	
		return userName;
	}

	/**
	 * Retrieves the authentication flag set in the overloaded constructor.
	 * 
	 * @see com.aris.umc.authentication.spi.ISsoResult#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated() {
		log.debug("isAuthenticated called. Returning: " + authenticated);	
		return authenticated;
	}

}
