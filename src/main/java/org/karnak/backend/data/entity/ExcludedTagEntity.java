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

import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "ExcludedTag")
@DiscriminatorValue("ExcludedTag")
public class ExcludedTagEntity extends TagEntity implements Serializable {

  private static final long serialVersionUID = -5068272710332856139L;

  public ExcludedTagEntity() {
  }

  public ExcludedTagEntity(String tagValue, ProfileElementEntity profileElementEntity) {
    super(tagValue, profileElementEntity);
  }
}
