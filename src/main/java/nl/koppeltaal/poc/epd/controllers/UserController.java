/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import nl.koppeltaal.poc.epd.dto.UserDto;
import nl.koppeltaal.poc.oidc.service.OidcClientService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 *
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

	final OidcClientService oidcClientService;

	public UserController(OidcClientService oidcClientService) {
		this.oidcClientService = oidcClientService;
	}

	@RequestMapping(value = "current", method = RequestMethod.GET)
	public UserDto getUser(HttpSession httpSession) throws JwkException {
		UserDto rv = new UserDto();
		SessionTokenStorage tokenStorage = new SessionTokenStorage(httpSession);
		if (tokenStorage.hasIdToken()) {
			try {
				rv.setUserId(oidcClientService.getUserIdFromCredentials(tokenStorage));
				rv.setUserIdentifier(oidcClientService.getUserIdentifierFromCredentials(tokenStorage));
				rv.setLoggedIn(true);
			} catch (TokenExpiredException e) {
				rv.setLoggedIn(false);
			}
		} else {
			rv.setLoggedIn(false);
		}
		return rv;
	}
}
