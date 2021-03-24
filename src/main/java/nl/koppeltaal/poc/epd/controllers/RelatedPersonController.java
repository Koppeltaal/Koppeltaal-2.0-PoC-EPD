/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import nl.koppeltaal.poc.fhir.dto.RelatedPersonDto;
import nl.koppeltaal.poc.fhir.dto.RelatedPersonDtoConverter;
import nl.koppeltaal.poc.fhir.service.RelatedPersonFhirClientService;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController()
@RequestMapping("/api/RelatedPerson")
public class RelatedPersonController extends BaseResourceController<RelatedPersonDto, RelatedPerson> {

	public RelatedPersonController(RelatedPersonFhirClientService fhirClientService, RelatedPersonDtoConverter dtoConverter) {
		super(fhirClientService, dtoConverter);
	}

}
