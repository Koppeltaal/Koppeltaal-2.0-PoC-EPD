/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nl.koppeltaal.poc.oidc.service.OidcClientService;
import nl.koppeltaal.poc.utils.UrlUtils;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuthorizationUrlDto;
import org.junit.Assert;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 */
@SuppressWarnings("SameReturnValue")
@Controller

public class LoginController {

	final OidcClientService oidcClientService;

	public LoginController(OidcClientService oidcClientService) {
		this.oidcClientService = oidcClientService;
	}

	@SuppressWarnings("SameReturnValue")
	@RequestMapping("code_response")
	public String codeResponse(HttpSession httpSession, HttpServletRequest request, String code, String state) throws IOException {
		Object stateAttribute = httpSession.getAttribute("state");
		if (stateAttribute != null) {
			Assert.assertEquals(state, stateAttribute);
		}
		oidcClientService.getIdToken(code, UrlUtils.getServerUrl("/code_response", request), new SessionTokenStorage(httpSession));

		return "redirect:index.html";

	}

	@RequestMapping("/login")
	public View login(HttpSession httpSession, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		AuthorizationUrlDto authorizationUrl = oidcClientService.getAuthorizationUrl(UrlUtils.getServerUrl("", request), UrlUtils.getServerUrl("/code_response", request));
		httpSession.setAttribute("state", authorizationUrl.getState());
		System.out.println(authorizationUrl.getState());
		redirectAttributes.addAllAttributes(authorizationUrl.getParameters());
		return new RedirectView(authorizationUrl.getUrl());
	}

	@RequestMapping("/logout")
	public String logout(HttpSession httpSession, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		httpSession.removeAttribute("credentials");
		return "redirect:index.html";
	}

}
