/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.editor;

import org.dcm4che3.data.Attributes;
import org.karnak.backend.model.expression.ExprConditionDestination;
import org.karnak.backend.model.expression.ExpressionResult;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;

public class ConditionEditor implements AttributeEditor {
  private String condition;

  public ConditionEditor(String condition) {
    this.condition = condition;
  }

  private static boolean validateCondition(String condition, Attributes dcm) {
    return (Boolean)
        ExpressionResult.get(condition, new ExprConditionDestination(dcm), Boolean.class);
  }

  @Override
  public void apply(Attributes dcm, AttributeEditorContext context) {
    if (!validateCondition(this.condition, dcm)) {
      context.setAbort(Abort.FILE_EXCEPTION);
      context.setAbortMessage("The instance is blocked because is does not meet the condition");
    }
  }
}
