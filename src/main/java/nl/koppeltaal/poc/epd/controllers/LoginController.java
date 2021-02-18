/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import nl.koppeltaal.poc.fhir.dto.AuthorizationUrlDto;
import nl.koppeltaal.poc.fhir.service.Oauth2ClientService;
import nl.koppeltaal.poc.utils.UrlUtils;
import org.junit.Assert;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 */
@Controller

public class LoginController {

	final Oauth2ClientService oauth2ClientService;

	public LoginController(Oauth2ClientService oauth2ClientService) {
		this.oauth2ClientService = oauth2ClientService;
	}

	@RequestMapping("code_response")
	public String codeResponse(HttpSession httpSession, HttpServletRequest request, String code, String state) throws IOException {
		Assert.assertEquals(state, httpSession.getAttribute("state"));
		oauth2ClientService.getToken(code, UrlUtils.getServerUrl("/code_response", request), new SessionTokenStorage(httpSession));

		return "redirect:index.html";

	}

	@RequestMapping("/login")
	public View login(HttpSession httpSession, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		AuthorizationUrlDto authorizationUrl = oauth2ClientService.getAuthorizationUrl(UrlUtils.getServerUrl("", request), UrlUtils.getServerUrl("/code_response", request));
		httpSession.setAttribute("state", authorizationUrl.getState());
		redirectAttributes.addAllAttributes(authorizationUrl.getParameters());
		return new RedirectView(authorizationUrl.getUrl());
	}

	@RequestMapping("/logout")
	public String logout(HttpSession httpSession, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		httpSession.removeAttribute("credentials");
		return "redirect:index.html";
	}

}
