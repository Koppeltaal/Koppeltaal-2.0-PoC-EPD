/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 *
 */
@Controller
@RequestMapping("/")
public class IndexController {
	@RequestMapping(value = {"/"})
	public String index(HttpSession httpSession) {
		if (httpSession.getAttribute("credentials")  == null) {
			return "redirect:/login";
		}
		return "index.html";
	}
}
