/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.config;

import org.karnak.backend.constant.EndPoint;
import org.karnak.backend.security.OpenIdConnectLogoutHandler;
import org.karnak.backend.util.SecurityUtil;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.context.ShutdownEndpoint;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
@ConditionalOnProperty(value = "IDP", havingValue = "oidc")
public class SecurityOpenIdConnectConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// Disables cross-site request forgery (CSRF) protection for main route
			.csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher(EndPoint.ALL_REMAINING_PATH)))
			// Turns on/off authorizations
			.authorizeHttpRequests(authorize -> authorize
				// Actuator, health, info
				.requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/**"))
				.permitAll()
				.requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class))
				.permitAll()
				// Allows all internal traffic from the Vaadin framework
				.requestMatchers(SecurityUtil::isFrameworkInternalRequest)
				.permitAll()
				// Allow endpoints
				.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/echo/destinations"))
				.permitAll()
				// Deny
				.requestMatchers(EndpointRequest.to(ShutdownEndpoint.class))
				.denyAll()
				// Allows all authenticated traffic
				.anyRequest()
				.authenticated())
			// OpenId connect login
			.oauth2Login(Customizer.withDefaults())
			// Handle logout
			.logout(logout -> logout.addLogoutHandler(new OpenIdConnectLogoutHandler()));

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		// Access to static resources, bypassing Spring security.
		return (web) -> web.ignoring()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/VAADIN/**"),
					AntPathRequestMatcher.antMatcher("/img/**"), AntPathRequestMatcher.antMatcher("/icons/**"),
					AntPathRequestMatcher.antMatcher("/sw.js"), AntPathRequestMatcher.antMatcher("/favicon.ico"),
					AntPathRequestMatcher.antMatcher("/manifest.webmanifest"),
					AntPathRequestMatcher.antMatcher("/offline.html"),
					AntPathRequestMatcher.antMatcher("/sw-runtime-resources-precache.js"));
	}

}
