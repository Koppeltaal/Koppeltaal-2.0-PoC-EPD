/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.fhir.service;

import ca.uhn.fhir.context.FhirContext;
import nl.koppeltaal.poc.fhir.dto.LocationDto;
import nl.koppeltaal.poc.fhir.dto.LocationDtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.service.fhir.SmartClientCredentialService;
import org.hl7.fhir.r4.model.Location;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class LocationFhirClientService extends BaseFhirClientService<LocationDto, Location> {

	public LocationFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, LocationDtoConverter locationDtoConverter) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, locationDtoConverter);
	}

	@Override
	protected String getResourceName() {
		return "Location";
	}

	protected String getDefaultSystem() {
		return "urn:ietf:rfc:3986";
	}

}
