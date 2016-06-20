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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.aris.umc.authentication.spi.ISsoResult;
import com.aris.umc.authentication.spi.ISsoTokenValidator;

/**
 * Implementation of the <code>ISsoTokenValidator</code> interface. This
 * implementation verifies SSO tokens passed from web agents such as Oracle
 * Access Manager and SiteMinder, that pass user IDs in request headers. It also
 * verifies the SSO token passed from the Architect download client.
 * 
 * @author Christopher Steel - Software AG Government Solutions
 * 
 * @since Jan 15, 2015 9:22:06 PM
 * @version 1.0
 */
public class SSOTokenValidator implements ISsoTokenValidator {
	protected String SSO_HEADER_NAME = "SM_USER";
	protected String tenant = "default";
	private Logger log = SSOPluginLogger.getLogger(SSOTokenValidator.class);
	protected SSOProperties properties = SSOProperties.getInstance();

	public SSOTokenValidator() {
		log.debug("constructor called.");
	}

	/**
	 * This method is invoked by the business server (abs) in order to
	 * authenticate the download client (Architect). The download client will be
	 * called via the {@link #SSOTokenProvider()} method acquireSsoToken. The
	 * token created by the download client will be passed into this method for
	 * validation.
	 * 
	 * @param tenant
	 *            The tenant to validate against. This is used to create the
	 *            result
	 * @param token
	 *            The token created by the download client in the
	 *            acquireSsoToken method.
	 * @return The SSOAuthenticationResult with the auth result, tenant and
	 *         token as a String.
	 */
	@Override
	public ISsoResult verifySsoToken(String tenant, byte[] token) {
		String tokenStr = "Unknown";
		if (token != null) {
			tokenStr = new String(token);
			log.debug("called with tenant = " + tenant + " and token = " + tokenStr);
		} else {
			log.debug("called with tenant = " + tenant + " and NULL token");
		}
		return new SSOAuthenticationResult(true, tenant, getArisUser(tokenStr));
	}

	/**
	 * This method is invoked when the Design Server detects a new connection
	 * and is setup to authenticate via SSO. It is used for server-side
	 * authentication where a header or request parameter is assumed to contain
	 * the SSO token. This implementation presumes the ARIS user id is passed in
	 * the request via a request header. The name of the header attribute is
	 * configurable using the setSsoHeaderName method.
	 * 
	 * @param requestUri
	 *            The request URI passed in. Currently not used.
	 * @param parameters
	 *            The request parameters. Currently not used.
	 * @param headers
	 *            The request headers, used to retrieve the user id to
	 *            authenticate with.http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=1799756
	 * 
	 * @return The SSOAuthenticationResult indicating true if the appropriate
	 *         header is found.
	 */
	@Override
	public ISsoResult verifySsoToken(String requestUri, Map<String, String[]> parameters, Map<String, String[]> headers) {
		log.debug("called with requestUri = " + requestUri + " parameters = " + parameters + " headers = ");
		String ssoHeaderName = getSsoHeaderName();
		if (log.isDebugEnabled()) {
			// Log all headers passed in the request
			for (String header : headers.keySet()) {
				log.debug("\t" + header + " : ");
				for (String value : headers.get(header)) {
					log.debug("\t\t" + value + " ");
				}
			}
		}

		if (headers.containsKey(ssoHeaderName)) {
			String userId = headers.get(ssoHeaderName)[0];
			// TODO: Check if there is an API to validate the user is valid in ARIS.
			String userName = getArisUser(userId);
			log.debug("SSO Authentication succeeded for user id: " + userName);
			return new SSOAuthenticationResult(true, tenant, userName);
		}

		log.debug("SSO Authentication failed. Did not find " + ssoHeaderName + " in request header.");
		return new SSOAuthenticationResult(false, tenant, null);
	}

	/**
	 * Gets the ARIS user name as it will appear in UMC. Based on configuration
	 * properties it will parse or strip out the domain altogether. For a given
	 * token domain.xyz.com/user it will return domain.xyz.com\\user,
	 * domain\\user, or user, based on properties set in the SSOProperties
	 * calls.
	 * 
	 * @param tokenStr
	 *            The user token consisting of just the user or domain\\user
	 * @return The ARIS user name.
	 */
	protected String getArisUser(String tokenStr) {
		String user = getUserName(tokenStr);
		String domain = getDomain(tokenStr);
		if (domain != null && Boolean.parseBoolean(properties.getProperty("sso.validator.use_domain", "true"))) {
			return domain + "\\" + user;
		} else
			return user;
	}

	/**
	 * Gets the Windows domain portion of the SSO token. If the property
	 * sso.validator.trim_domain is set to true, it will strip out everything
	 * after the first dot. For example foo.bar.com/user would return foo. If
	 * set to false, the fully qualified domain will be returned (foo.bar.com).
	 * 
	 * @param token
	 *            The SSO token to be parsed. Assumed to be in the format
	 *            domain\\user or domain.xyz.com\\user.
	 * @return Either the short name of the domain or the full qualified name,
	 *         depending on the property sso.validator.trim_domain.
	 */
	protected String getDomain(String token) {
		log.debug("called with token: " + token);
		String domain = null;
		StringTokenizer tokenizer = new StringTokenizer(token, "/,\\");
		if (tokenizer.countTokens() > 1) {
			domain = tokenizer.nextToken();
		} else {
			log.warn("No domain found for token: " + token);
			return domain;
		}
		if (Boolean.parseBoolean(properties.getProperty("sso.validator.trim_domain", "true"))) {
			StringTokenizer domainTokenizer = new StringTokenizer(token, ".");
			if (domainTokenizer.countTokens() > 1) {
				log.debug("Trimming domain: " + domain);
				domain = domainTokenizer.nextToken();
			}
		}
		log.debug("returning domain: " + domain);
		return domain;
	}

	/**
	 * Gets the user name portion of the SSO token.
	 * 
	 * @param token
	 *            The SSO token to be parsed. Assumed to be in the format
	 *            domain\\user or domain.xyz.com\\user.
	 * @return The user name contained in the SSO token.
	 */
	protected String getUserName(String token) {
		log.debug("called with token: " + token);
		StringTokenizer tokenizer = new StringTokenizer(token, "/,\\");
		if (tokenizer.countTokens() > 1) {
			tokenizer.nextToken();
			String user = tokenizer.nextToken();
			log.debug("returning stripped user: " + user);
			return user;
		}
		log.debug("returning user: " + token);
		return token;

	}

	/**
	 * Gets the name of the request header used that contains the SSO token.
	 * 
	 * @return the ssoHeaderName
	 */
	public String getSsoHeaderName() {
		log.debug("called.");
		String headerName = properties.getProperty("sso.validator.header_name", SSO_HEADER_NAME);
		log.debug("returning: " + headerName);
		return headerName;
	}

}
