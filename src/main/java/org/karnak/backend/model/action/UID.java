/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.action;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;
import org.dcm4che3.util.TagUtils;
import org.karnak.backend.model.profilepipe.HMAC;
import org.slf4j.MDC;

public class UID extends AbstractAction {

  public UID(String symbol) {
    super(symbol);
  }

  @Override
  public void execute(Attributes dcm, int tag, HMAC hmac) {
    String uidValue = dcm.getString(tag);
    String uidHashed = null;
    if (uidValue != null) {
      uidHashed = hmac.uidHash(uidValue);
      dcm.setString(tag, VR.UI, uidHashed);
    }
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace(
          CLINICAL_MARKER,
          PATTERN_WITH_INOUT,
          MDC.get("SOPInstanceUID"),
          TagUtils.toString(tag),
          symbol,
          uidValue,
          uidHashed);
    }
  }
}
