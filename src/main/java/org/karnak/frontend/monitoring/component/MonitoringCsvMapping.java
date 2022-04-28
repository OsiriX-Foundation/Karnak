/*
 * Copyright (c) 2022 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.monitoring.component;

/**
 * Mapping for the monitoring CSV export Handle order of the columns and mapping to fields of the
 * entities. Order of the enum will determine the order of the columns in the CSV file
 */
public enum MonitoringCsvMapping {
  FORWARD_AETITLE("fwdAeTitle", "Forward aeTitle"),
  FORWARD_DESCRIPTION("fwdDescription", "Forward description"),
  DESTINATION_HOSTNAME("hostname", "Destination hostname"),
  DESTINATION_AETITLE("aeTitle", "Destination aeTitle"),
  DESTINATION_PORT("port", "Destination port"),
  DESTINATION_URL("url", "Destination url"),
  DESTINATION_DESCRIPTION("description", "Destination description"),
  PATIENT_ID_ORIGINAL("patientIdOriginal", "Patient Id Original"),
  PATIENT_ID_TO_SEND("patientIdToSend", "Patient Id To Send"),
  ACCESSION_NUMBER_ORIGINAL("accessionNumberOriginal", "Accession Number Original"),
  ACCESSION_NUMBER_TO_SEND("accessionNumberToSend", "Accession Number To Send"),
  STUDY_UID_ORIGINAL("studyUidOriginal", "Study Uid Original"),
  STUDY_UID_TO_SEND("studyUidToSend", "Study Uid To Send"),
  STUDY_DESCRIPTION_ORIGINAL("studyDescriptionOriginal", "Study Description Original"),
  STUDY_DESCRIPTION_TO_SEND("studyDescriptionToSend", "Study Description To Send"),
  STUDY_DATE_ORIGINAL("studyDateOriginal", "Study Date Original"),
  STUDY_DATE_TO_SEND("studyDateToSend", "Study Date To Send"),
  SERIE_UID_ORIGINAL("serieUidOriginal", "Serie Uid Original"),
  SERIE_UID_TO_SEND("studyUidToSend", "Study Uid To Send"),
  SERIE_DESCRIPTION_ORIGINAL("serieDescriptionOriginal", "Serie Description Original"),
  SERIE_DESCRIPTION_TO_SEND("serieDescriptionToSend", "Serie Description To Send"),
  SERIE_DATE_ORIGINAL("serieDateOriginal", "Serie Date Original"),
  SERIE_DATE_TO_SEND("serieDateToSend", "Serie Date To Send"),
  SOP_INSTANCE_UID_ORIGINAL("sopInstanceUidOriginal", "SopInstance Uid Original"),
  SOP_INSTANCE_UID_TO_SEND("sopInstanceUidToSend", "SopInstance Uid To Send"),
  TRANSFER_DATE("transferDate", "Transfer Date"),
  SENT("sent", "Sent"),
  REASON("reason", "Reason");

  // Name of the field of the entity
  private final String nameFieldEntity;

  // Name of the column header of the Csv file
  private final String labelCsv;

  /**
   * Constructor
   *
   * @param nameFieldEntity Name of the field of the entity to retrieve
   * @param labelCsv Name of the column header of the Csv file
   */
  MonitoringCsvMapping(String nameFieldEntity, String labelCsv) {
    this.nameFieldEntity = nameFieldEntity;
    this.labelCsv = labelCsv;
  }

  public String getNameFieldEntity() {
    return nameFieldEntity;
  }

  public String getLabelCsv() {
    return labelCsv;
  }
}
