/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.profile.component.editprofile;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProfileElementMainViewTest {

  @Test
  void should_create_profile_element_main_view() {

    // Call constructor
    ProfileElementMainView profileElementMainView = new ProfileElementMainView();

    // Test results
    Assert.assertNotNull(profileElementMainView);
  }

  @Test
  void should_set_profile_and_enable() {

    // Call constructor
    ProfileElementMainView profileElementMainView = new ProfileElementMainView();

    // Init data
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

    // Call method
    profileElementMainView.setProfile(profileEntity);

    // Test results
    Assert.assertTrue(profileElementMainView.isEnabled());
  }
}
