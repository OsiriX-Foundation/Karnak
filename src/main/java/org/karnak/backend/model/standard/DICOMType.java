/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.standard;

import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.exception.StandardDICOMException;
import org.karnak.backend.util.MetadataDICOMObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DICOMType {

  private static final Logger LOGGER = LoggerFactory.getLogger(DICOMType.class);

  public static String getBySOP(Attributes dcm, int tag) {
    final StandardDICOM standardDICOM = AppConfig.getInstance().getStandardDICOM();
    final String sopUID = MetadataDICOMObject.getValue(dcm, Tag.SOPClassUID);
    final String tagPath = MetadataDICOMObject.getTagPath(dcm, tag);

    String currentType = null;
    try {
      List<ModuleAttribute> moduleAttribute = standardDICOM.getAttributesBySOP(sopUID, tagPath);
      if (moduleAttribute.size() == 1) {
        currentType = moduleAttribute.get(0).getType();
      } else if (moduleAttribute.size() > 1) {
        currentType = ModuleAttribute.getStrictedType(moduleAttribute);
      } else {
        LOGGER.warn(
            String.format("Could not found the attribute %s in the SOP %s.", tagPath, sopUID));
      }
    } catch (StandardDICOMException standardDICOMException) {
      LOGGER.error(
          String.format(
              "Could not find a DICOM type with the SOP %s and the attribute %s", sopUID, tagPath),
          standardDICOMException);
    }
    return currentType;
  }
}
