/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.extid;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.karnak.backend.cache.ExternalIDCSVCache;
import org.karnak.backend.cache.ExternalIDCSVPatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.data.entity.ProjectEntity;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExternalIDGridTest {

  @Test
  void should_create_external_id_grid() {

    // Call constructor
    ExternalIDGrid externalIDGrid = new ExternalIDGrid();

    // Test results
    Assert.assertNotNull(externalIDGrid);
  }

  @Test
  void should_read_cache() {

    // Call constructor
    ExternalIDGrid externalIDGrid = new ExternalIDGrid();

    // Set cache
    PatientClient externalIDCache = new ExternalIDCSVCache();
    ExternalIDCSVPatient patient =
        new ExternalIDCSVPatient(
            "pseudonym",
            "patientId",
            "patientFirstName",
            "patientLastName",
            "issuerOfPatientId",
            1L);
    patient.setProjectID(1L);
    externalIDCache.put("key", patient);
    externalIDGrid.setExternalIDCache(externalIDCache);

    // set project
    ProjectEntity projectEntity = new ProjectEntity();
    projectEntity.setId(1L);
    externalIDGrid.setProjectEntity(projectEntity);

    // Call method
    externalIDGrid.readAllCacheValue();

    // Test results
    Assert.assertNotNull(externalIDGrid);
    Assert.assertNotNull(externalIDGrid.getExternalIDCache());
    Assert.assertNotNull(externalIDGrid.getProjectEntity());
    Assert.assertEquals("pseudonym", externalIDGrid.getPatientsListInCache().get(0).getPseudonym());
  }

  @Test
  void should_add_patient_and_check_existence() {

    // Call constructor
    ExternalIDGrid externalIDGrid = new ExternalIDGrid();

    // Add patient
    ExternalIDCSVPatient patient =
        new ExternalIDCSVPatient(
            "pseudonym",
            "patientId",
            "patientFirstName",
            "patientLastName",
            "issuerOfPatientId",
            1L);
    patient.setProjectID(1L);

    // set project
    ProjectEntity projectEntity = new ProjectEntity();
    projectEntity.setId(1L);
    externalIDGrid.setProjectEntity(projectEntity);

    // Test no patient in cache
    Assert.assertFalse(externalIDGrid.patientExist(patient));

    // Add patient
    externalIDGrid.addPatient(patient);

    // Test patient in cache
    Assert.assertTrue(externalIDGrid.patientExist(patient));
  }
}
