package nl.koppeltaal.poc.generic;

import nl.koppeltaal.poc.fhir.service.Oauth2ClientService;

/**
 *
 */
public interface TokenStorage {
	void clear();

	Oauth2TokenResponse getToken();

	void updateToken(Oauth2TokenResponse token);
}
