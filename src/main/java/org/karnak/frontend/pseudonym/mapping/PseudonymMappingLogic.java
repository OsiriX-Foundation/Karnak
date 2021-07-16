/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.pseudonym.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.karnak.backend.cache.CachedPatient;
import org.karnak.backend.cache.ExternalIDCache;
import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.backend.cache.Patient;
import org.karnak.backend.cache.PseudonymPatient;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.ProjectService;
import org.karnak.backend.service.PseudonymMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Mapping logic service use to make calls to backend and implement logic linked to the mapping view
 */
@Service
public class PseudonymMappingLogic {

  private static final Logger LOGGER = LoggerFactory.getLogger(PseudonymMappingLogic.class);

  // View
  private PseudonymMappingView pseudonymMappingView;

  // Services
  private final PseudonymMappingService pseudonymMappingService;
  private final ProjectService projectService;

  // Cache
  private final ExternalIDCache externalIDCache;

  /**
   * Autowired constructor
   *
   * @param pseudonymMappingService Pseudonym mapping backend service
   * @param externalIDCache External ID Cache
   * @param projectService Project service
   */
  @Autowired
  public PseudonymMappingLogic(
      final PseudonymMappingService pseudonymMappingService,
      final ExternalIDCache externalIDCache,
      final ProjectService projectService) {
    this.pseudonymMappingService = pseudonymMappingService;
    this.externalIDCache = externalIDCache;
    this.projectService = projectService;
  }

  /**
   * Retrieve a patient stored in mainzelliste by its pseudonym
   *
   * @param pseudonym Pseudonym
   * @return Patient found
   */
  public MainzellistePatient retrieveMainzellistePatient(String pseudonym) {
    return pseudonymMappingService.retrieveMainzellistePatient(pseudonym);
  }

  /**
   * Retrieve a map of patients by project stored in external id cache which have the pseudonym in
   * parameter
   *
   * @param pseudonym Pseudonym
   * @return Map of patients by project
   */
  public Map<String, Patient> retrieveExternalIDCachePatients(String pseudonym) {
    Map<String, Patient> externalIDCacheMapping = new HashMap<>();

    // Look for PseudonymPatient in externalID cache corresponding to the input of the user
    List<PseudonymPatient> pseudonymPatientsFound =
        externalIDCache.getAll().stream()
            .filter(extId -> Objects.equals(extId.getPseudonym(), pseudonym))
            .collect(Collectors.toList());

    // Add mapping found
    pseudonymPatientsFound.forEach(
        p -> {
          Long projectID = ((CachedPatient) p).getProjectID();
          ProjectEntity projectEntity = projectService.retrieveProject(projectID);
          externalIDCacheMapping.put(projectEntity.getName(), (CachedPatient) p);
        });

    return externalIDCacheMapping;
  }

  public PseudonymMappingView getMappingView() {
    return pseudonymMappingView;
  }

  public void setMappingView(PseudonymMappingView pseudonymMappingView) {
    this.pseudonymMappingView = pseudonymMappingView;
  }
}
