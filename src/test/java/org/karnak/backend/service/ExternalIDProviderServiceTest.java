/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.data.repo.ExternalIDProviderRepo;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest
class ExternalIDProviderServiceTest {

  // Application Event Publisher
  private final ApplicationEventPublisher applicationEventPublisherMock =
      Mockito.mock(ApplicationEventPublisher.class);

  // Repositories
  private final ExternalIDProviderRepo externalIDProviderRepoMock =
      Mockito.mock(ExternalIDProviderRepo.class);

  // Service
  private ExternalIDProviderService externalIDProviderService;

  @BeforeEach
  public void setUp() {
    // Init data
    ExternalIDProviderEntity externalIDProviderEntity = new ExternalIDProviderEntity();
    externalIDProviderEntity.setId(1L);
    externalIDProviderEntity.setExternalIDProviderType(ExternalIDProviderType.EXTID_IN_CACHE);

    // Mock repositories
    Mockito.when(externalIDProviderRepoMock.findAll())
        .thenReturn(Collections.singletonList(externalIDProviderEntity));

    // Build mocked service
    externalIDProviderService =
        new ExternalIDProviderService(externalIDProviderRepoMock, applicationEventPublisherMock);
  }

  @Test
  void shouldCallSaveFromRepository() {
    // Init data
    ExternalIDProviderEntity externalIDProviderEntity = new ExternalIDProviderEntity();

    // Call service
    externalIDProviderService.saveExternalIDProvider(externalIDProviderEntity);

    // Test results
    Mockito.verify(externalIDProviderRepoMock, Mockito.times(1))
        .saveAndFlush(Mockito.any(ExternalIDProviderEntity.class));
  }

  @Test
  void shouldRetrieveAllExternalIDProvider() {
    // Init data
    ExternalIDProviderEntity externalIDProviderEntity = new ExternalIDProviderEntity();
    externalIDProviderEntity.setId(1L);

    // Call service
    Set<ExternalIDProviderEntity> externalIDProviderEntities =
        externalIDProviderService.getAllExternalIDProvider();

    // Test results
    Mockito.verify(externalIDProviderRepoMock, Mockito.times(1)).findAll();
    Assert.assertNotNull(externalIDProviderEntities);
    Assert.assertEquals(1, externalIDProviderEntities.size());
  }

  @Test
  void shouldRetrieveExternalIDProvider() {
    // Init data
    ExternalIDProviderEntity externalIDProviderEntity = new ExternalIDProviderEntity();

    Mockito.when(
            externalIDProviderService.getExternalIDProvider(
                Mockito.any(ExternalIDProviderType.class), Mockito.anyString()))
        .thenReturn(externalIDProviderEntity);
  }

  @Test
  void shouldRetrieveIDProviderTypeExist() {

    Mockito.when(
            externalIDProviderService.externalIDProviderTypeExist(
                Mockito.any(ExternalIDProviderType.class)))
        .thenReturn(true);

    Mockito.when(
            externalIDProviderService.externalIDProviderTypeExist(
                Mockito.any(ExternalIDProviderType.class)))
        .thenReturn(false);
  }
}
