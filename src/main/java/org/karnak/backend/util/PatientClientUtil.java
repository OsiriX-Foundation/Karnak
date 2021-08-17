/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.cache.PseudonymPatient;
import org.karnak.backend.model.profilepipe.PatientMetadata;

public class PatientClientUtil {

  private PatientClientUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static String getPseudonym(PatientMetadata patientMetadata, PatientClient cache) {
    if (cache != null) {
      final String key = generateKey(patientMetadata);
      return getCachedKey(key, patientMetadata, cache);
    }
    return null;
  }

  public static String getPseudonym(PatientMetadata patientMetadata, PatientClient cache, Long id) {
    if (cache != null) {
      final String key = generateKey(patientMetadata, id);
      return getCachedKey(key, patientMetadata, cache);
    }
    return null;
  }

  private static String getCachedKey(
      String key, PatientMetadata patientMetadata, PatientClient cache) {
    final PseudonymPatient patient = cache.get(key);
    if (patient != null && patientMetadata.compareCachedPatient(patient)) {
      return patient.getPseudonym();
    }
    return null;
  }

  public static String generateKey(String patientID, String issuerOfPatientID) {
    return patientID.concat(issuerOfPatientID == null ? "" : issuerOfPatientID);
  }

  public static String generateKey(PseudonymPatient patient) {
    String patientID = patient.getPatientId();
    String issuerOfPatientID = patient.getIssuerOfPatientId();
    return generateKey(patientID, issuerOfPatientID);
  }

  public static String generateKey(PatientMetadata patientMetadata) {
    String patientID = patientMetadata.getPatientID();
    String issuerOfPatientID = patientMetadata.getIssuerOfPatientID();
    return generateKey(patientID, issuerOfPatientID);
  }

  public static String generateKey(PatientMetadata patientMetadata, Long id) {
    final String key = generateKey(patientMetadata);
    return key.concat(id == null ? "" : id.toString());
  }

  public static String generateKey(PseudonymPatient pseudonymPatient, Long id) {
    final String key = generateKey(pseudonymPatient);
    return key.concat(id == null ? "" : id.toString());
  }
}
