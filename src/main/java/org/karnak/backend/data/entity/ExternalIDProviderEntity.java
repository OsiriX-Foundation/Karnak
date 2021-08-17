/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.karnak.backend.enums.ExternalIDProviderType;

@Entity(name = "ExternalIDProvider")
@Table(name = "externalid_provider")
public class ExternalIDProviderEntity {

  private static final long serialVersionUID = 158546565156165167L;

  private Long id;
  private boolean bydefault;
  private ExternalIDProviderType externalIDProviderType;
  private String jarName;

  public ExternalIDProviderEntity() {}

  public ExternalIDProviderEntity(
      boolean bydefault, ExternalIDProviderType externalIDProviderType, String jarName) {
    this.bydefault = bydefault;
    this.externalIDProviderType = externalIDProviderType;
    this.jarName = jarName;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isBydefault() {
    return bydefault;
  }

  public void setBydefault(boolean bydefault) {
    this.bydefault = bydefault;
  }

  @Column(name = "externalid_provider_type")
  @Enumerated(EnumType.STRING)
  public ExternalIDProviderType getExternalIDProviderType() {
    return externalIDProviderType;
  }

  public void setExternalIDProviderType(ExternalIDProviderType externalIDProviderType) {
    this.externalIDProviderType = externalIDProviderType;
  }

  public String getJarName() {
    return jarName;
  }

  public void setJarName(String filePath) {
    this.jarName = filePath;
  }
}
