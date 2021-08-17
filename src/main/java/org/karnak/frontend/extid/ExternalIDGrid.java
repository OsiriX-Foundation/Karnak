/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.cache.ExternalIDCSVPatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.cache.PseudonymPatient;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.util.PatientClientUtil;
import org.vaadin.klaudeta.PaginatedGrid;

public class ExternalIDGrid extends PaginatedGrid<ExternalIDCSVPatient> {

  private static final String ERROR_MESSAGE_PATIENT = "Length must be between 1 and 50.";
  private static final String LABEL_SAVE = "Save";
  private static final String LABEL_CANCEL = "Cancel";
  private static final String LABEL_FILTER = "Filter";
  private final Binder<ExternalIDCSVPatient> binder;
  private final List<ExternalIDCSVPatient> patientList;
  private transient PatientClient externalIDCache;
  private transient ProjectEntity projectEntity;
  private Button deletePatientButton;
  private Button saveEditPatientButton;
  private Button cancelEditPatientButton;
  private Editor<ExternalIDCSVPatient> editor;
  private Collection<Button> editButtons;
  private TextField externalIdField;
  private TextField patientIdField;
  private TextField patientFirstNameField;
  private TextField patientLastNameField;
  private TextField issuerOfPatientIdField;
  private Grid.Column<ExternalIDCSVPatient> deleteColumn;

  private Grid.Column<ExternalIDCSVPatient> extidColumn;
  private Grid.Column<ExternalIDCSVPatient> patientIdColumn;
  private Grid.Column<ExternalIDCSVPatient> patientFirstNameColumn;
  private Grid.Column<ExternalIDCSVPatient> patientLastNameColumn;
  private Grid.Column<ExternalIDCSVPatient> issuerOfPatientIDColumn;

  private TextField patientIdFilter;
  private TextField extidFilter;
  private TextField patientFirstNameFilter;
  private TextField patientLastNameFilter;
  private TextField issuerOfPatientIDFilter;

  private List<ExternalIDCSVPatient> patientsListInCache = new ArrayList<>();
  private transient Collection<PseudonymPatient> duplicatePatientsList = new ArrayList<>();

  public ExternalIDGrid() {
    binder = new Binder<>(ExternalIDCSVPatient.class);
    patientList = new ArrayList<>();
    this.externalIDCache = AppConfig.getInstance().getExternalIDCache();

    setPageSize(10);
    setPaginatorSize(2);

    setSizeFull();
    getElement()
        .addEventListener("keyup", event -> editor.cancel())
        .setFilter("event.key === 'Escape' || event.key === 'Esc'");
    setHeightByRows(true);
    setItems(patientList);
    setElements();
    setBinder();
    readAllCacheValue();
    editor.addOpenListener(
        e -> {
          editButtons.stream().forEach(button -> button.setEnabled(!editor.isOpen()));
          deleteColumn.setVisible(false);
        });

    editor.addCloseListener(
        e -> {
          editButtons.stream().forEach(button -> button.setEnabled(!editor.isOpen()));
          deleteColumn.setVisible(true);
        });

    saveEditPatientButton.addClickListener(
        e -> {
          final ExternalIDCSVPatient patientEdit =
              new ExternalIDCSVPatient(
                  externalIdField.getValue(),
                  patientIdField.getValue(),
                  patientFirstNameField.getValue(),
                  patientLastNameField.getValue(),
                  issuerOfPatientIdField.getValue(),
                  projectEntity.getId());
          externalIDCache.remove(
              PatientClientUtil.generateKey(editor.getItem(), projectEntity.getId()));
          externalIDCache.put(
              PatientClientUtil.generateKey(patientEdit, projectEntity.getId()), patientEdit);
          editor.save();
        });
    saveEditPatientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    cancelEditPatientButton.addClickListener(e -> editor.cancel());
  }

  private void setElements() {
    extidColumn =
        addColumn(ExternalIDCSVPatient::getPseudonym)
            .setHeader("External Pseudonym")
            .setSortable(true);
    patientIdColumn =
        addColumn(ExternalIDCSVPatient::getPatientId).setHeader("Patient ID").setSortable(true);
    patientFirstNameColumn =
        addColumn(ExternalIDCSVPatient::getPatientFirstName)
            .setHeader("Patient first name")
            .setSortable(true);
    patientLastNameColumn =
        addColumn(ExternalIDCSVPatient::getPatientLastName)
            .setHeader("Patient last name")
            .setSortable(true);
    issuerOfPatientIDColumn =
        addColumn(ExternalIDCSVPatient::getIssuerOfPatientId)
            .setHeader("Issuer of patient ID")
            .setSortable(true);
    Grid.Column<ExternalIDCSVPatient> editorColumn =
        addComponentColumn(
            patient -> {
              Button edit = new Button("Edit");
              edit.addClassName("edit");
              edit.addClickListener(
                  e -> {
                    editor.editItem(patient);
                    externalIdField.focus();
                  });
              edit.setEnabled(!editor.isOpen());
              editButtons.add(edit);
              return edit;
            });

    addFilterElements();

    editButtons = Collections.newSetFromMap(new WeakHashMap<>());
    editor = getEditor();
    editor.setBinder(binder);
    editor.setBuffered(true);

    externalIdField = new TextField();
    patientIdField = new TextField();
    patientFirstNameField = new TextField();
    patientLastNameField = new TextField();
    issuerOfPatientIdField = new TextField();

    extidColumn.setEditorComponent(externalIdField);
    patientIdColumn.setEditorComponent(patientIdField);
    patientFirstNameColumn.setEditorComponent(patientFirstNameField);
    patientLastNameColumn.setEditorComponent(patientLastNameField);
    issuerOfPatientIDColumn.setEditorComponent(issuerOfPatientIdField);

    deleteColumn =
        addComponentColumn(
            patient -> {
              deletePatientButton = new Button("Delete");
              deletePatientButton.addThemeVariants(
                  ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
              deletePatientButton.addClickListener(
                  e -> {
                    externalIDCache.remove(
                        PatientClientUtil.generateKey(patient, projectEntity.getId()));
                    readAllCacheValue();
                  });
              return deletePatientButton;
            });

    saveEditPatientButton = new Button(LABEL_SAVE);
    cancelEditPatientButton = new Button(LABEL_CANCEL);

    Div buttons = new Div(saveEditPatientButton, cancelEditPatientButton);
    editorColumn.setEditorComponent(buttons);
  }

  public void addFilterElements() {
    HeaderRow filterRow = appendHeaderRow();

    extidFilter = new TextField();

    extidFilter.addValueChangeListener(event -> checkAndUpdateAllFilters());
    extidFilter.setValueChangeMode(ValueChangeMode.EAGER);
    filterRow.getCell(extidColumn).setComponent(extidFilter);
    extidFilter.setSizeFull();
    extidFilter.setPlaceholder(LABEL_FILTER);

    patientIdFilter = new TextField();
    patientIdFilter.addValueChangeListener(event -> checkAndUpdateAllFilters());
    patientIdFilter.setValueChangeMode(ValueChangeMode.EAGER);
    filterRow.getCell(patientIdColumn).setComponent(patientIdFilter);
    patientIdFilter.setSizeFull();
    patientIdFilter.setPlaceholder(LABEL_FILTER);

    patientFirstNameFilter = new TextField();
    patientFirstNameFilter.addValueChangeListener(event -> checkAndUpdateAllFilters());
    patientFirstNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
    filterRow.getCell(patientFirstNameColumn).setComponent(patientFirstNameFilter);
    patientFirstNameFilter.setSizeFull();
    patientFirstNameFilter.setPlaceholder(LABEL_FILTER);

    patientLastNameFilter = new TextField();
    patientLastNameFilter.addValueChangeListener(event -> checkAndUpdateAllFilters());
    patientLastNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
    filterRow.getCell(patientLastNameColumn).setComponent(patientLastNameFilter);
    patientLastNameFilter.setSizeFull();
    patientLastNameFilter.setPlaceholder(LABEL_FILTER);

    issuerOfPatientIDFilter = new TextField();
    issuerOfPatientIDFilter.addValueChangeListener(event -> checkAndUpdateAllFilters());
    issuerOfPatientIDFilter.setValueChangeMode(ValueChangeMode.EAGER);
    filterRow.getCell(issuerOfPatientIDColumn).setComponent(issuerOfPatientIDFilter);
    issuerOfPatientIDFilter.setSizeFull();
    issuerOfPatientIDFilter.setPlaceholder(LABEL_FILTER);
  }

  public Div setBinder() {
    Div validationStatus = new Div();
    validationStatus.setId("validation");
    validationStatus.getStyle().set("color", "var(--theme-color, red)");
    binder
        .forField(externalIdField)
        .withValidator(StringUtils::isNotBlank, "External Pseudonym is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .withStatusLabel(validationStatus)
        .bind("pseudonym");

    binder
        .forField(patientIdField)
        .withValidator(StringUtils::isNotBlank, "Patient ID is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .withStatusLabel(validationStatus)
        .bind("patientId");

    binder
        .forField(patientFirstNameField)
        .withValidator(StringUtils::isNotBlank, "Patient firstname is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .bind("patientFirstName");

    binder
        .forField(patientLastNameField)
        .withValidator(StringUtils::isNotBlank, "Patient last name is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .bind("patientLastName");

    binder
        .forField(issuerOfPatientIdField)
        .withValidator(new StringLengthValidator("Length must be between 0 and 50.", 0, 50))
        .withStatusLabel(validationStatus)
        .bind("issuerOfPatientId");

    return validationStatus;
  }

  public void readAllCacheValue() {
    if (externalIDCache != null) {
      Collection<PseudonymPatient> pseudonymPatients = externalIDCache.getAll();
      patientsListInCache = new ArrayList<>();
      for (Iterator<PseudonymPatient> iterator = pseudonymPatients.iterator();
          iterator.hasNext(); ) {
        final ExternalIDCSVPatient patient = (ExternalIDCSVPatient) iterator.next();
        if (projectEntity != null
            && patient.getProjectID() != null
            && patient.getProjectID().equals(projectEntity.getId())) {
          patientsListInCache.add(patient);
        }
      }
      setItems(patientsListInCache);
    }
    refreshPaginator();
  }

  public void addPatient(ExternalIDCSVPatient newPatient) {
    if (!patientExist(newPatient)) {
      externalIDCache.put(
          PatientClientUtil.generateKey(newPatient, projectEntity.getId()), newPatient);
    }
  }

  public void addPatientList(List<ExternalIDCSVPatient> patientList) {
    patientList.forEach(this::addPatient);
    readAllCacheValue();
  }

  public boolean patientExist(PseudonymPatient patient) {
    final PseudonymPatient duplicatePatient =
        externalIDCache.get(PatientClientUtil.generateKey(patient, projectEntity.getId()));
    if (duplicatePatient != null) {
      duplicatePatientsList.add(duplicatePatient);
      return true;
    }
    return false;
  }

  public void checkAndUpdateAllFilters() {
    List<ExternalIDCSVPatient> filterList =
        patientsListInCache.stream().collect(Collectors.toList());

    if (!extidFilter.getValue().equals("")) {
      filterList =
          filterList.stream()
              .filter(
                  cachedPatient -> cachedPatient.getPseudonym().contains(extidFilter.getValue()))
              .collect(Collectors.toList());
    }

    if (!patientIdFilter.getValue().equals("")) {
      filterList =
          filterList.stream()
              .filter(
                  cachedPatient ->
                      cachedPatient.getPatientId().contains(patientIdFilter.getValue()))
              .collect(Collectors.toList());
    }

    if (!patientFirstNameFilter.getValue().equals("")) {
      filterList =
          filterList.stream()
              .filter(
                  cachedPatient ->
                      cachedPatient
                          .getPatientFirstName()
                          .contains(patientFirstNameFilter.getValue()))
              .collect(Collectors.toList());
    }

    if (!patientLastNameFilter.getValue().equals("")) {
      filterList =
          filterList.stream()
              .filter(
                  cachedPatient ->
                      cachedPatient.getPatientLastName().contains(patientLastNameFilter.getValue()))
              .collect(Collectors.toList());
    }

    if (!issuerOfPatientIDFilter.getValue().equals("")) {
      filterList =
          filterList.stream()
              .filter(
                  cachedPatient ->
                      cachedPatient
                          .getIssuerOfPatientId()
                          .contains(issuerOfPatientIDFilter.getValue()))
              .collect(Collectors.toList());
    }

    setItems(filterList);
  }

  public Collection<PseudonymPatient> getDuplicatePatientsList() {
    return duplicatePatientsList;
  }

  public void setDuplicatePatientsList(Collection<PseudonymPatient> duplicatePatientsList) {
    this.duplicatePatientsList = duplicatePatientsList;
  }

  public ProjectEntity getProjectEntity() {
    return projectEntity;
  }

  public void setProjectEntity(ProjectEntity projectEntity) {
    this.projectEntity = projectEntity;
  }

  public PatientClient getExternalIDCache() {
    return externalIDCache;
  }

  public void setExternalIDCache(PatientClient externalIDCache) {
    this.externalIDCache = externalIDCache;
  }

  public List<ExternalIDCSVPatient> getPatientsListInCache() {
    return patientsListInCache;
  }
}
