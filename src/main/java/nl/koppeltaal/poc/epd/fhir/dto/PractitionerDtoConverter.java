/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.fhir.dto;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *
 */
@Component
public class PractitionerDtoConverter implements DtoConverter<PractitionerDto, Practitioner> {

	public void applyDto(Practitioner practitioner, PractitionerDto practitionerDto) {
		practitioner.addIdentifier(createIdentifier(practitionerDto.getIdentifierSystem(), practitionerDto.getIdentifierValue()));

		practitioner.setActive(practitionerDto.isActive());

		// TODO: dangerous code...
		practitioner.getTelecom().clear();

		String email = practitionerDto.getEmail();
		if (StringUtils.isNotEmpty(email)) {
			addTelecomEmail(practitioner.addTelecom(), email, ContactPoint.ContactPointUse.WORK);
		}
		String phone = practitionerDto.getPhone();
		if (StringUtils.isNotEmpty(phone)) {
			addTelecomPhone(practitioner.addTelecom(), phone, ContactPoint.ContactPointUse.WORK);
		}

		// TODO: dangerous code...
		practitioner.getAddress().clear();

		String addressLines = practitionerDto.getAddressLines();
		String addressCity = practitionerDto.getAddressCity();
		String addressPostalCode = practitionerDto.getAddressPostalCode();
		String addressCountry = practitionerDto.getAddressCountry();

		if (!StringUtils.isAllEmpty(addressLines, addressCity, addressPostalCode, addressCountry)) {
			Address address = practitioner.addAddress();
			for (String line : unjoinAdressLine(addressLines)) {
				address.addLine(line);
			}
			address.setCity(addressCity);
			address.setPostalCode(addressPostalCode);
		}


		String gender = practitionerDto.getGender();
		if (StringUtils.isNotEmpty(gender)) {
			practitioner.setGender(Enumerations.AdministrativeGender.fromCode(gender));
		}

		practitioner.setBirthDate(practitionerDto.getBirthDate());

		practitioner.getName().clear();
		HumanName humanName = practitioner.addName();
		humanName.setFamily(practitionerDto.getNameFamily());
		if (StringUtils.isNotEmpty(practitionerDto.getNameGiven())) {
			humanName.getGiven().clear();
			for (String givenName : StringUtils.split(practitionerDto.getNameGiven())) {
				humanName.addGiven(givenName);
			}
		}

	}

	private List<String> unjoinAdressLine(String addressLines) {
		return Arrays.asList(StringUtils.split(addressLines, "\n"));
	}


	public void applyResource(PractitionerDto practitionerDto, Practitioner practitioner) {

		practitionerDto.setId(getRelativeReference(practitioner.getIdElement()));

		List<Identifier> identifiers = practitioner.getIdentifier();
		for (Identifier identifier : identifiers) {
			practitionerDto.setIdentifierSystem(identifier.getSystem());
			practitionerDto.setIdentifierValue(identifier.getValue());
		}

		practitionerDto.setActive(practitioner.getActive());
		List<ContactPoint> telecoms = practitioner.getTelecom();
		for (ContactPoint telecom : telecoms) {
			if (StringUtils.equals(telecom.getSystem().toCode(), "email")) {
				if (StringUtils.equals(telecom.getUse().toCode(), "work")) {
					practitionerDto.setEmail(telecom.getValue());
				}
			} else if (StringUtils.equals(telecom.getSystem().toCode(), "phone")) {
				if (StringUtils.equals(telecom.getUse().toCode(), "work")) {
					practitionerDto.setPhone(telecom.getValue());
				}
			}
		}

		for (Address address : practitioner.getAddress()) {
			practitionerDto.setAddressLines(joinAdressLines(address));
			practitionerDto.setAddressCity(address.getCity());
			practitionerDto.setAddressPostalCode(address.getPostalCode());
			practitionerDto.setAddressCountry(address.getCountry());
			break;
		}

		Enumerations.AdministrativeGender gender = practitioner.getGender();
		if (gender != null) {
			practitionerDto.setGender(gender.toCode());
		} else {
			practitionerDto.setGender(Enumerations.AdministrativeGender.UNKNOWN.toCode());
		}


		Date birthDate = practitioner.getBirthDate();
		practitionerDto.setBirthDate(birthDate);

		for (HumanName humanName : practitioner.getName()) {
			practitionerDto.setNameFamily(humanName.getFamily());
			practitionerDto.setNameGiven(humanName.getGivenAsSingleString());
			break;
		}

	}

	private String joinAdressLines(Address address) {
		String addressLine = "";
		for (StringType stringType : address.getLine()) {
			if (StringUtils.isNotEmpty(addressLine)) {
				addressLine += "\n";
			}
			addressLine += stringType.getValue();
		}
		return addressLine;
	}

	public PractitionerDto convert(Practitioner practitioner) {
		PractitionerDto practitionerDto = new PractitionerDto();

		applyResource(practitionerDto, practitioner);


		return practitionerDto;
	}

	public Practitioner convert(PractitionerDto practitionerDto) {
		Practitioner practitioner = new Practitioner();

		applyDto(practitioner, practitionerDto);
		return practitioner;
	}


	private Identifier createIdentifier(String system, String value) {
		Identifier identifier = new Identifier();
		Identifier.IdentifierUse use = Identifier.IdentifierUse.OFFICIAL;
		identifier.setSystem(system);
		identifier.setValue(value);
		identifier.setUse(use);
		return identifier;
	}

	private String getRelativeReference(IIdType idElement) {
		return idElement.toUnqualifiedVersionless().getIdPart();
	}


}
