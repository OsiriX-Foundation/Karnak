/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.dicominnolitics;

public class jsonCIODtoModule {

  private String ciodId;

  private String moduleId;

  private String usage;

  private String conditionalStatement;

  private String informationEntity;

  public String getCiodId() {
    return ciodId;
  }

  public String getModuleId() {
    return moduleId;
  }

  public String getUsage() {
    return usage;
  }

  public String getConditionalStatement() {
    return conditionalStatement;
  }

  public String getInformationEntity() {
    return informationEntity;
  }
}
