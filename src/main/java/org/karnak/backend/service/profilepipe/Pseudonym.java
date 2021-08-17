/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.profilepipe;

import java.util.HashMap;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.util.TagUtils;
import org.karnak.ExternalIDProvider;
import org.karnak.backend.api.MainzellisteApi;
import org.karnak.backend.cache.ExternalIDProviderPatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.config.ExternalIDProviderConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.karnak.backend.model.profilepipe.PatientMetadata;
import org.karnak.backend.util.PatientClientUtil;
import org.karnak.backend.util.SpecialCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pseudonym {

  private static final Logger LOGGER = LoggerFactory.getLogger(Pseudonym.class);

  private final PatientClient externalIdCSVCache;
  private final PatientClient externalIdProviderCache;
  private final HashMap<String, ExternalIDProvider> externalIDProviderImplMap;

  public Pseudonym() {
    this.externalIdCSVCache = AppConfig.getInstance().getExternalIDCache();
    this.externalIdProviderCache = AppConfig.getInstance().getExternalIDProviderCache();
    this.externalIDProviderImplMap =
        ExternalIDProviderConfig.getInstance().externalIDProviderImplMap();
  }

  public String generatePseudonym(DestinationEntity destinationEntity, Attributes dcm) {

    final ExternalIDProviderEntity externalIDProviderEntity =
        destinationEntity.getExternalIDProviderEntity();
    final ExternalIDProviderType externalIDProviderType =
        externalIDProviderEntity.getExternalIDProviderType();
    PatientMetadata patientMetadata;
    if (destinationEntity.getIssuerByDefault() == null
        || destinationEntity.getIssuerByDefault().equals("")) {
      patientMetadata = new PatientMetadata(dcm);
    } else {
      patientMetadata = new PatientMetadata(dcm, destinationEntity.getIssuerByDefault());
    }

    if (externalIDProviderType.equals(ExternalIDProviderType.EXTID_IN_CACHE)) {
      return getExternalIDCSVInCache(patientMetadata, destinationEntity.getProjectEntity().getId());
    }

    if (externalIDProviderType.equals(ExternalIDProviderType.EXTID_IN_TAG)) {
      return getExternalIDInDicomTag(dcm, destinationEntity, patientMetadata);
    }

    final String cachedExternalID =
        getExternalIDProviderCache(patientMetadata, destinationEntity.getId());
    if (cachedExternalID != null) {
      return cachedExternalID;
    }

    if (externalIDProviderType.equals(ExternalIDProviderType.ID_GENERATED_BY_MAINZELLISTE)) {
      return getIDGeneratedByMainzelliste(patientMetadata, destinationEntity.getId());
    }

    if (externalIDProviderType.equals(ExternalIDProviderType.EXTID_IN_MAINTELLISTE)) {
      return getExternalIDInMainzelliste(patientMetadata, destinationEntity.getId());
    }

    // browse the implement of the exertnal id provider
    if (externalIDProviderEntity
        .getExternalIDProviderType()
        .equals(ExternalIDProviderType.EXTID_PROVIDER_IMPLEMENTATION)) {
      return getExternalIDProviderImpl(patientMetadata, dcm, destinationEntity);
    }
    return null;
  }

  private String getExternalIDInDicomTag(
      Attributes dcm, DestinationEntity destinationEntity, PatientMetadata patientMetadata) {
    final String cleanTag = destinationEntity.getTag().replaceAll("[(),]", "").toUpperCase();
    final String tagValue = dcm.getString(TagUtils.intFromHexString(cleanTag));
    String pseudonymExtidInTag = null;

    if (tagValue != null
        && destinationEntity.getDelimiter() != null
        && destinationEntity.getPosition() != null
        && !destinationEntity.getDelimiter().equals("")) {
      String delimiterSpec =
          SpecialCharacter.escapeSpecialRegexChars(destinationEntity.getDelimiter());
      try {
        pseudonymExtidInTag = tagValue.split(delimiterSpec)[destinationEntity.getPosition()];
      } catch (ArrayIndexOutOfBoundsException e) {
        LOGGER.error("Can not split the external pseudonym", e);
      }
    } else {
      pseudonymExtidInTag = tagValue;
    }

    if (pseudonymExtidInTag == null) {
      throw new IllegalStateException("Cannot get an external pseudonym of type EXTID_IN_TAG");
    } else {
      if (destinationEntity.getSavePseudonym().booleanValue()) {
        final MainzellisteApi pseudonymApi = new MainzellisteApi();
        pseudonymApi.addExternalID(
            patientMetadata.generateMainzellisteFields(), pseudonymExtidInTag);
      }
    }
    return pseudonymExtidInTag;
  }

  public String getExternalIDCSVInCache(PatientMetadata patientMetadata, Long projectID) {
    final String pseudonymCacheExtID =
        PatientClientUtil.getPseudonym(patientMetadata, externalIdCSVCache, projectID);
    if (pseudonymCacheExtID == null) {
      throw new IllegalStateException("Cannot get an external pseudonym of type EXTID_IN_CACHE");
    }
    return pseudonymCacheExtID;
  }

  public String getIDGeneratedByMainzelliste(PatientMetadata patientMetadata, Long destinationID) {
    final MainzellisteApi mainzellisteApi = new MainzellisteApi();
    final String idGeneratedByMainzelliste =
        mainzellisteApi.generatePID(patientMetadata.generateMainzellisteFields());
    if (idGeneratedByMainzelliste == null) {
      throw new IllegalStateException(
          "Cannot get an external pseudonym of type ID_GENERATED_BY_MAINZELLISTE");
    }
    cachingExternalIDProvider(idGeneratedByMainzelliste, patientMetadata, destinationID);
    return idGeneratedByMainzelliste;
  }

  public String getExternalIDInMainzelliste(PatientMetadata patientMetadata, Long destinationID) {
    final MainzellisteApi mainzellisteApi = new MainzellisteApi();
    final String externalIDInMainzelliste =
        mainzellisteApi.getExistingExternalID(patientMetadata.generateMainzellisteFields());
    if (externalIDInMainzelliste == null) {
      throw new IllegalStateException(
          "Cannot get an external pseudonym of type EXTID_IN_MAINTELLISTE");
    }
    cachingExternalIDProvider(externalIDInMainzelliste, patientMetadata, destinationID);
    return externalIDInMainzelliste;
  }

  public String getExternalIDProviderImpl(
      PatientMetadata patientMetadata, Attributes dcm, DestinationEntity destinationEntity) {
    // browse the implement of the exertnal id provider
    final ExternalIDProviderEntity externalIDProviderEntity =
        destinationEntity.getExternalIDProviderEntity();
    ;
    final ExternalIDProvider externalIDProviderImpl =
        externalIDProviderImplMap.get(externalIDProviderEntity.getJarName());
    if (externalIDProviderImpl != null) {
      final String externalID = externalIDProviderImpl.getExternalID(dcm);
      cachingExternalIDProvider(externalID, patientMetadata, destinationEntity.getId());
      return externalID;
    }
    throw new IllegalStateException(
        "Cannot get an external pseudonym of type EXTID_PROVIDER_IMPLEMENTATION");
  }

  public String getExternalIDProviderCache(PatientMetadata patientMetadata, Long destinationID) {
    final String cachedPseudonym =
        PatientClientUtil.getPseudonym(patientMetadata, externalIdProviderCache, destinationID);
    if (cachedPseudonym != null) {
      cachingExternalIDProvider(cachedPseudonym, patientMetadata, destinationID);
      return cachedPseudonym;
    }
    return null;
  }

  private void cachingExternalIDProvider(
      String pseudonym, PatientMetadata patientMetadata, Long destinationID) {
    final ExternalIDProviderPatient externalIDProviderPatient =
        new ExternalIDProviderPatient(
            pseudonym,
            patientMetadata.getPatientID(),
            patientMetadata.getPatientName(),
            patientMetadata.getIssuerOfPatientID(),
            destinationID);
    String cacheKey = PatientClientUtil.generateKey(patientMetadata, destinationID);
    externalIdProviderCache.put(cacheKey, externalIDProviderPatient);
  }
}
