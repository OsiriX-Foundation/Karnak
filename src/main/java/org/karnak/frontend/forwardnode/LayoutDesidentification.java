/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.enums.IdTypes;
import org.karnak.backend.service.ProjectService;
import org.karnak.frontend.project.MainViewProjects;
import org.karnak.frontend.util.UIS;

public class LayoutDesidentification extends Div {

  final String[] extidSentence = {
    "Pseudonym are generate automatically",
    "Pseudonym is already store in KARNAK",
    "Pseudonym is in a DICOM tag"
  };
  private final String LABEL_CHECKBOX_DESIDENTIFICATION = "Activate de-identification";
  private final String LABEL_DISCLAIMER_DEIDENTIFICATION =
      "In order to ensure complete de-identification, visual verification of metadata and images is necessary.";
  private ProjectService projectService;
  private Checkbox checkboxDesidentification;
  private Label labelDisclaimer;
  private Checkbox checkboxUseAsPatientName;
  private ProjectDropDown projectDropDown;
  private ExtidPresentInDicomTagView extidPresentInDicomTagView;
  private Binder<DestinationEntity> destinationBinder;
  private Div div;
  private DesidentificationName desidentificationName;
  private WarningNoProjectsDefined warningNoProjectsDefined;
  private Select<String> extidListBox;

  public LayoutDesidentification() {}

  public void init(final Binder<DestinationEntity> binder, final ProjectService projectService) {
    this.projectService = projectService;
    this.projectDropDown = new ProjectDropDown();
    this.projectDropDown.setItems(projectService.getAllProjects());
    this.projectDropDown.setItemLabelGenerator(ProjectEntity::getName);
    this.desidentificationName = new DesidentificationName();
    this.warningNoProjectsDefined = new WarningNoProjectsDefined();
    this.warningNoProjectsDefined.setTextBtnCancel("Continue");
    this.warningNoProjectsDefined.setTextBtnValidate("Create a project");

    setDestinationBinder(binder);

    setElements();
    setBinder();
    setEventCheckboxDesidentification();
    setEventExtidListBox();
    setEventWarningDICOM();

    add(UIS.setWidthFull(new HorizontalLayout(checkboxDesidentification, div)));

    if (checkboxDesidentification.getValue()) {
      div.add(labelDisclaimer, projectDropDown, desidentificationName, extidListBox);
    }

    projectDropDown.addValueChangeListener(
        event -> {
          setTextOnSelectionProject(event.getValue());
        });
  }

  private void setElements() {
    checkboxDesidentification = new Checkbox(LABEL_CHECKBOX_DESIDENTIFICATION);
    checkboxDesidentification.setValue(true);
    checkboxDesidentification.setMinWidth("25%");

    labelDisclaimer = new Label(LABEL_DISCLAIMER_DEIDENTIFICATION);
    labelDisclaimer.getStyle().set("color", "red");
    labelDisclaimer.setMinWidth("75%");
    labelDisclaimer.getStyle().set("right", "0px");

    projectDropDown.setLabel("Choose a project");
    projectDropDown.setWidth("100%");

    extidListBox = new Select<>();
    extidListBox.setLabel("Pseudonym type");
    extidListBox.setWidth("100%");
    extidListBox.getStyle().set("right", "0px");
    extidListBox.setItems(extidSentence);

    checkboxUseAsPatientName = new Checkbox("Use as Patient Name");

    extidPresentInDicomTagView = new ExtidPresentInDicomTagView(destinationBinder);
    div = new Div();
    div.setWidth("100%");
  }

  private void setEventWarningDICOM() {
    warningNoProjectsDefined
        .getBtnCancel()
        .addClickListener(
            btnEvent -> {
              checkboxDesidentification.setValue(false);
              warningNoProjectsDefined.close();
            });
    warningNoProjectsDefined
        .getBtnValidate()
        .addClickListener(
            btnEvent -> {
              warningNoProjectsDefined.close();
              navigateToProject();
            });
  }

  private void navigateToProject() {
    getUI()
        .ifPresent(
            nav -> {
              nav.navigate(MainViewProjects.VIEW_NAME.toLowerCase());
            });
  }

  private void setEventCheckboxDesidentification() {
    checkboxDesidentification.addValueChangeListener(
        event -> {
          if (event.getValue() != null) {
            if (event.getValue()) {
              if (projectService.getAllProjects().size() > 0) {
                div.add(labelDisclaimer, projectDropDown, desidentificationName, extidListBox);
                setTextOnSelectionProject(projectDropDown.getValue());
              } else {
                warningNoProjectsDefined.open();
              }
            } else {
              div.remove(labelDisclaimer, projectDropDown, desidentificationName);
              extidListBox.setValue(extidSentence[0]);
              checkboxUseAsPatientName.clear();
              extidPresentInDicomTagView.clear();
              div.remove(extidListBox);
              remove(checkboxUseAsPatientName);
              div.remove(extidPresentInDicomTagView);
            }
          }
        });
  }

  private void setTextOnSelectionProject(ProjectEntity projectEntity) {
    if (projectEntity != null && projectEntity.getProfileEntity() != null) {
      desidentificationName.setShowValue(
          String.format("The profile %s will be used", projectEntity.getProfileEntity().getName()));
    } else if (projectEntity != null && projectEntity.getProfileEntity() == null) {
      desidentificationName.setShowValue("No profiles defined in the project");
    } else {
      desidentificationName.removeAll();
    }
  }

  private void setEventExtidListBox() {
    extidListBox.addValueChangeListener(
        event -> {
          if (event.getValue() != null) {
            if (event.getValue().equals(extidSentence[0])) {
              checkboxUseAsPatientName.clear();
              extidPresentInDicomTagView.clear();
              div.remove(checkboxUseAsPatientName);
              div.remove(extidPresentInDicomTagView);
            } else {
              div.add(UIS.setWidthFull(checkboxUseAsPatientName));
              if (event.getValue().equals(extidSentence[1])) {
                extidPresentInDicomTagView.clear();
                div.remove(extidPresentInDicomTagView);
              } else {
                extidPresentInDicomTagView.enableComponent();
                div.add(extidPresentInDicomTagView);
              }
            }
          }
        });
  }

  private void setBinder() {
    destinationBinder
        .forField(checkboxDesidentification)
        .bind(DestinationEntity::isDesidentification, DestinationEntity::setDesidentification);
    destinationBinder
        .forField(projectDropDown)
        .withValidator(
            project ->
                project != null
                    || (project == null && checkboxDesidentification.getValue() == false),
            "Choose a project")
        .bind(DestinationEntity::getProjectEntity, DestinationEntity::setProjectEntity);

    destinationBinder
        .forField(extidListBox)
        .withValidator(type -> type != null, "Choose pseudonym type\n")
        .bind(
            destination -> {
              if (destination.getIdTypes().equals(IdTypes.PID)) {
                return extidSentence[0];
              } else if (destination.getIdTypes().equals(IdTypes.EXTID)) {
                return extidSentence[1];
              } else {
                return extidSentence[2];
              }
            },
            (destination, s) -> {
              if (s.equals(extidSentence[0])) {
                destination.setIdTypes(IdTypes.PID);
              } else if (s.equals(extidSentence[1])) {
                destination.setIdTypes(IdTypes.EXTID);
              } else {
                destination.setIdTypes(IdTypes.ADD_EXTID);
              }
            });

    destinationBinder
        .forField(checkboxUseAsPatientName)
        .bind(
            DestinationEntity::getPseudonymAsPatientName,
            DestinationEntity::setPseudonymAsPatientName);
  }

  public Binder<DestinationEntity> getDestinationBinder() {
    return destinationBinder;
  }

  public void setDestinationBinder(Binder<DestinationEntity> destinationBinder) {
    this.destinationBinder = destinationBinder;
  }
}
