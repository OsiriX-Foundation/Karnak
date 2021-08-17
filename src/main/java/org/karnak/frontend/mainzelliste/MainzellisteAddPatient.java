/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.mainzelliste;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.api.MainzellisteApi;
import org.karnak.backend.api.rqbody.Fields;
import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.frontend.component.ConfirmDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainzellisteAddPatient extends VerticalLayout {

  protected static final Logger LOGGER = LoggerFactory.getLogger(MainzellisteAddPatient.class);
  private static final String ERROR_MESSAGE_PATIENT = "Length must be between 1 and 50.";
  private final Binder<MainzellistePatient> binder;

  private TextField externalIdField;
  private TextField patientIdField;
  private TextField patientFirstNameField;
  private TextField patientLastNameField;
  private TextField issuerOfPatientIdField;
  private DatePicker patientBirthDateField;
  private Select<String> patientSexField;
  private Button saveInMainzellisteButton;
  private Button clearFieldsButton;

  private HorizontalLayout horizontalLayout1;
  private HorizontalLayout horizontalLayout2;
  private HorizontalLayout horizontalLayout3;
  private HorizontalLayout horizontalLayoutAddClear;

  public MainzellisteAddPatient() {
    setSizeFull();
    binder = new BeanValidationBinder<>(MainzellistePatient.class);

    setElements();
    setBinder();

    saveInMainzellisteButton.addClickListener(
        click -> {
          ConfirmDialog dialog =
              new ConfirmDialog("Are you sure to send a patient in Mainzelliste database ?");
          dialog.open();
          dialog.addConfirmationListener(componentEvent -> saveInMainzelliste());
        });

    clearFieldsButton.addClickListener(click -> clearPatientFields());
    // enable/disable update button while editing
    binder.addStatusChangeListener(
        event -> {
          boolean isValid = !event.hasValidationErrors();
          boolean hasChanges = binder.hasChanges();
          saveInMainzellisteButton.setEnabled(hasChanges && isValid);
        });

    horizontalLayoutAddClear.add(clearFieldsButton, saveInMainzellisteButton);
    horizontalLayout1.add(
        externalIdField, patientIdField, patientFirstNameField, patientLastNameField);
    horizontalLayout2.add(issuerOfPatientIdField, patientBirthDateField, patientSexField);
    horizontalLayout3.add(horizontalLayoutAddClear);
    add(horizontalLayout1, horizontalLayout2, horizontalLayout3);
  }

  private void setElements() {
    externalIdField = new TextField("External Pseudonym");
    externalIdField.setWidth("25%");
    patientIdField = new TextField("Patient ID");
    patientIdField.setWidth("25%");
    patientFirstNameField = new TextField("Patient first name");
    patientFirstNameField.setWidth("25%");
    patientLastNameField = new TextField("Patient last name");
    patientLastNameField.setWidth("25%");
    issuerOfPatientIdField = new TextField("Issuer of patient ID");
    issuerOfPatientIdField.setWidth("33%");
    patientBirthDateField = new DatePicker("Patient Birth Date");
    patientBirthDateField.setWidth("33%");
    patientSexField = new Select<>();
    patientSexField.setLabel("Patient Sex");
    patientSexField.setItems("M", "F", "O");
    patientSexField.setWidth("33%");

    saveInMainzellisteButton = new Button("Save patient in Mainzelliste");
    saveInMainzellisteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    saveInMainzellisteButton.setIcon(new IronIcon("icons", "icons:send"));

    horizontalLayoutAddClear = new HorizontalLayout();
    horizontalLayoutAddClear.getStyle().set("margin-left", "auto");

    horizontalLayout1 = new HorizontalLayout();
    horizontalLayout2 = new HorizontalLayout();
    horizontalLayout3 = new HorizontalLayout();
    horizontalLayout1.setSizeFull();
    horizontalLayout2.setSizeFull();
    horizontalLayout3.setSizeFull();

    clearFieldsButton = new Button("Clear");
  }

  public void setBinder() {
    binder
        .forField(externalIdField)
        .withValidator(StringUtils::isNotBlank, "External Pseudonym is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .bind("pseudonym");

    binder
        .forField(patientIdField)
        .withValidator(StringUtils::isNotBlank, "Patient ID is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .bind("patientId");

    binder
        .forField(patientFirstNameField)
        .withValidator(new StringLengthValidator("Length must be between 0 and 50.", 0, 50))
        .bind("patientFirstName");

    binder
        .forField(patientLastNameField)
        .withValidator(StringUtils::isNotBlank, "Patient last name is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .bind("patientLastName");

    binder
        .forField(issuerOfPatientIdField)
        .withValidator(new StringLengthValidator("Length must be between 0 and 50.", 0, 50))
        .bind("issuerOfPatientId");

    binder
        .forField(patientBirthDateField)
        .asRequired("Please choose a date")
        .bind("patientBirthDate");

    binder
        .forField(patientSexField)
        .withValidator(StringUtils::isNotBlank, "Patient Sex is empty")
        .withValidator(new StringLengthValidator("Length must be 1.", 1, 1))
        .bind("patientSex");
  }

  public void clearPatientFields() {
    externalIdField.clear();
    patientIdField.clear();
    patientLastNameField.clear();
    issuerOfPatientIdField.clear();
    patientBirthDateField.clear();
    patientSexField.clear();
    binder.readBean(null);
  }

  public void saveInMainzelliste() {
    binder.validate();
    if (binder.isValid()) {
      final Fields newPatientFields =
          new Fields(
              patientIdField.getValue(),
              String.format(
                  "%s^%s", patientLastNameField.getValue(), patientFirstNameField.getValue()),
              patientBirthDateField.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
              patientSexField.getValue(),
              issuerOfPatientIdField.getValue());

      try {
        final MainzellisteApi mainzellisteApi = new MainzellisteApi();
        final String pseudonym =
            mainzellisteApi.addExternalID(newPatientFields, externalIdField.getValue());
        if (pseudonym != null) {
          final String strPatient =
              "ExternalID: "
                  + externalIdField.getValue()
                  + " "
                  + "PatientID:"
                  + newPatientFields.get_patientID()
                  + " "
                  + "PatientName:"
                  + newPatientFields.get_patientName()
                  + " "
                  + "IssuerOfPatientID:"
                  + newPatientFields.get_issuerOfPatientID()
                  + " "
                  + "PatientSex:"
                  + newPatientFields.get_patientSex()
                  + " "
                  + "PatientBirthDate:"
                  + newPatientFields.get_patientBirthDate();
          LOGGER.info("Added a new patient in Mainzelliste: {}", strPatient);
        }
      } catch (Exception e) {
        LOGGER.error("Cannot create a new patient with Mainzelliste API", e);
      }
      binder.readBean(null);
    }
  }
}
