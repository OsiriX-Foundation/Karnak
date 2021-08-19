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

import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.KheopsAlbumsEntity;
import org.karnak.backend.enums.DestinationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class KheopsAlbumsRepoTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(KheopsAlbumsRepoTest.class);

  @Autowired private KheopsAlbumsRepo repository;
  @Autowired private DestinationRepo destinationRepo;

  /** Test save and find record. */
  @Test
  void shouldSaveAndFindARecord() {
    // Create an entity to save
    KheopsAlbumsEntity entity = new KheopsAlbumsEntity();
    entity.setCondition("Condition");

    // Save the entity
    LOGGER.info("Saving entity with Condition [{}]", entity.getCondition());
    entity = repository.save(entity);

    // Test Save
    Assert.assertEquals("Condition", entity.getCondition());
    Assert.assertNotNull(entity.getId());
    LOGGER.info(
        "Entity with Condition [{}] and id [{}] saved", entity.getCondition(), entity.getId());

    // Find By Id
    Optional<KheopsAlbumsEntity> foundByIdOpt = repository.findById(entity.getId());

    // Test Find by Id
    Assert.assertTrue(foundByIdOpt.isPresent());
    LOGGER.info(
        "Entity found with Condition [{}] and id [{}]",
        foundByIdOpt.get().getCondition(),
        foundByIdOpt.get().getId());
    Assert.assertEquals(entity.getId(), foundByIdOpt.get().getId());
  }

  /** Test find all. */
  @Test
  void shouldFindAllRecords() {
    // Create an entity to save
    // Destination
    DestinationEntity destinationEntity = new DestinationEntity();
    destinationEntity.setAeTitle("AeTitle");
    destinationEntity.setDestinationType(DestinationType.dicom);
    destinationEntity.setHostname("hostName");
    destinationEntity.setPort(1);
    destinationEntity = destinationRepo.saveAndFlush(destinationEntity);
    // Kheops Albums
    KheopsAlbumsEntity entity = new KheopsAlbumsEntity();
    entity.setCondition("Condition");
    entity.setDestinationEntity(destinationEntity);

    // Save the entity
    LOGGER.info("Saving entity with Condition [{}]", entity.getCondition());
    repository.saveAndFlush(entity);

    // Find all
    List<KheopsAlbumsEntity> all = repository.findAll();

    // Test find all
    Assert.assertNotNull(all);
    Assert.assertTrue(all.size() > 0);
    Assert.assertEquals(1, all.size());
    LOGGER.info("Number of entities found [{}]", all.size());
  }

  /** Test modification of a record. */
  @Test
  void shouldModifyRecord() {

    String initialText = "InitialText";
    String modifiedText = "ModifiedText";

    // Create an entity to save
    KheopsAlbumsEntity entity = new KheopsAlbumsEntity();
    entity.setCondition(initialText);

    // Save the entity
    LOGGER.info("Saving entity with Condition [{}]", entity.getCondition());
    entity = repository.save(entity);
    LOGGER.info("Id of the entity with Condition [{}]", entity.getId());

    // Test Save
    Assert.assertNotNull(entity);
    Assert.assertEquals(initialText, entity.getCondition());

    // Modify the record
    entity.setCondition(modifiedText);
    LOGGER.info("Modify entity Condition [{}] to [{}]", initialText, modifiedText);
    KheopsAlbumsEntity entityModified = repository.save(entity);

    // Test Modify
    Assert.assertNotNull(entityModified);
    Assert.assertEquals(entity.getId(), entityModified.getId());
    Assert.assertEquals(modifiedText, entityModified.getCondition());
    LOGGER.info(
        "Condition of the entity with id [{}]: [{}]",
        entityModified.getId(),
        entityModified.getCondition());
  }

  /** Test delete record. */
  @Test
  void shouldDeleteRecord() {
    // Create an entity to save
    KheopsAlbumsEntity entity = new KheopsAlbumsEntity();
    String condition = "Condition";
    entity.setCondition(condition);

    // Save the entity
    LOGGER.info("Saving entity with Condition [{}]", entity.getCondition());
    entity = repository.save(entity);

    // Retrieve the entity
    Optional<KheopsAlbumsEntity> foundByIdOpt = repository.findById(entity.getId());

    // Test Find by Id
    Assert.assertTrue(foundByIdOpt.isPresent());

    // Delete the entity
    entity = foundByIdOpt.get();
    Long id = entity.getId();
    LOGGER.info("Deleting entity with id [{}]", id);
    repository.delete(entity);

    // Test Delete
    foundByIdOpt = repository.findById(id);
    LOGGER.info("Is deleted entity with id [{}] present: [{}]", id, foundByIdOpt.isPresent());
    Assert.assertFalse(foundByIdOpt.isPresent());
  }

  /** Test findAllByDestinationEntity method. */
  @Test
  void shouldFindAllByDestinationEntity() {
    // Create and save destination
    DestinationEntity destinationEntity = new DestinationEntity();
    destinationEntity.setAeTitle("AeTitle");
    destinationEntity.setDestinationType(DestinationType.dicom);
    destinationEntity.setHostname("hostName");
    destinationEntity.setPort(1);

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", destinationEntity.getAeTitle());
    destinationEntity = destinationRepo.saveAndFlush(destinationEntity);

    // Create and save 2 Kheops Albums with same destination
    KheopsAlbumsEntity entityFirst = new KheopsAlbumsEntity();
    entityFirst.setDestinationEntity(destinationEntity);
    KheopsAlbumsEntity entitySecond = new KheopsAlbumsEntity();
    entitySecond.setDestinationEntity(destinationEntity);

    // Save the entity
    LOGGER.info("Saving entities");
    entityFirst = repository.save(entityFirst);
    entitySecond = repository.save(entitySecond);

    // Retrieve the entities
    List<KheopsAlbumsEntity> all = repository.findAllByDestinationEntity(destinationEntity);

    // Test results
    Assert.assertNotNull(all);
    Assert.assertTrue(all.size() > 0);
    Assert.assertEquals(2, all.size());
    Assert.assertEquals("AeTitle", all.get(0).getDestinationEntity().getAeTitle());
    Assert.assertEquals("AeTitle", all.get(1).getDestinationEntity().getAeTitle());
    LOGGER.info("Number of entities found [{}]", all.size());
  }
}
