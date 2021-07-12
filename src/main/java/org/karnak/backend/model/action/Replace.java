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
import org.dcm4che3.util.TagUtils;
import org.karnak.backend.model.profilepipe.HMAC;
import org.slf4j.MDC;

public class Replace extends AbstractAction {

  public Replace(String symbol) {
    super(symbol);
  }

  public Replace(String symbol, String dummyValue) {
    super(symbol, dummyValue);
  }

  @Override
  public void execute(Attributes dcm, int tag, HMAC hmac) {
    if (LOGGER.isTraceEnabled()) {
      String tagValueIn = AbstractAction.getStringValue(dcm, tag);
      LOGGER.trace(
          CLINICAL_MARKER,
          PATTERN_WITH_INOUT,
          MDC.get("SOPInstanceUID"),
          TagUtils.toString(tag),
          symbol,
          tagValueIn,
          dummyValue);
    }

    if (dummyValue != null) {
      dcm.setString(tag, dcm.getVR(tag), dummyValue);
    } else {
      dcm.setNull(tag, dcm.getVR(tag));
    }
  }
}
