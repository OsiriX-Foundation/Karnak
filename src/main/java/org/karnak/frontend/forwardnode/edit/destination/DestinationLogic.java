/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.edit.destination;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.HashSet;
import java.util.Objects;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.service.DestinationService;
import org.karnak.backend.service.ExternalIDProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Logic service use to make calls to backend and implement logic linked to the view */
@Service
public class DestinationLogic extends ListDataProvider<DestinationEntity> {

  // View
  private DestinationView destinationView;

  // Services
  private final transient DestinationService destinationService;
  private final transient ExternalIDProviderService externalIDProviderService;

  /** Text filter that can be changed separately. */
  private String filterText = "";

  private ForwardNodeEntity forwardNodeEntity; // Current forward node

  /**
   * Autowired constructor
   *
   * @param destinationService Destination Service
   */
  @Autowired
  public DestinationLogic(
      final DestinationService destinationService,
      final ExternalIDProviderService externalIDProviderService) {
    super(new HashSet<>());
    this.destinationService = destinationService;
    this.externalIDProviderService = externalIDProviderService;
  }

  @Override
  public Object getId(DestinationEntity data) {
    Objects.requireNonNull(data, "Cannot provide an id for a null item.");
    return data.hashCode();
  }

  @Override
  public void refreshAll() {
    getItems().clear();
    if (forwardNodeEntity != null) {
      getItems().addAll(forwardNodeEntity.getDestinationEntities());
    }
    super.refreshAll();
  }

  /**
   * Sets the filter to use for this data provider and refreshes data.
   *
   * <p>Filter is compared for allowed properties.
   *
   * @param filterTextInput the text to filter by, never null.
   */
  public void setFilter(String filterTextInput) {
    Objects.requireNonNull(filterText, "Filter text cannot be null.");

    final String filterTextInputTrim = filterTextInput.trim();

    if (Objects.equals(this.filterText, filterTextInputTrim)) {
      return;
    }
    this.filterText = filterTextInputTrim;

    setFilter(data -> matchesFilter(data, filterTextInputTrim));
  }

  private boolean matchesFilter(DestinationEntity data, String filterText) {
    return data != null && data.matchesFilter(filterText);
  }

  public DestinationView getDestinationsView() {
    return destinationView;
  }

  public void setDestinationsView(DestinationView destinationView) {
    this.destinationView = destinationView;
  }

  /**
   * Load the forward node in the list of items
   *
   * @param forwardNodeEntity Forward node to load
   */
  public void loadForwardNode(ForwardNodeEntity forwardNodeEntity) {
    this.forwardNodeEntity = forwardNodeEntity;
    getItems().clear();
    getItems().addAll(destinationService.retrieveDestinations(this.forwardNodeEntity));
  }

  /**
   * Save the destination
   *
   * @param destinationEntity destination to save
   */
  public void saveDestination(DestinationEntity destinationEntity) {
    destinationService.save(forwardNodeEntity, destinationEntity);
    loadForwardNode(forwardNodeEntity);
  }

  public void publishEvent(NodeEvent nodeEvent) {
    destinationService.getApplicationEventPublisher().publishEvent(nodeEvent);
  }

  /**
   * Delete the destination in parameter
   *
   * @param destinationEntity destination to delete
   */
  public void deleteDestination(DestinationEntity destinationEntity) {
    destinationService.delete(destinationEntity);
    refreshAll();
  }

  public ExternalIDProviderEntity getExteralIDProviderEntity(
      ExternalIDProviderType externalIDProviderType, String jarName) {
    return externalIDProviderService.getExternalIDProvider(externalIDProviderType, jarName);
  }
}
