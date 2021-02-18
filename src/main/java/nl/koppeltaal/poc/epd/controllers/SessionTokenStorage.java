/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import nl.koppeltaal.poc.fhir.service.Oauth2ClientService;
import nl.koppeltaal.poc.generic.TokenStorage;

import javax.servlet.http.HttpSession;

/**
 *
 */
public class SessionTokenStorage implements TokenStorage {
	final HttpSession httpSession;

	public SessionTokenStorage(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	@Override
	public Oauth2ClientService.Oauth2TokenResponse getToken() {
		return (Oauth2ClientService.Oauth2TokenResponse) httpSession.getAttribute("credentials");
	}

	@Override
	public void updateToken(Oauth2ClientService.Oauth2TokenResponse token) {
		httpSession.setAttribute("credentials", token);
	}

	public boolean hasToken() {
		return httpSession.getAttribute("credentials") != null;
	}

	@Override
	public void clear() {
		httpSession.removeAttribute("credentials");
	}
}
