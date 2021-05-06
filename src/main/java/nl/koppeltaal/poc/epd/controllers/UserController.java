/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import com.auth0.jwk.JwkException;
import nl.koppeltaal.poc.epd.dto.UserDto;
import nl.koppeltaal.poc.fhir.service.OidcClientService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
			rv.setUserId(oidcClientService.getUserIdFromCredentials(tokenStorage));
			rv.setUserIdentifier(oidcClientService.getUserIdentifierFromCredentials(tokenStorage));
			rv.setLoggedIn(true);
		} else {
			rv.setLoggedIn(false);
		}
		return rv;
	}
}
