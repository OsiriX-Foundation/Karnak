/*
 * Copyright (c) 2009-2019 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom;

import java.util.List;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.DicomState;

public abstract class ForwardDestination {

  protected final List<AttributeEditor> dicomEditors;
  private final Long id;

  public ForwardDestination(Long id, List<AttributeEditor> dicomEditors) {
    this.dicomEditors = dicomEditors;
    this.id = id;
  }

  public List<AttributeEditor> getDicomEditors() {
    return dicomEditors;
  }

  public Long getId() {
    return id;
  }

  public abstract ForwardDicomNode getForwardDicomNode();

  public abstract void stop();

  public abstract DicomState getState();
}
