/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExternalIDProviderPatientTest {
  static ExternalIDProviderPatient patient1;
  static ExternalIDProviderPatient patient2;
  static ExternalIDProviderPatient patient3;
  static ExternalIDProviderPatient patient4;
  static ExternalIDProviderPatient patient5;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    patient1 = new ExternalIDProviderPatient("pseudonym", "mykasa", "^Kenny", "DPA", 1L);
    patient2 = new ExternalIDProviderPatient("pseudonym", "mykasa", "Super^Homer", "DPA", 1L);
    patient2.setPatientId("12345");
    patient2.setPatientName("Super^Homer");
    patient2.setIssuerOfPatientId("APD");
    patient2.setDestinationID(2L);
  }

  private static Stream<Arguments> providerGetPatientName() {
    return Stream.of(Arguments.of(patient1, "^Kenny"), Arguments.of(patient2, "Super^Homer"));
  }

  private static Stream<Arguments> providerGetPatientFirstName() {
    return Stream.of(Arguments.of(patient1, "Kenny"), Arguments.of(patient2, "Homer"));
  }

  private static Stream<Arguments> providerGetPatientLastName() {
    return Stream.of(Arguments.of(patient1, ""), Arguments.of(patient2, "Super"));
  }

  @ParameterizedTest
  @MethodSource("providerGetPatientName")
  void getPatientName(ExternalIDProviderPatient patient, String output) {
    assertEquals(patient.getPatientName(), output);
  }

  @ParameterizedTest
  @MethodSource("providerGetPatientFirstName")
  void getPatientFirstName(ExternalIDProviderPatient patient, String output) {
    assertEquals(patient.getPatientFirstName(), output);
  }

  @ParameterizedTest
  @MethodSource("providerGetPatientLastName")
  void getPatientLastName(ExternalIDProviderPatient patient, String output) {
    assertEquals(patient.getPatientLastName(), output);
  }
}
