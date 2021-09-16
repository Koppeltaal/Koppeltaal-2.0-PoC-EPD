/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.controllers;

import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.CodeableConceptDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.CodingDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.EndpointDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.EndpointDtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.service.fhir.EndpointFhirClientService;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Endpoint;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController()
@RequestMapping("/api/Endpoint")
public class EndpointsController extends BaseResourceController<EndpointDto, Endpoint> {

	public EndpointsController(EndpointFhirClientService fhirClientService, EndpointDtoConverter dtoConverter) {
		super(fhirClientService, dtoConverter);
	}

	@Override
	public EndpointDto put(HttpServletRequest request, @RequestBody EndpointDto activitydefinitionDto) throws IOException {

		final Coding endpointType = new Coding();
		endpointType.setSystem("http://terminology.hl7.org/CodeSystem/endpoint-payload-type");
		endpointType.setCode("any");

		final CodeableConceptDto codeableConceptDto = new CodeableConceptDto();
		codeableConceptDto.addCoding(new CodingDto(endpointType));

		activitydefinitionDto.setPayloadType(Collections.singletonList(codeableConceptDto));

		final Coding connectionType = new Coding();
		connectionType.setSystem("http://terminology.hl7.org/CodeSystem/endpoint-connection-type");
		connectionType.setCode("hl7-fhir-rest");

		activitydefinitionDto.setConnectionType(new CodingDto(connectionType));

		return super.put(request, activitydefinitionDto);
	}
}
