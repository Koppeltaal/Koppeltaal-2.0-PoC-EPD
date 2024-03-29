/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import nl.koppeltaal.spring.boot.starter.smartservice.dto.OrganizationDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.OrganizationDtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.service.fhir.OrganizationFhirClientService;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController()
@RequestMapping("/api/Organization")
public class OrganizationsController extends BaseResourceController<OrganizationDto, Organization> {

	public OrganizationsController(OrganizationFhirClientService fhirClientService, OrganizationDtoConverter dtoConverter) {
		super(fhirClientService, dtoConverter);
	}

}
