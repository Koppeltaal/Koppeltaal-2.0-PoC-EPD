/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import com.auth0.jwk.JwkException;
import nl.koppeltaal.poc.fhir.dto.OrganizationDto;
import nl.koppeltaal.poc.fhir.exception.EnitityNotFoundException;
import nl.koppeltaal.poc.fhir.dto.OrganizationDtoConverter;
import nl.koppeltaal.poc.fhir.service.OrganizationFhirClientService;
import nl.koppeltaal.poc.utils.UrlUtils;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RestController()
@RequestMapping("/api/organizations")
public class OrganizationsController {


	final OrganizationFhirClientService fhirClientService;
	final OrganizationDtoConverter dtoConverter;

	public OrganizationsController(OrganizationFhirClientService fhirClientService, OrganizationDtoConverter dtoConverter) {
		this.fhirClientService = fhirClientService;
		this.dtoConverter = dtoConverter;
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<OrganizationDto> list(HttpSession httpSession) throws IOException, JwkException {
		List<OrganizationDto> rv = new ArrayList<>();
		List<Organization> organizations = fhirClientService.getResources(new SessionTokenStorage(httpSession));
		for (Organization organization : organizations) {
			rv.add(dtoConverter.convert(organization));
		}
		return rv;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public OrganizationDto put(HttpSession httpSession, HttpServletRequest request, @RequestBody OrganizationDto organizationDto) throws IOException, JwkException {
		return dtoConverter.convert(fhirClientService.storeResource(new SessionTokenStorage(httpSession), UrlUtils.getServerUrl("", request), dtoConverter.convert(organizationDto)));
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		fhirClientService.deleteResource(new SessionTokenStorage(httpSession), id);
	}

	@RequestMapping(value = "Organization/{id}", method = RequestMethod.DELETE)
	public void deletebyReference(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		fhirClientService.deleteResourceByReference(new SessionTokenStorage(httpSession), id);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public OrganizationDto get(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		Organization organization = fhirClientService.getResourceByIdentifier(new SessionTokenStorage(httpSession), id);
		if (organization != null) {
			return dtoConverter.convert(organization);
		} else {
			throw new EnitityNotFoundException("Cannot locate organization " + id);
		}
	}

	@RequestMapping(value = "/Organization/{id}", method = RequestMethod.GET)
	public OrganizationDto getByReference(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		Organization organization = fhirClientService.getResourceByReference(new SessionTokenStorage(httpSession), id);
		if (organization != null) {
			return dtoConverter.convert(organization);
		} else {
			throw new EnitityNotFoundException("Cannot locate organization " + id);
		}
	}

}
