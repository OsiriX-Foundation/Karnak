/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.api.rqbody.Fields;
import org.karnak.backend.config.MainzellisteConfig;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class MainzellisteApiTest {

  MockedStatic<MainzellisteConfig> mainzellisteConfigMock;

  @BeforeEach
  void setUp() {
    MainzellisteConfig mainzellisteConfig = new MainzellisteConfig();
    mainzellisteConfig.setServerurl("http://serverUrl");
    mainzellisteConfig.setApikey("apiKey");
    mainzellisteConfigMock = Mockito.mockStatic(MainzellisteConfig.class);
    mainzellisteConfigMock.when(MainzellisteConfig::getInstance).thenReturn(mainzellisteConfig);
  }

  @AfterEach
  void tearDown() {
    // Close static mock
    if (mainzellisteConfigMock != null) {
      mainzellisteConfigMock.close();
    }
  }

  @Test
  void when_add_ext_id_server_invalid_should_throw_exception() {

    // Init data
    MainzellisteApi pseudonymApi = new MainzellisteApi();
    Fields fields = new Fields("patientId");

    Assertions.assertThrows(
        IllegalStateException.class,
        () -> {
          // Call method
          pseudonymApi.addExternalID(fields, "externalPseudonym");
        });
  }

  @Test
  void when_get_ext_id_server_invalid_should_throw_exception() {

    // Init data
    MainzellisteApi pseudonymApi = new MainzellisteApi();
    Fields fields = new Fields("patientId");

    Assertions.assertThrows(
        IllegalStateException.class,
        () -> {
          // Call method
          pseudonymApi.getExistingExternalID(fields);
        });
  }

  @Test
  void when_generate_pid_server_invalid_should_throw_exception() {
    // Init data
    MainzellisteApi pseudonymApi = new MainzellisteApi();
    Fields fields = new Fields("patientId");

    Assertions.assertThrows(
        IllegalStateException.class,
        () -> {
          // Call method
          pseudonymApi.generatePID(fields);
        });
  }
}
