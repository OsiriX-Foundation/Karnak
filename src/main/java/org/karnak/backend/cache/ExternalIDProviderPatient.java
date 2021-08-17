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

public class ExternalIDProviderPatient extends Patient {
  private Long destinationID;

  public ExternalIDProviderPatient(
      String pseudonym,
      String patientId,
      String patientName,
      String issuerOfPatientId,
      Long destinationID) {
    super(pseudonym, patientId, patientName, null, null, issuerOfPatientId);
    this.destinationID = destinationID;
  }

  public void setPseudonym(String pseudonym) {
    this.pseudonym = pseudonym;
  }

  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }

  public void setIssuerOfPatientId(String issuerOfPatientId) {
    this.issuerOfPatientId = issuerOfPatientId;
  }

  public Long getDestinationID() {
    return destinationID;
  }

  public void setDestinationID(Long destinationID) {
    this.destinationID = destinationID;
  }
}
