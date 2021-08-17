/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.edit;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import java.util.HashSet;
import java.util.Set;
import org.dcm4che3.util.TagUtils;
import org.karnak.ExternalIDProvider;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.karnak.backend.enums.DestinationType;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.service.ProjectService;
import org.karnak.backend.service.SOPClassUIDService;
import org.karnak.backend.util.DoubleToIntegerConverter;
import org.karnak.backend.util.ExternalIDProviderUtil;
import org.karnak.frontend.component.ConfirmDialog;
import org.karnak.frontend.forwardnode.ForwardNodeLogic;
import org.karnak.frontend.forwardnode.edit.component.ButtonSaveDeleteCancel;
import org.karnak.frontend.forwardnode.edit.component.EditAETitleDescription;
import org.karnak.frontend.forwardnode.edit.component.TabSourcesDestination;
import org.karnak.frontend.forwardnode.edit.destination.DestinationView;
import org.karnak.frontend.forwardnode.edit.destination.component.FilterBySOPClassesForm;
import org.karnak.frontend.forwardnode.edit.destination.component.LayoutDesidentification;
import org.karnak.frontend.forwardnode.edit.destination.component.NewUpdateDestination;
import org.karnak.frontend.forwardnode.edit.source.SourceView;
import org.karnak.frontend.forwardnode.edit.source.component.NewUpdateSourceNode;
import org.karnak.frontend.util.UIS;

/** Layout of the edit forward node */
@SuppressWarnings("serial")
public class LayoutEditForwardNode extends VerticalLayout {

  // Services
  private final transient ProjectService projectService;
  private final SOPClassUIDService sopClassUIDService;

  // UI components
  private final Binder<ForwardNodeEntity> binderForwardNode;
  private final EditAETitleDescription editAETitleDescription;
  private final TabSourcesDestination tabSourcesDestination;
  private final VerticalLayout layoutDestinationsSources;
  private final DestinationView destinationView;
  private final SourceView sourceView;
  private final NewUpdateDestination newUpdateDestination;
  private final ButtonSaveDeleteCancel buttonForwardNodeSaveDeleteCancel;
  private final NewUpdateSourceNode newUpdateSourceNode;
  private ForwardNodeEntity currentForwardNodeEntity;
  private final ForwardNodeLogic forwardNodeLogic;

  /**
   * Autowired constructor
   *
   * @param forwardNodeLogic
   */
  public LayoutEditForwardNode(final ForwardNodeLogic forwardNodeLogic) {
    this.forwardNodeLogic = forwardNodeLogic;
    this.projectService = forwardNodeLogic.getProjectService();
    this.sopClassUIDService = forwardNodeLogic.getSopClassUIDService();
    this.currentForwardNodeEntity = null;
    this.binderForwardNode = new BeanValidationBinder<>(ForwardNodeEntity.class);
    this.tabSourcesDestination = new TabSourcesDestination();
    this.layoutDestinationsSources = new VerticalLayout();
    this.buttonForwardNodeSaveDeleteCancel = new ButtonSaveDeleteCancel();
    this.newUpdateDestination = new NewUpdateDestination();
    this.newUpdateSourceNode = new NewUpdateSourceNode();
    this.sourceView = new SourceView(forwardNodeLogic);
    this.destinationView = new DestinationView(forwardNodeLogic);
    this.editAETitleDescription = new EditAETitleDescription(binderForwardNode);

    // Init components
    initComponents();

    // Build layout
    buildLayout();

    // Events
    addEvents();

    // Binder
    addBinders();
  }

  /** Build layout */
  private void buildLayout() {
    layoutDestinationsSources.setSizeFull();
    getStyle().set("overflow-y", "auto");
    setSizeFull();
    setEditView();
    setLayoutDestinationsSources(tabSourcesDestination.getSelectedTab().getLabel());
  }

  /** Add binders on components */
  private void addBinders() {
    addBindersFilterBySOPClassesForm(
        newUpdateDestination.getFormDICOM().getFilterBySOPClassesForm());
    addBindersFilterBySOPClassesForm(
        newUpdateDestination.getFormSTOW().getFilterBySOPClassesForm());

    addBinderExtidListBox(newUpdateDestination.getFormSTOW().getLayoutDesidentification());
    addBinderExtidListBox(newUpdateDestination.getFormDICOM().getLayoutDesidentification());
    addBinderExtidInDicomTag(newUpdateDestination.getFormSTOW().getLayoutDesidentification());
    addBinderExtidInDicomTag(newUpdateDestination.getFormDICOM().getLayoutDesidentification());
  }

  /** Add events on components */
  private void addEvents() {
    addEventButtonSaveNewUpdateSourceNode();
    addEventButtonDeleteNewUpdateSourceNode();
    addEventButtonSaveNewUpdateDestination();
    addEventButtonDeleteNewUpdateDestination();
    addEventCheckboxLayoutDesidentification(
        newUpdateDestination.getFormDICOM().getLayoutDesidentification());
    addEventCheckboxLayoutDesidentification(
        newUpdateDestination.getFormSTOW().getLayoutDesidentification());
    setEventChangeTabValue();
    setEventBinderForwardNode();
    setEventDestination();
    setEventDestinationsViewDICOM();
    setEventDestinationsViewSTOW();
    setEventDestinationCancelButton();
    setEventNewSourceNode();
    setEventGridSourceNode();
    setEventSourceNodeCancelButton();
  }

  private void initComponents() {
    // FormDicom
    newUpdateDestination
        .getFormDICOM()
        .getLayoutDesidentification()
        .getProjectDropDown()
        .setItems(projectService.getAllProjects());
    newUpdateDestination
        .getFormDICOM()
        .getFilterBySOPClassesForm()
        .getSopFilter()
        .setItems(sopClassUIDService.getAllSOPClassUIDsName());

    // FormStow
    newUpdateDestination
        .getFormSTOW()
        .getLayoutDesidentification()
        .getProjectDropDown()
        .setItems(projectService.getAllProjects());
    newUpdateDestination
        .getFormSTOW()
        .getFilterBySOPClassesForm()
        .getSopFilter()
        .setItems(sopClassUIDService.getAllSOPClassUIDsName());
  }

  public void setEditView() {
    removeAll();
    add(
        UIS.setWidthFull(this.editAETitleDescription),
        UIS.setWidthFull(this.tabSourcesDestination),
        UIS.setWidthFull(this.layoutDestinationsSources),
        UIS.setWidthFull(this.buttonForwardNodeSaveDeleteCancel));
  }

  public void load(ForwardNodeEntity forwardNodeEntity) {
    this.currentForwardNodeEntity = forwardNodeEntity;
    this.editAETitleDescription.setForwardNode(forwardNodeEntity);

    destinationView.loadForwardNode(forwardNodeEntity);
    sourceView.loadForwardNode(forwardNodeEntity);

    setEditView();
    if (forwardNodeEntity == null) {
      this.tabSourcesDestination.setEnabled(false);
      this.buttonForwardNodeSaveDeleteCancel.setEnabled(false);
    } else {
      this.tabSourcesDestination.setEnabled(true);
      this.buttonForwardNodeSaveDeleteCancel.setEnabled(true);
    }
  }

  private void setEventChangeTabValue() {
    this.tabSourcesDestination.addSelectedChangeListener(
        event -> {
          Tab selectedTab = event.getSource().getSelectedTab();
          setLayoutDestinationsSources(selectedTab.getLabel());
        });
  }

  private void setLayoutDestinationsSources(String currentTab) {
    this.layoutDestinationsSources.removeAll();
    if (currentTab.equals(this.tabSourcesDestination.LABEL_SOURCES)) {
      this.layoutDestinationsSources.add(this.sourceView);
    } else if (currentTab.equals(this.tabSourcesDestination.LABEL_DESTINATIONS)) {
      this.layoutDestinationsSources.add(this.destinationView);
    }
  }

  private void setEventDestinationsViewDICOM() {
    this.destinationView
        .getNewDestinationDICOM()
        .addClickListener(
            event -> {
              this.newUpdateDestination.load(null, DestinationType.dicom);
              addFormView(this.newUpdateDestination);
            });
  }

  private void setEventDestinationsViewSTOW() {
    this.destinationView
        .getNewDestinationSTOW()
        .addClickListener(
            event -> {
              this.newUpdateDestination.load(null, DestinationType.stow);
              addFormView(this.newUpdateDestination);
            });
  }

  private void setEventNewSourceNode() {
    this.sourceView
        .getNewSourceNode()
        .addClickListener(
            event -> {
              this.newUpdateSourceNode.load(null);
              addFormView(this.newUpdateSourceNode);
            });
  }

  private void addFormView(Component form) {
    removeAll();
    add(form);
  }

  private void setEventBinderForwardNode() {
    this.binderForwardNode.addStatusChangeListener(
        event -> {
          boolean isValid = !event.hasValidationErrors();
          boolean hasChanges = this.binderForwardNode.hasChanges();
          this.buttonForwardNodeSaveDeleteCancel.getSave().setEnabled(hasChanges && isValid);
        });
  }

  private void setEventDestination() {
    this.destinationView
        .getGridDestination()
        .addItemClickListener(
            event -> {
              DestinationEntity destinationEntity = event.getItem();
              this.newUpdateDestination.load(
                  destinationEntity, destinationEntity.getDestinationType());
              addFormView(this.newUpdateDestination);
            });
  }

  private void setEventDestinationCancelButton() {
    this.newUpdateDestination.getButtonDICOMCancel().addClickListener(event -> setEditView());
    this.newUpdateDestination.getButtonSTOWCancel().addClickListener(event -> setEditView());
  }

  private void setEventGridSourceNode() {
    this.sourceView
        .getGridSourceNode()
        .addItemClickListener(
            event -> {
              DicomSourceNodeEntity dicomSourceNodeEntity = event.getItem();
              this.newUpdateSourceNode.load(dicomSourceNodeEntity);
              addFormView(this.newUpdateSourceNode);
            });
  }

  private void setEventSourceNodeCancelButton() {
    this.newUpdateSourceNode.getButtonCancel().addClickListener(event -> setEditView());
  }

  public void updateForwardNodeInEditView() {
    this.load(currentForwardNodeEntity);
  }

  public ButtonSaveDeleteCancel getButtonForwardNodeSaveDeleteCancel() {
    return buttonForwardNodeSaveDeleteCancel;
  }

  public ForwardNodeEntity getCurrentForwardNodeEntity() {
    return currentForwardNodeEntity;
  }

  public void setCurrentForwardNodeEntity(ForwardNodeEntity currentForwardNodeEntity) {
    this.currentForwardNodeEntity = currentForwardNodeEntity;
  }

  public Binder<ForwardNodeEntity> getBinderForwardNode() {
    return binderForwardNode;
  }

  private void addEventButtonSaveNewUpdateSourceNode() {
    newUpdateSourceNode
        .getButtonSaveDeleteCancel()
        .getSave()
        .addClickListener(
            event -> {
              NodeEventType nodeEventType =
                  newUpdateSourceNode.getCurrentSourceNode().getId() == null
                      ? NodeEventType.ADD
                      : NodeEventType.UPDATE;
              if (newUpdateSourceNode
                  .getBinderFormSourceNode()
                  .writeBeanIfValid(newUpdateSourceNode.getCurrentSourceNode())) {
                sourceView
                    .getSourceLogic()
                    .saveSourceNode(newUpdateSourceNode.getCurrentSourceNode());
                load(getCurrentForwardNodeEntity());
                sourceView
                    .getSourceLogic()
                    .publishEvent(
                        new NodeEvent(newUpdateSourceNode.getCurrentSourceNode(), nodeEventType));
              }
            });
  }

  private void addEventButtonDeleteNewUpdateSourceNode() {
    newUpdateSourceNode
        .getButtonSaveDeleteCancel()
        .getDelete()
        .addClickListener(
            event -> {
              if (newUpdateSourceNode.getCurrentSourceNode() != null) {
                ConfirmDialog dialog =
                    new ConfirmDialog(
                        "Are you sure to delete the DICOM source node "
                            + newUpdateSourceNode.getCurrentSourceNode().getAeTitle()
                            + "?");
                dialog.addConfirmationListener(
                    componentEvent -> {
                      NodeEvent nodeEvent =
                          new NodeEvent(
                              newUpdateSourceNode.getCurrentSourceNode(), NodeEventType.REMOVE);
                      sourceView
                          .getSourceLogic()
                          .deleteSourceNode(newUpdateSourceNode.getCurrentSourceNode());
                      load(getCurrentForwardNodeEntity());
                      sourceView.getSourceLogic().publishEvent(nodeEvent);
                    });
                dialog.open();
              }
            });
  }

  private void addEventButtonSaveNewUpdateDestination() {
    newUpdateDestination
        .getButtonDestinationDICOMSaveDeleteCancel()
        .getSave()
        .addClickListener(
            event -> {
              if (newUpdateDestination.getCurrentDestinationEntity().getDestinationType()
                      == DestinationType.dicom
                  && newUpdateDestination
                      .getBinderFormDICOM()
                      .writeBeanIfValid(newUpdateDestination.getCurrentDestinationEntity())) {
                NodeEventType nodeEventType =
                    newUpdateDestination.getCurrentDestinationEntity().getId() == null
                        ? NodeEventType.ADD
                        : NodeEventType.UPDATE;
                saveCurrentDestination(nodeEventType);
              }
            });

    newUpdateDestination
        .getButtonDestinationSTOWSaveDeleteCancel()
        .getSave()
        .addClickListener(
            event -> {
              if (newUpdateDestination.getCurrentDestinationEntity().getDestinationType()
                      == DestinationType.stow
                  && newUpdateDestination
                      .getBinderFormSTOW()
                      .writeBeanIfValid(newUpdateDestination.getCurrentDestinationEntity())) {
                NodeEventType nodeEventType =
                    newUpdateDestination.getCurrentDestinationEntity().getId() == null
                        ? NodeEventType.ADD
                        : NodeEventType.UPDATE;
                saveCurrentDestination(nodeEventType);
              }
            });
  }

  private void saveCurrentDestination(NodeEventType nodeEventType) {
    destinationView
        .getDestinationLogic()
        .saveDestination(newUpdateDestination.getCurrentDestinationEntity());
    load(getCurrentForwardNodeEntity());
    destinationView
        .getDestinationLogic()
        .publishEvent(
            new NodeEvent(newUpdateDestination.getCurrentDestinationEntity(), nodeEventType));
  }

  private void addEventButtonDeleteNewUpdateDestination() {
    newUpdateDestination
        .getButtonDestinationDICOMSaveDeleteCancel()
        .getDelete()
        .addClickListener(event -> removeCurrentDestination());
    newUpdateDestination
        .getButtonDestinationSTOWSaveDeleteCancel()
        .getDelete()
        .addClickListener(event -> removeCurrentDestination());
  }

  private void removeCurrentDestination() {
    if (newUpdateDestination.getCurrentDestinationEntity() != null) {
      ConfirmDialog dialog =
          new ConfirmDialog(
              "Are you sure to delete the forward node "
                  + newUpdateDestination.getCurrentDestinationEntity().getDescription()
                  + " ["
                  + newUpdateDestination.getCurrentDestinationEntity().getDestinationType()
                  + "] ?");
      dialog.addConfirmationListener(
          componentEvent -> {
            NodeEvent nodeEvent =
                new NodeEvent(
                    newUpdateDestination.getCurrentDestinationEntity(), NodeEventType.REMOVE);
            destinationView
                .getDestinationLogic()
                .deleteDestination(newUpdateDestination.getCurrentDestinationEntity());
            destinationView.getDestinationLogic().publishEvent(nodeEvent);
            load(getCurrentForwardNodeEntity());
          });
      dialog.open();
    }
  }

  private void addEventCheckboxLayoutDesidentification(
      LayoutDesidentification layoutDesidentification) {
    layoutDesidentification
        .getCheckboxDesidentification()
        .addValueChangeListener(
            event -> {
              if (event.getValue() != null) {
                if (event.getValue()) {
                  if (projectService.getAllProjects().size() > 0) {
                    layoutDesidentification
                        .getDiv()
                        .add(
                            layoutDesidentification.getLabelDisclaimer(),
                            layoutDesidentification.getProjectDropDown(),
                            layoutDesidentification.getDesidentificationName(),
                            layoutDesidentification.getDivExtID(),
                            layoutDesidentification.getIssuerOfPatientIDByDefault());
                    layoutDesidentification.setTextOnSelectionProject(
                        layoutDesidentification.getProjectDropDown().getValue());
                  } else {
                    layoutDesidentification.getWarningNoProjectsDefined().open();
                  }
                } else {
                  layoutDesidentification
                      .getDiv()
                      .remove(
                          layoutDesidentification.getLabelDisclaimer(),
                          layoutDesidentification.getProjectDropDown(),
                          layoutDesidentification.getDesidentificationName(),
                          layoutDesidentification.getIssuerOfPatientIDByDefault(),
                          layoutDesidentification.getDivExtID(),
                          layoutDesidentification.getExtidPresentInDicomTagView());
                  layoutDesidentification.getExtidPresentInDicomTagView().clear();
                  layoutDesidentification.getExtidListBox().setValue(null);
                }
              }
            });
  }

  private void addBindersFilterBySOPClassesForm(FilterBySOPClassesForm filterBySOPClassesForm) {
    filterBySOPClassesForm
        .getBinder()
        .forField(filterBySOPClassesForm.getSopFilter())
        .withValidator(
            listOfSOPFilter ->
                !listOfSOPFilter.isEmpty()
                    || !filterBySOPClassesForm.getFilterBySOPClassesCheckbox().getValue(),
            "No filter are applied\n")
        .bind(
            DestinationEntity::retrieveSOPClassUIDFiltersName,
            (destination, sopClassNames) -> {
              Set<SOPClassUIDEntity> newSOPClassUIDEntities = new HashSet<>();
              sopClassNames.forEach(
                  sopClasseName -> {
                    SOPClassUIDEntity sopClassUIDEntity =
                        sopClassUIDService.getByName(sopClasseName);
                    newSOPClassUIDEntities.add(sopClassUIDEntity);
                  });
              destination.setSOPClassUIDEntityFilters(newSOPClassUIDEntities);
            });

    filterBySOPClassesForm
        .getBinder()
        .forField(filterBySOPClassesForm.getFilterBySOPClassesCheckbox()) //
        .bind(DestinationEntity::isFilterBySOPClasses, DestinationEntity::setFilterBySOPClasses);
  }

  public void addBinderExtidListBox(LayoutDesidentification layoutDesidentification) {
    layoutDesidentification
        .getDestinationBinder()
        .forField(layoutDesidentification.getExtidListBox())
        .withValidator(
            type ->
                type != null
                    || (type == null
                        && !layoutDesidentification.getCheckboxDesidentification().getValue()),
            "Choose pseudonym type\n")
        .bind(
            destination -> {
              if (destination.getExternalIDProviderEntity() != null) {
                final ExternalIDProviderType externalIDProviderType =
                    destination.getExternalIDProviderEntity().getExternalIDProviderType();
                if (externalIDProviderType.equals(
                    ExternalIDProviderType.EXTID_PROVIDER_IMPLEMENTATION)) {
                  final String jarName = destination.getExternalIDProviderEntity().getJarName();
                  final ExternalIDProvider externalIDProvider =
                      layoutDesidentification.getExternalIDProviderImplMap().get(jarName);
                  return externalIDProvider.getDescription();
                } else {
                  return externalIDProviderType.getDescription();
                }
              } else {
                return null;
              }
            },
            (destination, descriptionSelected) -> {
              final ExternalIDProviderType externalIDProviderTypeSelected =
                  ExternalIDProviderUtil.getType(descriptionSelected);
              if (externalIDProviderTypeSelected != null) {
                final ExternalIDProviderEntity externalIDProviderEntity =
                    forwardNodeLogic
                        .getDestinationLogic()
                        .getExteralIDProviderEntity(externalIDProviderTypeSelected, null);
                destination.setExternalIDProviderEntity(externalIDProviderEntity);
              } else {
                final ExternalIDProviderEntity externalIDProviderEntity =
                    ExternalIDProviderUtil.getExternalIDProviderEntityWithDescription(
                        forwardNodeLogic.getDestinationLogic(),
                        layoutDesidentification,
                        descriptionSelected);
                if (externalIDProviderEntity != null) {
                  destination.setExternalIDProviderEntity(externalIDProviderEntity);
                } else {
                  destination.setExternalIDProviderEntity(null);
                }
              }
            });
  }

  public void addBinderExtidInDicomTag(LayoutDesidentification layoutDesidentification) {
    layoutDesidentification
        .getDestinationBinder()
        .forField(layoutDesidentification.getExtidPresentInDicomTagView().getTag())
        .withConverter(String::valueOf, value -> (value == null) ? "" : value, "Must be a tag")
        .withValidator(
            tag -> {
              if (!layoutDesidentification.getCheckboxDesidentification().getValue()
                  || (layoutDesidentification.getExtidListBox().getValue() != null
                      && !layoutDesidentification
                          .getExtidListBox()
                          .getValue()
                          .equals(ExternalIDProviderType.EXTID_IN_TAG.getDescription()))) {
                return true;
              }
              final String cleanTag = tag.replaceAll("[(),]", "").toUpperCase();
              try {
                TagUtils.intFromHexString(cleanTag);
              } catch (Exception e) {
                return false;
              }
              return (tag != null && !tag.equals("") && cleanTag.length() == 8);
            },
            "Choose a valid tag\n")
        .bind(DestinationEntity::getTag, DestinationEntity::setTag);

    layoutDesidentification
        .getDestinationBinder()
        .forField(layoutDesidentification.getExtidPresentInDicomTagView().getDelimiter())
        .withConverter(
            String::valueOf, value -> (value == null) ? "" : value, "Must be a delimiter")
        .withValidator(
            delimiter -> {
              if (!layoutDesidentification.getCheckboxDesidentification().getValue()
                  || (layoutDesidentification.getExtidListBox().getValue() != null
                      && !layoutDesidentification
                          .getExtidListBox()
                          .getValue()
                          .equals(ExternalIDProviderType.EXTID_IN_TAG.getDescription()))) {
                return true;
              }
              if (layoutDesidentification.getExtidPresentInDicomTagView().getPosition().getValue()
                      != null
                  && layoutDesidentification
                          .getExtidPresentInDicomTagView()
                          .getPosition()
                          .getValue()
                      > 0) {
                return delimiter != null && !delimiter.equals("");
              }
              return true;
            },
            "A delimiter must be defined, when a position is present")
        .bind(DestinationEntity::getDelimiter, DestinationEntity::setDelimiter);

    layoutDesidentification
        .getDestinationBinder()
        .forField(layoutDesidentification.getExtidPresentInDicomTagView().getPosition())
        .withConverter(new DoubleToIntegerConverter())
        .withValidator(
            position -> {
              if (!layoutDesidentification.getCheckboxDesidentification().getValue()
                  || (layoutDesidentification.getExtidListBox().getValue() != null
                      && !layoutDesidentification
                          .getExtidListBox()
                          .getValue()
                          .equals(ExternalIDProviderType.EXTID_IN_TAG.getDescription()))) {
                return true;
              }
              if (layoutDesidentification.getExtidPresentInDicomTagView().getDelimiter().getValue()
                      != null
                  && !layoutDesidentification
                      .getExtidPresentInDicomTagView()
                      .getDelimiter()
                      .getValue()
                      .equals("")) {
                return position != null && position >= 0;
              }
              return true;
            },
            "A position must be defined, when a delimiter is present")
        .bind(DestinationEntity::getPosition, DestinationEntity::setPosition);

    layoutDesidentification
        .getDestinationBinder()
        .forField(layoutDesidentification.getExtidPresentInDicomTagView().getSavePseudonym())
        .bind(DestinationEntity::getSavePseudonym, DestinationEntity::setSavePseudonym);
  }
}
