/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import com.auth0.jwk.JwkException;
import nl.koppeltaal.poc.fhir.dto.LocationDto;
import nl.koppeltaal.poc.fhir.dto.LocationDtoConverter;
import nl.koppeltaal.poc.fhir.exception.EnitityNotFoundException;
import nl.koppeltaal.poc.fhir.service.LocationFhirClientService;
import nl.koppeltaal.poc.utils.UrlUtils;
import org.hl7.fhir.r4.model.Location;
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
@RequestMapping("/api/locations")
public class LocationsController {


	final LocationFhirClientService fhirClientService;
	final LocationDtoConverter dtoConverter;

	public LocationsController(LocationFhirClientService fhirClientService, LocationDtoConverter dtoConverter) {
		this.fhirClientService = fhirClientService;
		this.dtoConverter = dtoConverter;
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<LocationDto> list(HttpSession httpSession) throws IOException, JwkException {
		List<LocationDto> rv = new ArrayList<>();
		List<Location> locations = fhirClientService.getResources(new SessionTokenStorage(httpSession));
		for (Location location : locations) {
			rv.add(dtoConverter.convert(location));
		}
		return rv;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public LocationDto put(HttpSession httpSession, HttpServletRequest request, @RequestBody LocationDto locationDto) throws IOException, JwkException {
		return dtoConverter.convert(fhirClientService.storeResource(new SessionTokenStorage(httpSession), UrlUtils.getServerUrl("", request), dtoConverter.convert(locationDto)));
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		fhirClientService.deleteResource(new SessionTokenStorage(httpSession), id);
	}

	@RequestMapping(value = "Location/{id}", method = RequestMethod.DELETE)
	public void deletebyReference(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		fhirClientService.deleteResourceByReference(new SessionTokenStorage(httpSession), id);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public LocationDto get(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		Location location = fhirClientService.getResourceByReference(new SessionTokenStorage(httpSession), id);
		if (location != null) {
			return dtoConverter.convert(location);
		} else {
			throw new EnitityNotFoundException("Cannot locate location " + id);
		}
	}
	@RequestMapping(value = "/Location/{id}", method = RequestMethod.GET)
	public LocationDto getByReference(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		Location location = fhirClientService.getResourceByReference(new SessionTokenStorage(httpSession), id);
		if (location != null) {
			return dtoConverter.convert(location);
		} else {
			throw new EnitityNotFoundException("Cannot locate location " + id);
		}
	}

}
