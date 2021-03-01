/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import com.auth0.jwk.JwkException;
import nl.koppeltaal.poc.fhir.dto.ActivityDefinitionDto;
import nl.koppeltaal.poc.fhir.dto.ActivityDefinitionDtoConverter;
import nl.koppeltaal.poc.fhir.exception.EnitityNotFoundException;
import nl.koppeltaal.poc.fhir.service.ActivityDefinitionFhirClientService;
import nl.koppeltaal.poc.utils.UrlUtils;
import org.hl7.fhir.r4.model.ActivityDefinition;
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
@RequestMapping("/api/activitydefinitions")
public class ActivityDefinitionController {


	final ActivityDefinitionFhirClientService fhirClientService;
	final ActivityDefinitionDtoConverter dtoConverter;

	public ActivityDefinitionController(ActivityDefinitionFhirClientService fhirClientService, ActivityDefinitionDtoConverter dtoConverter) {
		this.fhirClientService = fhirClientService;
		this.dtoConverter = dtoConverter;
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<ActivityDefinitionDto> list(HttpSession httpSession) throws IOException, JwkException {
		List<ActivityDefinitionDto> rv = new ArrayList<>();
		List<ActivityDefinition> activitydefinitions = fhirClientService.getResources(new SessionTokenStorage(httpSession));
		for (ActivityDefinition activitydefinition : activitydefinitions) {
			rv.add(dtoConverter.convert(activitydefinition));
		}
		return rv;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ActivityDefinitionDto put(HttpSession httpSession, HttpServletRequest request, @RequestBody ActivityDefinitionDto activitydefinitionDto) throws IOException, JwkException {
		return dtoConverter.convert(fhirClientService.storeResource(new SessionTokenStorage(httpSession), UrlUtils.getServerUrl("", request), dtoConverter.convert(activitydefinitionDto)));
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		fhirClientService.deleteResource(new SessionTokenStorage(httpSession), id);
	}

	@RequestMapping(value = "ActivityDefinition/{id}", method = RequestMethod.DELETE)
	public void deletebyReference(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		fhirClientService.deleteResourceByReference(new SessionTokenStorage(httpSession), id);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ActivityDefinitionDto get(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		ActivityDefinition activitydefinition = fhirClientService.getResourceByIdentifier(new SessionTokenStorage(httpSession), id);
		if (activitydefinition != null) {
			return dtoConverter.convert(activitydefinition);
		} else {
			throw new EnitityNotFoundException("Cannot locate activitydefinition " + id);
		}
	}
	@RequestMapping(value = "/ActivityDefinition/{id}", method = RequestMethod.GET)
	public ActivityDefinitionDto getByReference(HttpSession httpSession, @PathVariable String id) throws IOException, JwkException {
		ActivityDefinition activitydefinition = fhirClientService.getResourceById(new SessionTokenStorage(httpSession), id);
		if (activitydefinition != null) {
			return dtoConverter.convert(activitydefinition);
		} else {
			throw new EnitityNotFoundException("Cannot locate activitydefinition " + id);
		}
	}
}
