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

public class jsonModuleToAttribute {

  private String moduleId;

  private String path;

  private String tag;

  private String type;

  private String linkToStandard;

  private String description;

  public String getModuleId() {
    return moduleId;
  }

  public String getPath() {
    return path;
  }

  public String getTag() {
    return tag;
  }

  public String getType() {
    return type;
  }

  public String getLinkToStandard() {
    return linkToStandard;
  }

  public String getDescription() {
    return description;
  }
}
