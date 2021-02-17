/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.poc.epd.configuration;

import brave.Tracing;
import brave.httpclient.TracingHttpClientBuilder;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.apache.ApacheRestfulClientFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@Configuration
@ConfigurationProperties(prefix = "fhir.client")
public class FhirClientConfiguration {
	String baseUrl;
	String clientId;

	@Bean
	public FhirContext fhirContext(@Autowired Tracing tracing) {
		FhirContext fhirContext = FhirContext.forR4();
		fhirContext.setRestfulClientFactory(new ApacheRestfulClientFactory(fhirContext) {

			private HttpClient myHttpClient;
			private HttpHost myProxy;

			@Override
			public HttpClient getNativeHttpClient() {
				if (myHttpClient == null) {

					PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(5000,
							TimeUnit.MILLISECONDS);
					connectionManager.setMaxTotal(getPoolMaxTotal());
					connectionManager.setDefaultMaxPerRoute(getPoolMaxPerRoute());

					//TODO: Use of a deprecated method should be resolved.
					RequestConfig defaultRequestConfig =
							RequestConfig.custom()
									.setSocketTimeout(getSocketTimeout())
									.setConnectTimeout(getConnectTimeout())
									.setConnectionRequestTimeout(getConnectionRequestTimeout())
									.setStaleConnectionCheckEnabled(true)
									.setProxy(myProxy)
									.build();

					HttpClientBuilder builder = TracingHttpClientBuilder.create(tracing).setConnectionManager(connectionManager)
							.setDefaultRequestConfig(defaultRequestConfig).disableCookieManagement();

					if (myProxy != null && StringUtils.isNotBlank(getProxyUsername()) && StringUtils.isNotBlank(getProxyPassword())) {
						CredentialsProvider credsProvider = new BasicCredentialsProvider();
						credsProvider.setCredentials(new AuthScope(myProxy.getHostName(), myProxy.getPort()),
								new UsernamePasswordCredentials(getProxyUsername(), getProxyPassword()));
						builder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
						builder.setDefaultCredentialsProvider(credsProvider);
					}

					myHttpClient = builder.build();

				}

				return myHttpClient;
			}

			/**
			 * Only allows to set an instance of type org.apache.http.client.HttpClient
			 * @see ca.uhn.fhir.rest.client.api.IRestfulClientFactory#setHttpClient(Object)
			 */
			@Override
			public synchronized void setHttpClient(Object theHttpClient) {
				this.myHttpClient = (HttpClient) theHttpClient;
			}

			@Override
			public void setProxy(String theHost, Integer thePort) {
				if (theHost != null) {
					myProxy = new HttpHost(theHost, thePort, "http");
				} else {
					myProxy = null;
				}
			}

			@Override
			protected void resetHttpClient() {
				this.myHttpClient = null;
			}

		});
		return fhirContext;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
