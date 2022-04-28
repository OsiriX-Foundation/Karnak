/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.profile.component.errorprofile;

import org.karnak.backend.data.entity.ProfileElementEntity;

public class ProfileError {

  private ProfileElementEntity profileElementEntity;

  private String error;

  public ProfileError(ProfileElementEntity profileElementEntity) {
    this.profileElementEntity = profileElementEntity;
    this.error = null;
  }

  public ProfileError(ProfileElementEntity profileElementEntity, String error) {
    this.profileElementEntity = profileElementEntity;
    this.error = error;
  }

  public ProfileElementEntity getProfileElement() {
    return profileElementEntity;
  }

  public void setProfileElement(ProfileElementEntity profileElementEntity) {
    this.profileElementEntity = profileElementEntity;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
