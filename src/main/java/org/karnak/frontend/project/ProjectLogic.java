/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.project;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.data.entity.SecretEntity;
import org.karnak.backend.service.ProjectService;
import org.karnak.backend.service.SecretService;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.frontend.component.ConfirmDialog;
import org.karnak.frontend.project.component.EditProject;
import org.karnak.frontend.project.component.GridProject;
import org.karnak.frontend.project.component.NewProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Project logic service use to make calls to backend and implement logic linked to the project
 * view
 */
@Service
public class ProjectLogic extends ListDataProvider<ProjectEntity> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectLogic.class);

  // View
  private ProjectView projectView;

  // Services
  private final transient ProjectService projectService;

  private final transient ProfilePipeService profilePipeService;

  private final SecretService secretService;

  /**
   * Autowired constructor
   *
   * @param projectService     Project backend service
   * @param profilePipeService Profile Pipe Service
   */
  @Autowired
  public ProjectLogic(
      final ProjectService projectService,
      final ProfilePipeService profilePipeService,
      final SecretService secretService) {
    super(new ArrayList<>());
    this.projectService = projectService;
    this.profilePipeService = profilePipeService;
    this.secretService = secretService;
    initDataProvider();
  }

  @Override
  public void refreshAll() {
    getItems().clear();
    getItems().addAll(projectService.getAllProjects());
    super.refreshAll();
  }

  /**
   * Initialize the data provider
   */
  private void initDataProvider() {
    getItems().addAll(projectService.getAllProjects());
  }

  public Long enter(String dataIdStr) {
    try {
      return Long.valueOf(dataIdStr);
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot get valueOf {}", dataIdStr, e);
    }
    return null;
  }

  /**
   * Create a new project
   *
   * @param projectEntity Project to create
   * @param secretEntity  Secret associated to the new project
   */
  public void createProject(ProjectEntity projectEntity, SecretEntity secretEntity) {
    boolean isNewProject = projectEntity.getId() == null;
    if (isNewProject) {
      getItems().add(projectEntity);
    } else {
      refreshItem(projectEntity);
    }
    projectEntity = projectService.save(projectEntity);
    secretService.saveActiveSecret(secretEntity, projectEntity);
    refreshAll();
  }

  /**
   * Retrieve a project depending of its id
   *
   * @param projectID Id of the project to retrieve
   * @return Project found
   */
  public ProjectEntity retrieveProject(Long projectID) {
    refreshAll();
    return getItems().stream()
        .filter(project -> project.getId().equals(projectID))
        .findAny()
        .orElse(null);
  }

  /**
   * Add event on button update on the edit component
   *
   * @param editProject Edit project component
   * @param gridProject Link to the grid for element selection
   */
  public void addEditEventButtonUpdate(EditProject editProject, GridProject gridProject) {
    editProject
        .getButtonUpdate()
        .addClickListener(
            event -> {
              if (editProject.getProjectEntity() != null
                  && editProject.getBinder().writeBeanIfValid(editProject.getProjectEntity())) {
                if (editProject.getProjectEntity().getDestinationEntities() != null
                    && editProject.getProjectEntity().getDestinationEntities().size() > 0) {
                  ConfirmDialog dialog =
                      new ConfirmDialog(
                          String.format(
                              "The project %s is used, are you sure you want to updated ?",
                              editProject.getProjectEntity().getName()));
                  dialog.addConfirmationListener(
                      componentEvent -> {
                        projectService.update(editProject.getProjectEntity());
                        refreshAll();
                        gridProject.deselectAll();
                      });
                  dialog.open();
                } else {
                  projectService.update(editProject.getProjectEntity());
                  refreshAll();
                  gridProject.deselectAll();
                }
              }
            });
  }

  /**
   * Add event on button remove on the edit component
   *
   * @param editProject Edit project component
   */
  public void addEditEventButtonRemove(EditProject editProject) {
    editProject
        .getButtonRemove()
        .addClickListener(
            e -> {
              List<DestinationEntity> destinationEntities =
                  editProject.getProjectEntity().getDestinationEntities();
              if (destinationEntities != null && destinationEntities.size() > 0) {
                editProject.getDialogWarning().setText(editProject.getProjectEntity());
                editProject.getDialogWarning().open();

              } else {
                projectService.remove(editProject.getProjectEntity());
                refreshAll();
                editProject.clear();
                editProject.setEnabled(false);
              }
            });
  }

  /**
   * Init profile drop down for component Edit
   *
   * @param editProject Edit component
   */
  public void initEditProfileDropDown(EditProject editProject) {
    editProject.getProfileDropDown().setItems(profilePipeService.getAllProfiles());
    editProject
        .getProfileDropDown()
        .setItemLabelGenerator(
            profileEntity ->
                String.format(
                    "%s [version %s]", profileEntity.getName(), profileEntity.getVersion()));
  }

  /**
   * Init profile drop down for component New project
   *
   * @param newProject New project component
   */
  public void initNewProjectProfileDropDown(NewProject newProject) {
    newProject.getProfileDropDown().setItems(profilePipeService.getAllProfiles());
    newProject
        .getProfileDropDown()
        .setItemLabelGenerator(
            profileEntity ->
                String.format(
                    "%s [version %s]", profileEntity.getName(), profileEntity.getVersion()));
  }

  public ProjectView getProjectView() {
    return projectView;
  }

  public void setProjectView(ProjectView projectView) {
    this.projectView = projectView;
  }
}
