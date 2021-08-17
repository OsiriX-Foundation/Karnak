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

import static org.karnak.backend.enums.ExternalIDProviderType.EXTID_IN_TAG;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.karnak.ExternalIDProvider;
import org.karnak.backend.config.ExternalIDProviderConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.karnak.frontend.component.ProjectDropDown;
import org.karnak.frontend.project.ProjectView;
import org.karnak.frontend.util.UIS;

public class LayoutDesidentification extends VerticalLayout {

  private static final String LABEL_CHECKBOX_DESIDENTIFICATION = "Activate de-identification";
  private static final String LABEL_DISCLAIMER_DEIDENTIFICATION =
      "In order to ensure complete de-identification, visual verification of metadata and images is necessary.";
  private static final String LABEL_DEFAULT_ISSUER =
      "If this field is empty, the Issuer of Patient ID is not used to define the authenticity of the patient";

  private Checkbox checkboxDesidentification;
  private Label labelDisclaimer;

  private ProjectDropDown projectDropDown;
  private ExtidPresentInDicomTagView extidPresentInDicomTagView;
  private Div divExtID;
  private Binder<DestinationEntity> destinationBinder;
  private Div div;
  private DesidentificationName desidentificationName;
  private WarningNoProjectsDefined warningNoProjectsDefined;
  private Select<String> extidListBox;
  private TextField issuerOfPatientIDByDefault;
  private HashMap<String, ExternalIDProvider> externalIDProviderImplMap;

  public LayoutDesidentification() {
    this.externalIDProviderImplMap =
        ExternalIDProviderConfig.getInstance().externalIDProviderImplMap();
  }

  public void init(final Binder<DestinationEntity> binder) {
    this.issuerOfPatientIDByDefault = new TextField();
    this.projectDropDown = new ProjectDropDown();
    this.projectDropDown.setItemLabelGenerator(ProjectEntity::getName);
    this.desidentificationName = new DesidentificationName();
    this.warningNoProjectsDefined = new WarningNoProjectsDefined();
    this.warningNoProjectsDefined.setTextBtnCancel("Continue");
    this.warningNoProjectsDefined.setTextBtnValidate("Create a project");

    setDestinationBinder(binder);

    setElements();
    setBinder();
    setEventExtidListBox();
    setEventWarningDICOM();

    setPadding(true);

    add(UIS.setWidthFull(new HorizontalLayout(checkboxDesidentification, div)));

    if (checkboxDesidentification.getValue()) {
      div.add(
          labelDisclaimer,
          projectDropDown,
          desidentificationName,
          divExtID,
          issuerOfPatientIDByDefault);
    }

    projectDropDown.addValueChangeListener(event -> setTextOnSelectionProject(event.getValue()));
  }

  private void setElements() {
    issuerOfPatientIDByDefault.setLabel("Issuer of Patient ID by default");
    issuerOfPatientIDByDefault.setWidth("100%");
    issuerOfPatientIDByDefault.setPlaceholder(LABEL_DEFAULT_ISSUER);
    UIS.setTooltip(issuerOfPatientIDByDefault, LABEL_DEFAULT_ISSUER);
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
    final List<String> externalIDProviderTypeSentenceList = new ArrayList<>();

    for (ExternalIDProviderType enumType : ExternalIDProviderType.values()) {
      if (enumType.isByDefault()) {
        externalIDProviderTypeSentenceList.add(enumType.getDescription());
      }
    }

    externalIDProviderImplMap.forEach(
        (s, externalIDProvider) -> {
          externalIDProviderTypeSentenceList.add(externalIDProvider.getDescription());
        });
    extidListBox.setItems(externalIDProviderTypeSentenceList);

    extidPresentInDicomTagView = new ExtidPresentInDicomTagView(destinationBinder);
    div = new Div();
    div.setWidth("100%");

    divExtID = new Div();
    divExtID.add(extidListBox);
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
              nav.navigate(ProjectView.VIEW_NAME.toLowerCase());
            });
  }

  public void setTextOnSelectionProject(ProjectEntity projectEntity) {
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
            if (event.getValue().equals(EXTID_IN_TAG.getDescription())) {
              div.add(extidPresentInDicomTagView);
            } else {
              extidPresentInDicomTagView.clear();
              div.remove(extidPresentInDicomTagView);
            }
          }
        });
  }

  private void setBinder() {
    destinationBinder
        .forField(issuerOfPatientIDByDefault)
        .bind(
            DestinationEntity::getIssuerByDefault,
            (destinationEntity, s) -> {
              if (checkboxDesidentification.getValue()) {
                destinationEntity.setIssuerByDefault(s);
              } else {
                destinationEntity.setIssuerByDefault("");
              }
            });
    destinationBinder
        .forField(checkboxDesidentification)
        .bind(DestinationEntity::isDesidentification, DestinationEntity::setDesidentification);
    destinationBinder
        .forField(projectDropDown)
        .withValidator(
            project ->
                project != null || (project == null && !checkboxDesidentification.getValue()),
            "Choose a project")
        .bind(DestinationEntity::getProjectEntity, DestinationEntity::setProjectEntity);
  }

  public Binder<DestinationEntity> getDestinationBinder() {
    return destinationBinder;
  }

  public void setDestinationBinder(Binder<DestinationEntity> destinationBinder) {
    this.destinationBinder = destinationBinder;
  }

  public ProjectDropDown getProjectDropDown() {
    return projectDropDown;
  }

  public Checkbox getCheckboxDesidentification() {
    return checkboxDesidentification;
  }

  public Label getLabelDisclaimer() {
    return labelDisclaimer;
  }

  public ExtidPresentInDicomTagView getExtidPresentInDicomTagView() {
    return extidPresentInDicomTagView;
  }

  public Div getDivExtID() {
    return divExtID;
  }

  public Div getDiv() {
    return div;
  }

  public DesidentificationName getDesidentificationName() {
    return desidentificationName;
  }

  public WarningNoProjectsDefined getWarningNoProjectsDefined() {
    return warningNoProjectsDefined;
  }

  public Select<String> getExtidListBox() {
    return extidListBox;
  }

  public TextField getIssuerOfPatientIDByDefault() {
    return issuerOfPatientIDByDefault;
  }

  public HashMap<String, ExternalIDProvider> getExternalIDProviderImplMap() {
    return externalIDProviderImplMap;
  }
}
