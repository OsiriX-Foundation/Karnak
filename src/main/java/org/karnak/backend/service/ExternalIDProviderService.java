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

import java.util.HashSet;
import java.util.Set;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.data.repo.ExternalIDProviderRepo;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ExternalIDProviderService {
  private final ExternalIDProviderRepo externalIDProviderRepo;

  // Event publisher
  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public ExternalIDProviderService(
      final ExternalIDProviderRepo externalIDProviderRepo,
      final ApplicationEventPublisher applicationEventPublisher) {
    this.externalIDProviderRepo = externalIDProviderRepo;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public Set<ExternalIDProviderEntity> getAllExternalIDProvider() {
    Set<ExternalIDProviderEntity> list = new HashSet<>();
    externalIDProviderRepo
        .findAll() //
        .forEach(list::add);
    return list;
  }

  public boolean externalIDProviderTypeExist(ExternalIDProviderType externalIDProviderType) {
    return externalIDProviderRepo.existsByExternalIDProviderType(externalIDProviderType);
  }

  public boolean jarNameExist(String jarName) {
    return externalIDProviderRepo.existsByJarName(jarName);
  }

  public ExternalIDProviderEntity saveExternalIDProvider(
      ExternalIDProviderEntity externalIDProviderEntity) {
    return externalIDProviderRepo.saveAndFlush(externalIDProviderEntity);
  }

  public ExternalIDProviderEntity getExternalIDProvider(
      ExternalIDProviderType externalIDProviderType, String jarName) {
    return externalIDProviderRepo.getByExternalIDProviderTypeAndJarName(
        externalIDProviderType, jarName);
  }
}
