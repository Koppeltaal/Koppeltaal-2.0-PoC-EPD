/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.fhir.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import com.auth0.jwk.JwkException;
import nl.koppeltaal.poc.fhir.dto.ActivityDefinitionDto;
import nl.koppeltaal.poc.fhir.dto.ActivityDefinitionDtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.service.fhir.SmartClientCredentialService;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.codesystems.PublicationStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 *
 */
@Service
public class ActivityDefinitionFhirClientService extends BaseFhirClientService<ActivityDefinitionDto, ActivityDefinition> {

	public ActivityDefinitionFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, ActivityDefinitionDtoConverter activityDefinitionDtoConverter) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, activityDefinitionDtoConverter);
	}

	public List<ActivityDefinition> getResourcesForPatient(String patientReference) throws IOException, JwkException {
		ICriterion<TokenClientParam> criterion = ActivityDefinition.STATUS.exactly().code(StringUtils.lowerCase(PublicationStatus.ACTIVE.name()));
		return getResources(criterion);
	}

	@Override
	protected String getDefaultSystem() {
		return "http:/vzvz.nl/artifacts";
	}

	@Override
	protected String getResourceName() {
		return "ActivityDefinition";
	}

}
