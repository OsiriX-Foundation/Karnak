/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.data.repo;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ExternalIDProviderRepoTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileRepoTest.class);

  @Autowired private ExternalIDProviderRepo repository;

  /** Test instanciateEntity. */
  @Test
  void shouldInstanciate() {

    // Create an entity to save
    ExternalIDProviderEntity entity =
        new ExternalIDProviderEntity(true, ExternalIDProviderType.EXTID_IN_TAG, null);

    // Save the entity
    entity = repository.saveAndFlush(entity);

    // Check if

    Assert.assertTrue(repository.existsById(entity.getId()));
    ExternalIDProviderEntity entityFind = repository.findById(entity.getId()).get();
    Assert.assertTrue(entityFind.isBydefault());

    // Delete the profile
    entity.setBydefault(false);
    entity = repository.saveAndFlush(entity);

    Assert.assertFalse(repository.findById(entity.getId()).get().isBydefault());
  }

  /** Test existsByJarName method. */
  @Test
  void shouldExistsByJarName() {

    // Create an entity to save
    ExternalIDProviderEntity entity = new ExternalIDProviderEntity();
    entity.setJarName("JarName");

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getJarName());
    entity = repository.saveAndFlush(entity);

    // Check if exists
    Assert.assertTrue(repository.existsByJarName("JarName"));

    // Delete the profile
    repository.delete(entity);
    repository.flush();

    // Check if profile exists
    Assert.assertFalse(repository.existsByJarName("JarName"));
  }

  /** Test existsByExternalIDProviderType method. */
  @Test
  void shouldExistsByExternalIDProviderType() {

    // Create an entity to save
    ExternalIDProviderEntity entity = new ExternalIDProviderEntity();
    entity.setExternalIDProviderType(ExternalIDProviderType.EXTID_PROVIDER_IMPLEMENTATION);

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getExternalIDProviderType());
    entity = repository.saveAndFlush(entity);

    // Check if exists
    Assert.assertTrue(
        repository.existsByExternalIDProviderType(
            ExternalIDProviderType.EXTID_PROVIDER_IMPLEMENTATION));

    // Delete the entity
    repository.delete(entity);
    repository.flush();

    // Check if entity exists
    Assert.assertFalse(
        repository.existsByExternalIDProviderType(
            ExternalIDProviderType.EXTID_PROVIDER_IMPLEMENTATION));
  }

  /** Test getByExternalIDProviderTypeAndJarName */
  @Test
  void shouldGetByExternalIDProviderTypeAndJarName() {
    // Create an entity to save
    ExternalIDProviderEntity entity = new ExternalIDProviderEntity();
    entity.setJarName("JarName");
    entity.setExternalIDProviderType(ExternalIDProviderType.EXTID_IN_CACHE);

    // Save the entity
    entity = repository.save(entity);

    // Test Save
    Assert.assertEquals("JarName", entity.getJarName());
    Assert.assertNotNull(entity.getId());

    // Get entity
    ExternalIDProviderEntity foundEntity =
        repository.getByExternalIDProviderTypeAndJarName(
            ExternalIDProviderType.EXTID_IN_CACHE, "JarName");

    // Test entity not nul and found
    Assert.assertNotNull(foundEntity);
    Assert.assertEquals(entity.getId(), foundEntity.getId());
  }
}
