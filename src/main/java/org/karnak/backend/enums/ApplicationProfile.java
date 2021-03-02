/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.enums;

/** Application profile */
public enum ApplicationProfile {

  /** Profile OIDC: use to activate the application profile: application-oidc.yml */
  OIDC("oidc");

  /** Name of the profile to activate */
  final String code;

  ApplicationProfile(final String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
