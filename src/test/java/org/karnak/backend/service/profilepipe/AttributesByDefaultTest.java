/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.profilepipe;

import java.util.HashSet;
import java.util.Set;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.enums.ProfileItemType;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AttributesByDefaultTest {

  @Test
  void should_set_deIdentification_method_code_sequence() {

    // Init data
    Attributes attributes = new Attributes();
    ProjectEntity projectEntity = new ProjectEntity();
    ProfileEntity profileEntity = new ProfileEntity();
    Set<ProfileElementEntity> profileElementEntities = new HashSet<>();
    ProfileElementEntity profileElementEntityBasic = new ProfileElementEntity();
    profileElementEntityBasic.setCodename("basic.dicom.profile");
    profileElementEntityBasic.setName("nameBasic");
    ProfileElementEntity profileElementEntityCleanPixelData = new ProfileElementEntity();
    profileElementEntityCleanPixelData.setCodename("clean.pixel.data");
    profileElementEntityCleanPixelData.setName("nameCleanPixel");
    profileElementEntityBasic.setPosition(1);
    profileElementEntityCleanPixelData.setPosition(2);
    profileElementEntityBasic.setAction("ReplaceNull");
    profileElementEntityCleanPixelData.setAction("ReplaceNull");
    profileElementEntities.add(profileElementEntityBasic);
    profileElementEntities.add(profileElementEntityCleanPixelData);
    profileEntity.setProfileElementEntities(profileElementEntities);
    projectEntity.setProfileEntity(profileEntity);

    // Call method
    AttributesByDefault.setDeidentificationMethodCodeSequence(attributes, projectEntity);

    // Test results
    Assert.assertNotNull(attributes.getValue(Tag.DeidentificationMethodCodeSequence));
    Sequence sequence = (Sequence) attributes.getValue(Tag.DeidentificationMethodCodeSequence);
    Assert.assertEquals("113100", sequence.get(0).getValue(Tag.CodeValue));
    Assert.assertEquals("DCM", sequence.get(0).getValue(Tag.CodingSchemeDesignator));
    Assert.assertEquals(
        ProfileItemType.getCodeMeaning("basic.dicom.profile"),
        sequence.get(0).getValue(Tag.CodeMeaning));
    Assert.assertEquals("113101", sequence.get(1).getValue(Tag.CodeValue));
    Assert.assertEquals("DCM", sequence.get(1).getValue(Tag.CodingSchemeDesignator));
    Assert.assertEquals(
        ProfileItemType.getCodeMeaning("clean.pixel.data"),
        sequence.get(1).getValue(Tag.CodeMeaning));
  }
}
