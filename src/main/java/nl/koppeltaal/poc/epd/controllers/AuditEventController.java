/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.service.fhir.AuditEventFhirClientService;
import org.hl7.fhir.r4.model.AuditEvent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController()
@RequestMapping(value = "/api/AuditEvent")
public class AuditEventController extends BaseResourceController<AuditEventDto, AuditEvent> {

	public AuditEventController(AuditEventFhirClientService fhirClientService, AuditEventDtoConverter dtoConverter) {
		super(fhirClientService, dtoConverter);
	}

	@Override
	public SortSpec getSortSpec() {
		return new SortSpec("date", SortOrderEnum.DESC);
	}
}
