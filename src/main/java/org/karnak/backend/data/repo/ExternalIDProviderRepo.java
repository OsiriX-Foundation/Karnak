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

import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalIDProviderRepo extends JpaRepository<ExternalIDProviderEntity, Long> {
  Boolean existsByJarName(String jarNAme);

  Boolean existsByExternalIDProviderType(ExternalIDProviderType externalIDProviderType);

  ExternalIDProviderEntity getByExternalIDProviderTypeAndJarName(
      ExternalIDProviderType externalIDProviderType, String jarName);
}
