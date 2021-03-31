/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.repo.ForwardNodeRepo;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ForwardNodeServiceTest {

  // Repositories
  private final ForwardNodeRepo forwardNodeRepoMock = Mockito.mock(ForwardNodeRepo.class);

  // Service
  private ForwardNodeService forwardNodeService;

  @BeforeEach
  public void setUp() {

    // Build mocked service
    forwardNodeService = new ForwardNodeService(forwardNodeRepoMock);
  }

  @Test
  void should_retrieve_forward_node_by_id() {
    // Call service
    forwardNodeService.get(1L);

    // Test results
    Mockito.verify(forwardNodeRepoMock, Mockito.times(1)).findById(Mockito.anyLong());
  }

  @Test
  void should_save_forward_node() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();

    // Call service
    forwardNodeService.save(forwardNodeEntity);

    // Test results
    Mockito.verify(forwardNodeRepoMock, Mockito.times(1))
        .saveAndFlush(Mockito.any(ForwardNodeEntity.class));
  }

  @Test
  void should_delete_forward_node() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    forwardNodeEntity.setId(1L);

    // Call service
    forwardNodeService.delete(forwardNodeEntity);

    // Test results
    Mockito.verify(forwardNodeRepoMock, Mockito.times(1)).deleteById(Mockito.anyLong());
  }

  @Test
  void should_retrieve_all_forward_node() {
    // Call service
    forwardNodeService.getAllForwardNodes();

    // Test results
    Mockito.verify(forwardNodeRepoMock, Mockito.times(1)).findAll();
  }

  @Test
  void should_retrieve_destination_by_id() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    DestinationEntity destinationEntity = new DestinationEntity();
    destinationEntity.setId(1L);
    forwardNodeEntity.addDestination(destinationEntity);

    // Call service
    DestinationEntity destinationFound =
        forwardNodeService.getDestinationById(forwardNodeEntity, 1L);

    // Test result
    Assert.assertNotNull(destinationFound);
    Assert.assertEquals(1L, destinationFound.getId().longValue());
  }

  @Test
  void should_update_destination() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    DestinationEntity destinationEntity = new DestinationEntity();
    destinationEntity.setId(1L);
    forwardNodeEntity.addDestination(destinationEntity);
    // Destination to add
    DestinationEntity destinationEntityToAdd = new DestinationEntity();
    destinationEntityToAdd.setId(2L);

    // Call service
    DestinationEntity destinationAdded =
        forwardNodeService.updateDestination(forwardNodeEntity, destinationEntityToAdd);

    // Test result
    Assert.assertNotNull(destinationAdded);
    Assert.assertEquals(2L, destinationAdded.getId().longValue());
    Assert.assertEquals(2, forwardNodeEntity.getDestinationEntities().size());
    Assert.assertEquals(2, forwardNodeEntity.getDestinationEntities().size());
    Assert.assertTrue(forwardNodeEntity.getDestinationEntities().contains(destinationAdded));
  }

  @Test
  void should_delete_destination() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    DestinationEntity destinationEntity = new DestinationEntity();
    destinationEntity.setId(1L);
    forwardNodeEntity.addDestination(destinationEntity);
    // Destination to add
    DestinationEntity destinationEntityToDelete = new DestinationEntity();
    destinationEntityToDelete.setId(2L);
    forwardNodeEntity.addDestination(destinationEntityToDelete);

    // Call service
    forwardNodeService.deleteDestination(forwardNodeEntity, destinationEntityToDelete);

    // Test result
    Assert.assertEquals(1, forwardNodeEntity.getDestinationEntities().size());
    Assert.assertFalse(
        forwardNodeEntity.getDestinationEntities().contains(destinationEntityToDelete));
  }

  @Test
  void should_retrieve_source_by_id() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    DicomSourceNodeEntity dicomSourceNodeEntity = new DicomSourceNodeEntity();
    dicomSourceNodeEntity.setId(1L);
    forwardNodeEntity.addSourceNode(dicomSourceNodeEntity);

    // Call service
    DicomSourceNodeEntity sourceFound = forwardNodeService.getSourceNodeById(forwardNodeEntity, 1L);

    // Test result
    Assert.assertNotNull(sourceFound);
    Assert.assertEquals(1L, sourceFound.getId().longValue());
  }

  @Test
  void should_update_source() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    DicomSourceNodeEntity dicomSourceNodeEntity = new DicomSourceNodeEntity();
    dicomSourceNodeEntity.setId(1L);
    forwardNodeEntity.addSourceNode(dicomSourceNodeEntity);
    // Source to add
    DicomSourceNodeEntity dicomSourceNodeEntityToAdd = new DicomSourceNodeEntity();
    dicomSourceNodeEntityToAdd.setId(2L);

    // Call service
    DicomSourceNodeEntity sourceAdded =
        forwardNodeService.updateSourceNode(forwardNodeEntity, dicomSourceNodeEntityToAdd);

    // Test result
    Assert.assertNotNull(sourceAdded);
    Assert.assertEquals(2L, sourceAdded.getId().longValue());
    Assert.assertEquals(2, forwardNodeEntity.getSourceNodes().size());
    Assert.assertEquals(2, forwardNodeEntity.getSourceNodes().size());
    Assert.assertTrue(forwardNodeEntity.getSourceNodes().contains(sourceAdded));
  }

  @Test
  void should_delete_source() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    DicomSourceNodeEntity dicomSourceNodeEntity = new DicomSourceNodeEntity();
    dicomSourceNodeEntity.setId(1L);
    forwardNodeEntity.addSourceNode(dicomSourceNodeEntity);
    // Source to add
    DicomSourceNodeEntity sourceEntityToDelete = new DicomSourceNodeEntity();
    sourceEntityToDelete.setId(2L);
    forwardNodeEntity.addSourceNode(sourceEntityToDelete);

    // Call service
    forwardNodeService.deleteSourceNode(forwardNodeEntity, sourceEntityToDelete);

    // Test result
    Assert.assertEquals(1, forwardNodeEntity.getSourceNodes().size());
    Assert.assertFalse(forwardNodeEntity.getSourceNodes().contains(sourceEntityToDelete));
  }
}
