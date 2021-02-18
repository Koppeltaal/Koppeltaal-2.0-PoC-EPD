/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.fhir.service;

import ca.uhn.fhir.context.FhirContext;
import nl.koppeltaal.poc.fhir.configuration.FhirClientConfiguration;
import nl.koppeltaal.poc.fhir.dto.PatientDto;
import nl.koppeltaal.poc.fhir.dto.PatientDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class PatientFhirClientService extends BaseFhirClientService<PatientDto, Patient> {

	public PatientFhirClientService(FhirClientConfiguration fhirClientConfiguration, Oauth2ClientService oauth2ClientService, FhirContext fhirContext, PatientDtoConverter patientDtoConverter) {
		super(fhirClientConfiguration, oauth2ClientService, fhirContext, patientDtoConverter);
	}

	protected String getIdentifier(String system, Patient resource) {
		for (Identifier identifier : resource.getIdentifier()) {
			if (StringUtils.equals(identifier.getSystem(), system)) {
				return identifier.getValue();
			}
		}
		return null;
	}

	@Override
	protected String getResourceName() {
		return "Patient";
	}


}