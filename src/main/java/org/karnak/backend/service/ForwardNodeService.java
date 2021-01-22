package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.repo.ForwardNodeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("serial")
@Service
public class ForwardNodeService extends ListDataProvider<ForwardNodeEntity> {

  // Repositories
  private final ForwardNodeRepo forwardNodeRepo;

  /**
   * Text filter that can be changed separately.
   */
  private String filterText = "";

  @Autowired
  public ForwardNodeService(final ForwardNodeRepo forwardNodeRepo) {
    super(new ArrayList<>());
    this.forwardNodeRepo = forwardNodeRepo;
    getItems().addAll(getAllForwardNodes());
  }

  @Override
  public void refreshAll() {
    getItems().clear();
    getItems().addAll(getAllForwardNodes());
    super.refreshAll();
  }

  @Override
  public Long getId(ForwardNodeEntity data) {
    Objects.requireNonNull(data, "Cannot provide an id for a null item.");
    return data.getId();
  }

  /**
   * Retrieves the ForwardNode according to its ID.
   *
   * @param dataId the data ID.
   * @return the ForwardNodeEntity according to its ID; null if not found.
   */
  public ForwardNodeEntity get(Long dataId) {
    return forwardNodeRepo.findById(dataId).orElse(null);
  }

  /**
   * Store given ForwardNode.
   *
   * @param forwardNodeEntity the updated or new forwardNodeEntity
   */
  public void save(ForwardNodeEntity forwardNodeEntity) {
    forwardNodeRepo.saveAndFlush(forwardNodeEntity);
    refreshItem(forwardNodeEntity);
    refreshAll();
  }

  /**
   * Delete given data from the backing data service.
   *
   * @param data the data to be deleted
   */
  public void delete(ForwardNodeEntity data) {
    forwardNodeRepo.deleteById(data.getId());
    forwardNodeRepo.flush();
    refreshAll();
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

    final String filterText = filterTextInput.trim();

    if (Objects.equals(this.filterText, filterText)) {
      return;
    }
    this.filterText = filterText;

    setFilter(data -> matchesFilter(data, filterText));
  }

  private boolean matchesFilter(ForwardNodeEntity data, String filterText) {
    return data != null && data.matchesFilter(filterText);
  }

  public List<ForwardNodeEntity> getAllForwardNodes() {
    return forwardNodeRepo.findAll();
  }

  public Collection<DestinationEntity> getAllDestinations(ForwardNodeEntity forwardNodeEntity) {
    if (forwardNodeEntity != null) {
      return forwardNodeEntity.getDestinationEntities();
    }
    return new HashSet<>();
  }

  public DestinationEntity getDestinationById(ForwardNodeEntity forwardNodeEntity, Long dataId) {
    Collection<DestinationEntity> destinationEntities = getAllDestinations(forwardNodeEntity);
    for (DestinationEntity destinationEntity : destinationEntities) {
      if (Objects.equals(destinationEntity.getId(), dataId)) {
        return destinationEntity;
      }
    }
    return null;
  }

  public DestinationEntity updateDestination(
      ForwardNodeEntity forwardNodeEntity, DestinationEntity data) {
    if (forwardNodeEntity == null || data == null) {
      return null;
    }
    Collection<DestinationEntity> destinationEntities = getAllDestinations(forwardNodeEntity);
    if (!destinationEntities.contains(data)) {
      forwardNodeEntity.addDestination(data);
    }
    return data;
  }

  public void deleteDestination(ForwardNodeEntity forwardNodeEntity, DestinationEntity data) {
    if (forwardNodeEntity == null || data == null) {
      return;
    }
    forwardNodeEntity.removeDestination(data);
  }

  public Collection<DicomSourceNodeEntity> getAllSourceNodes(ForwardNodeEntity forwardNodeEntity) {
    if (forwardNodeEntity != null) {
      return forwardNodeEntity.getSourceNodes();
    }
    return new HashSet<>();
  }

  public DicomSourceNodeEntity getSourceNodeById(ForwardNodeEntity forwardNodeEntity, Long dataId) {
    Collection<DicomSourceNodeEntity> sourceNodes = getAllSourceNodes(forwardNodeEntity);
    for (DicomSourceNodeEntity sourceNode : sourceNodes) {
      if (Objects.equals(sourceNode.getId(), dataId)) {
        return sourceNode;
      }
    }
    return null;
  }

  public DicomSourceNodeEntity updateSourceNode(
      ForwardNodeEntity forwardNodeEntity, DicomSourceNodeEntity data) {
    if (forwardNodeEntity == null || data == null) {
      return null;
    }
    Collection<DicomSourceNodeEntity> sourceNodes = getAllSourceNodes(forwardNodeEntity);
    if (!sourceNodes.contains(data)) {
      forwardNodeEntity.addSourceNode(data);
    }
    return data;
  }

  public void deleteSourceNode(ForwardNodeEntity forwardNodeEntity, DicomSourceNodeEntity data) {
    if (forwardNodeEntity == null || data == null) {
      return;
    }
    forwardNodeEntity.removeSourceNode(data);
  }
}
