package nl.koppeltaal.poc.fhir.service;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.koppeltaal.poc.epd.configuration.OidcConfiguration;
import nl.koppeltaal.poc.generic.Oauth2TokenResponse;
import nl.koppeltaal.poc.jwt.JwtValidationService;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 *
 */
@Service
public class Oauth2ClientService {
	private final OidcConfiguration oidcConfiguration;
	private final JwtValidationService jwtValidationService;

	private Oauth2TokenResponse tokenResponse;


	public Oauth2ClientService(OidcConfiguration oidcConfiguration, JwtValidationService jwtValidationService) {
		this.oidcConfiguration = oidcConfiguration;
		this.jwtValidationService = jwtValidationService;
	}

	public void checkCredentials() throws JwkException, IOException {
		try {
			if (tokenResponse != null) {
				jwtValidationService.validate(tokenResponse.getAccessToken(), oidcConfiguration.getClientId(), 60);
			}
		} catch (TokenExpiredException e) {
			refreshToken();
		}
	}

	public void fetchToken() throws IOException {
		String tokenUrl = oidcConfiguration.getTokenUrl();
		try (CloseableHttpClient httpClient = createHttpClient()) {

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("grant_type", "client_credentials"));

			postTokenRequest(tokenUrl, httpClient, params);
		}

	}

	public String getAccessToken() throws JwkException, IOException {
		if (tokenResponse == null) {
			fetchToken();
		} else {
			checkCredentials();
		}
		return tokenResponse.getAccessToken();
	}

	public void refreshToken() throws IOException {
		String tokenUrl = oidcConfiguration.getTokenUrl();
		try (CloseableHttpClient httpClient = createHttpClient()) {

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("grant_type", "refresh_token"));
			params.add(new BasicNameValuePair("refresh_token", tokenResponse.getRefreshToken()));
			params.add(new BasicNameValuePair("scope", "openid user/Patient.* user/Practitioner.*"));

			postTokenRequest(tokenUrl, httpClient, params);
		}

	}

	private CloseableHttpClient createHttpClient() {
		return HttpClients.createDefault();
	}

	private void postTokenRequest(String tokenUrl, CloseableHttpClient httpClient, List<NameValuePair> params) throws IOException {
		final HttpPost httpPost = new HttpPost(tokenUrl);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", oidcConfiguration.getClientId(), oidcConfiguration.getClientSecret()).getBytes(StandardCharsets.US_ASCII)));
		httpPost.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpResponse response = httpClient.execute(httpPost);
		try (InputStream in = response.getEntity().getContent()) {
			String content = IOUtils.toString(new InputStreamReader(in, Charset.defaultCharset()));
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				tokenResponse = objectMapper.readValue(content, Oauth2TokenResponse.class);
			} catch (JsonParseException e) {
				System.out.println(content);
				throw e;
			}
		}
	}
}
