/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.cache;

import org.springframework.stereotype.Component;

@Component
public class ExternalIDProviderCache extends PatientClient {
  private static final String NAME = "externalid_provider";
  private static final int TTL_SECONDS = 15 * 60;

  public ExternalIDProviderCache() {
    super(NAME, TTL_SECONDS);
  }
}
