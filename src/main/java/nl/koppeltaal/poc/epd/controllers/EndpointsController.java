/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import com.auth0.jwk.JwkException;
import nl.koppeltaal.poc.fhir.dto.EndpointDto;
import nl.koppeltaal.poc.fhir.dto.EndpointDtoConverter;
import nl.koppeltaal.poc.fhir.exception.EnitityNotFoundException;
import nl.koppeltaal.poc.fhir.service.EndpointFhirClientService;
import nl.koppeltaal.poc.utils.UrlUtils;
import org.hl7.fhir.r4.model.Endpoint;
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
@RequestMapping("/api/endpoints")
public class EndpointsController {


	final EndpointFhirClientService fhirClientService;
	final EndpointDtoConverter dtoConverter;

	public EndpointsController(EndpointFhirClientService fhirClientService, EndpointDtoConverter dtoConverter) {
		this.fhirClientService = fhirClientService;
		this.dtoConverter = dtoConverter;
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<EndpointDto> list(HttpSession httpSession) throws IOException, JwkException {
		List<EndpointDto> rv = new ArrayList<>();
		List<Endpoint> endpoints = fhirClientService.getResources(new SessionTokenStorage(httpSession));
		for (Endpoint endpoint : endpoints) {
			rv.add(dtoConverter.convert(endpoint));
		}
		return rv;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public EndpointDto put(HttpSession httpSession, HttpServletRequest request, @RequestBody EndpointDto endpointDto) throws IOException, JwkException {
		return dtoConverter.convert(fhirClientService.storeResource(new SessionTokenStorage(httpSession), UrlUtils.getServerUrl("", request), dtoConverter.convert(endpointDto)));
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		fhirClientService.deleteResource(new SessionTokenStorage(httpSession), id);
	}

	@RequestMapping(value = "Endpoint/{id}", method = RequestMethod.DELETE)
	public void deletebyReference(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		fhirClientService.deleteResourceByReference(new SessionTokenStorage(httpSession), id);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public EndpointDto get(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		Endpoint endpoint = fhirClientService.getResourceByIdentifier(new SessionTokenStorage(httpSession), id);
		if (endpoint != null) {
			return dtoConverter.convert(endpoint);
		} else {
			throw new EnitityNotFoundException("Cannot locate endpoint " + id);
		}
	}
	@RequestMapping(value = "/Endpoint/{id}", method = RequestMethod.GET)
	public EndpointDto getByReference(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		Endpoint endpoint = fhirClientService.getResourceById(new SessionTokenStorage(httpSession), id);
		if (endpoint != null) {
			return dtoConverter.convert(endpoint);
		} else {
			throw new EnitityNotFoundException("Cannot locate endpoint " + id);
		}
	}
}
