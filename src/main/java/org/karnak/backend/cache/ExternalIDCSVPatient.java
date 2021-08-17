/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.cache;

public class ExternalIDCSVPatient extends Patient {

  private Long projectID;

  public ExternalIDCSVPatient(
      String pseudonym,
      String patientId,
      String patientFirstName,
      String patientLastName,
      String issuerOfPatientId,
      Long projectID) {
    super(pseudonym, patientId, patientFirstName, patientLastName, null, null, issuerOfPatientId);
    this.projectID = projectID;
  }

  public void setPseudonym(String pseudonym) {
    this.pseudonym = pseudonym;
  }

  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  public void setPatientFirstName(String patientFirstName) {
    updatePatientFirstName(patientFirstName);
  }

  public void setPatientLastName(String patientLastName) {
    updatePatientLastName(patientLastName);
  }

  public void setIssuerOfPatientId(String issuerOfPatientId) {
    this.issuerOfPatientId = issuerOfPatientId;
  }

  public Long getProjectID() {
    return projectID;
  }

  public void setProjectID(Long projectID) {
    this.projectID = projectID;
  }

  @Override
  public String toString() {
    return String.format(
        "External pseudonym: %s, Patient ID: %s, Patient first name: %s, Patient last name: %s,"
            + " Issuer of patient ID: %s",
        pseudonym, patientId, patientFirstName, patientLastName, issuerOfPatientId);
  }
}
