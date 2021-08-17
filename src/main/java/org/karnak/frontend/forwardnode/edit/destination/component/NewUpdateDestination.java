/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.edit.destination.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.enums.DestinationType;
import org.karnak.frontend.forwardnode.edit.component.ButtonSaveDeleteCancel;
import org.karnak.frontend.forwardnode.edit.destination.DestinationLogic;

@SuppressWarnings("serial")
public class NewUpdateDestination extends VerticalLayout {

  private final FormDICOM formDICOM;
  private final FormSTOW formSTOW;
  private final Binder<DestinationEntity> binderFormDICOM;
  private final Binder<DestinationEntity> binderFormSTOW;
  private final ButtonSaveDeleteCancel buttonDestinationDICOMSaveDeleteCancel;
  private final ButtonSaveDeleteCancel buttonDestinationSTOWSaveDeleteCancel;
  private DestinationEntity currentDestinationEntity;
  private DestinationLogic destinationLogic;

  public NewUpdateDestination() {
    setSizeFull();

    this.formDICOM = new FormDICOM();
    this.formSTOW = new FormSTOW();

    this.binderFormDICOM = new BeanValidationBinder<>(DestinationEntity.class);
    this.binderFormSTOW = new BeanValidationBinder<>(DestinationEntity.class);
    this.buttonDestinationDICOMSaveDeleteCancel = new ButtonSaveDeleteCancel();
    this.buttonDestinationSTOWSaveDeleteCancel = new ButtonSaveDeleteCancel();
    this.currentDestinationEntity = null;

    this.formDICOM.init(binderFormDICOM, buttonDestinationDICOMSaveDeleteCancel);
    this.formSTOW.init(binderFormSTOW, buttonDestinationSTOWSaveDeleteCancel);
  }

  public void load(DestinationEntity destinationEntity, DestinationType type) {
    if (destinationEntity != null) {
      currentDestinationEntity = destinationEntity;
      buttonDestinationDICOMSaveDeleteCancel.getDelete().setEnabled(true);
      buttonDestinationSTOWSaveDeleteCancel.getDelete().setEnabled(true);
    } else {
      currentDestinationEntity =
          type == DestinationType.stow
              ? DestinationEntity.ofStowEmpty()
              : DestinationEntity.ofDicomEmpty();
      buttonDestinationDICOMSaveDeleteCancel.getDelete().setEnabled(false);
      buttonDestinationSTOWSaveDeleteCancel.getDelete().setEnabled(false);
    }
    setView(type);
  }

  public void setView(DestinationType type) {
    removeAll();
    if (type == DestinationType.stow) {
      add(formSTOW);
      binderFormSTOW.readBean(currentDestinationEntity);
    } else if (type == DestinationType.dicom) {
      add(formDICOM);
      binderFormDICOM.readBean(currentDestinationEntity);
    }
  }

  public Button getButtonDICOMCancel() {
    return buttonDestinationDICOMSaveDeleteCancel.getCancel();
  }

  public Button getButtonSTOWCancel() {
    return buttonDestinationSTOWSaveDeleteCancel.getCancel();
  }

  public ButtonSaveDeleteCancel getButtonDestinationDICOMSaveDeleteCancel() {
    return buttonDestinationDICOMSaveDeleteCancel;
  }

  public ButtonSaveDeleteCancel getButtonDestinationSTOWSaveDeleteCancel() {
    return buttonDestinationSTOWSaveDeleteCancel;
  }

  public Binder<DestinationEntity> getBinderFormDICOM() {
    return binderFormDICOM;
  }

  public Binder<DestinationEntity> getBinderFormSTOW() {
    return binderFormSTOW;
  }

  public DestinationEntity getCurrentDestinationEntity() {
    return currentDestinationEntity;
  }

  public void setCurrentDestinationEntity(DestinationEntity currentDestinationEntity) {
    this.currentDestinationEntity = currentDestinationEntity;
  }

  public FormDICOM getFormDICOM() {
    return formDICOM;
  }

  public FormSTOW getFormSTOW() {
    return formSTOW;
  }
}
