/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.profilepipe;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.data.entity.MaskEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.enums.DestinationType;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.springframework.boot.test.context.SpringBootTest;
import org.weasis.dicom.param.AttributeEditorContext;

@SpringBootTest
class ProfileTest {

  @Test
  void should_apply() {

    // Init data
    Attributes attributes = new Attributes();
    ExternalIDProviderEntity externalIDProviderEntity =
        new ExternalIDProviderEntity(true, ExternalIDProviderType.EXTID_IN_TAG, null);
    DestinationEntity destinationEntity = new DestinationEntity();
    destinationEntity.setDestinationType(DestinationType.dicom);
    ProjectEntity projectEntity = new ProjectEntity();
    ProfileEntity profileEntityProject = new ProfileEntity();
    projectEntity.setProfileEntity(profileEntityProject);
    byte[] tabByte = new byte[16];
    tabByte[0] = 1;
    projectEntity.setSecret(tabByte);
    destinationEntity.setProjectEntity(projectEntity);
    destinationEntity.setExternalIDProviderEntity(externalIDProviderEntity);
    destinationEntity.setTag("0008,0080");
    destinationEntity.setSavePseudonym(false);
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
    profileElementEntityCleanPixelData.setCondition("!tagValueContains(#Tag.StationName,'ICT256')");

    profileElementEntities.add(profileElementEntityBasic);
    profileElementEntities.add(profileElementEntityCleanPixelData);
    profileEntity.setProfileElementEntities(profileElementEntities);
    profileEntity.setDefaultIssuerOfPatientId("defaultIssuerOfPatientId");
    Set<MaskEntity> maskEntities = new HashSet<>();
    MaskEntity maskEntity = new MaskEntity();
    maskEntities.add(maskEntity);
    maskEntity.setColor("1234567897");
    maskEntity.setStationName("stationName");
    maskEntity.setRectangles(Arrays.asList(new Rectangle()));
    profileEntity.setMaskEntities(maskEntities);
    AttributeEditorContext context = new AttributeEditorContext("tsuid", null, null);
    attributes.setString(Tag.PatientID, VR.SH, "patientID");
    attributes.setString(Tag.SeriesInstanceUID, VR.SH, "seriesInstanceUID");
    attributes.setString(Tag.SOPInstanceUID, VR.SH, "sopInstanceUID");
    attributes.setString(Tag.IssuerOfPatientID, VR.SH, "issuerOfPatientID");
    attributes.setString(Tag.PixelData, VR.SH, "pixelData");
    attributes.setString(Tag.SOPClassUID, VR.SH, "1.2.840.10008.5.1.4.1.1.88.74");
    attributes.setString(Tag.BurnedInAnnotation, VR.SH, "YES");
    attributes.setString(Tag.StationName, VR.SH, "stationName");
    attributes.setString(524416, VR.SH, "pseudonym");

    // Call method
    Profile profile = new Profile(profileEntity);
    // profile.init(profileEntity);
    profile.apply(attributes, destinationEntity, profileEntity, context);

    // Test results
    Assert.assertEquals("NONE", context.getAbort().name());
    Assert.assertNull(context.getMaskArea());
  }

  @Test
  void should_evaluate_condition_clean_pixel_case_no_condition() {

    // Init data
    ProfileEntity profileEntity = new ProfileEntity();
    Attributes attributes = new Attributes();
    Set<ProfileElementEntity> profileElementEntities = new HashSet<>();
    ProfileElementEntity profileElementEntityCleanPixelData = new ProfileElementEntity();
    profileElementEntityCleanPixelData.setCodename("clean.pixel.data");
    profileElementEntityCleanPixelData.setName("nameCleanPixel");
    profileElementEntityCleanPixelData.setAction("ReplaceNull");
    profileElementEntityCleanPixelData.setCondition(null);

    profileElementEntities.add(profileElementEntityCleanPixelData);
    profileEntity.setProfileElementEntities(profileElementEntities);
    Profile profile = new Profile(profileEntity);

    // Evaluate condition
    boolean evaluation = profile.evaluateConditionCleanPixelData(attributes);

    // Test results
    Assert.assertTrue(evaluation);
  }

  @Test
  void should_evaluate_condition_clean_pixel_case_exclude_station_name() {

    // Init data
    ProfileEntity profileEntity = new ProfileEntity();
    Attributes attributes = new Attributes();
    attributes.setString(Tag.StationName, VR.SH, "ICT256");
    Set<ProfileElementEntity> profileElementEntities = new HashSet<>();
    ProfileElementEntity profileElementEntityCleanPixelData = new ProfileElementEntity();
    profileElementEntityCleanPixelData.setCodename("clean.pixel.data");
    profileElementEntityCleanPixelData.setName("nameCleanPixel");
    profileElementEntityCleanPixelData.setAction("ReplaceNull");
    profileElementEntityCleanPixelData.setCondition("!tagValueContains(#Tag.StationName,'ICT256')");

    profileElementEntities.add(profileElementEntityCleanPixelData);
    profileEntity.setProfileElementEntities(profileElementEntities);
    Profile profile = new Profile(profileEntity);

    // Evaluate condition
    boolean evaluation = profile.evaluateConditionCleanPixelData(attributes);

    // Test results
    Assert.assertFalse(evaluation);
  }

  @Test
  void should_evaluate_condition_clean_pixel_case_include_station_name() {

    // Init data
    ProfileEntity profileEntity = new ProfileEntity();
    Attributes attributes = new Attributes();
    attributes.setString(Tag.StationName, VR.SH, "ICT256");
    Set<ProfileElementEntity> profileElementEntities = new HashSet<>();
    ProfileElementEntity profileElementEntityCleanPixelData = new ProfileElementEntity();
    profileElementEntityCleanPixelData.setCodename("clean.pixel.data");
    profileElementEntityCleanPixelData.setName("nameCleanPixel");
    profileElementEntityCleanPixelData.setAction("ReplaceNull");
    profileElementEntityCleanPixelData.setCondition("tagValueContains(#Tag.StationName,'ICT256')");

    profileElementEntities.add(profileElementEntityCleanPixelData);
    profileEntity.setProfileElementEntities(profileElementEntities);
    Profile profile = new Profile(profileEntity);

    // Evaluate condition
    boolean evaluation = profile.evaluateConditionCleanPixelData(attributes);

    // Test results
    Assert.assertTrue(evaluation);
  }
}
