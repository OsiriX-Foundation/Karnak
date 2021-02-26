/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.karnak.backend.model.profilepipe.PatientMetadata;
import org.karnak.backend.util.PatientClientUtil;

class PatientClientUtilTest {

  static DicomObject dataset;
  static DicomObject datasetWithIssuer;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    dataset = DicomObject.newDicomObject();
    dataset.setString(Tag.PatientID, VR.LO, "EREN");
    dataset.setString(Tag.PatientName, VR.PN, "Patient^Name");
    dataset.setString(Tag.PatientBirthDate, VR.DA, "19930216");
    dataset.setString(Tag.PatientSex, VR.CS, "M");

    datasetWithIssuer = DicomObject.newDicomObject();
    datasetWithIssuer.setString(Tag.PatientID, VR.LO, "EREN");
    datasetWithIssuer.setString(Tag.PatientName, VR.PN, "Patient^Name");
    datasetWithIssuer.setString(Tag.PatientBirthDate, VR.DA, "19930216");
    datasetWithIssuer.setString(Tag.PatientSex, VR.CS, "M");
    datasetWithIssuer.setString(Tag.IssuerOfPatientID, VR.LO, "PDA");
  }

  private static Stream<Arguments> providerGenerateKey() {
    return Stream.of(
        Arguments.of("123", "456", "789", "123789"),
        Arguments.of("123", "456", "", "123"),
        Arguments.of("EREN", "Patient^Name", "PDA", "ERENPDA"),
        Arguments.of("EREN", "Patient^Name", "", "EREN"));
  }

  private static Stream<Arguments> providerGenerateKeyPseudonymPatient() {
    return Stream.of(
        Arguments.of(new CachedPatient("pseudo", "123", "456", "789", "101112"), "123101112"),
        Arguments.of(new CachedPatient("pseudo", "123", "456", "789", ""), "123"),
        Arguments.of(new CachedPatient("pseudo", "EREN", "Patient", "Name", "PDA"), "ERENPDA"),
        Arguments.of(new CachedPatient("pseudo", "EREN", "Patient", "Name", ""), "EREN"),
        Arguments.of(
            new MainzellistePatient(
                "pseudo", "123", "", "456", LocalDate.of(1993, 02, 16), "M", ""),
            "123"),
        Arguments.of(
            new MainzellistePatient(
                "pseudo", "123", "", "456", LocalDate.of(1993, 02, 16), "M", "789"),
            "123789"),
        Arguments.of(
            new MainzellistePatient(
                "pseudo", "EREN", "Name", "Patient", LocalDate.of(1993, 02, 16), "M", "PDA"),
            "ERENPDA"),
        Arguments.of(
            new MainzellistePatient(
                "pseudo", "EREN", "Name", "Patient", LocalDate.of(1993, 02, 16), "M", ""),
            "EREN"));
  }

  private static Stream<Arguments> providerGenerateKeyPatientMetadata() {
    return Stream.of(
        Arguments.of(new PatientMetadata(dataset, "PDA"), "ERENPDA"),
        Arguments.of(new PatientMetadata(dataset, ""), "EREN"),
        Arguments.of(new PatientMetadata(datasetWithIssuer, "TEST"), "ERENPDA"),
        Arguments.of(new PatientMetadata(datasetWithIssuer, ""), "ERENPDA"));
  }

  @ParameterizedTest
  @MethodSource("providerGenerateKey")
  void generateKey(String PatientID, String IssuerOfPatientID, String output) {
    assertEquals(PatientClientUtil.generateKey(PatientID, IssuerOfPatientID), output);
  }

  @ParameterizedTest
  @MethodSource("providerGenerateKeyPseudonymPatient")
  void generateKeyPseudonymPatient(PseudonymPatient patient, String output) {
    assertEquals(PatientClientUtil.generateKey(patient), output);
  }

  @ParameterizedTest
  @MethodSource("providerGenerateKeyPatientMetadata")
  void generateKeyPatientMetadata(PatientMetadata patientMetadata, String output) {
    assertEquals(PatientClientUtil.generateKey(patientMetadata), output);
  }
}
