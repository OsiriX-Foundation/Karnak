package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationType;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.api.ForwardNodeAPI;
import org.karnak.ui.component.ConfirmDialog;
import org.karnak.ui.util.UIS;

public class LayoutEditForwardNode extends VerticalLayout {
    private ForwardNodeViewLogic forwardNodeViewLogic;
    private ForwardNodeAPI forwardNodeAPI;
    private ForwardNode currentForwardNode;

    private EditAETitleDescription editAETitleDescription;
    private TabSourcesDestination tabSourcesDestination;
    private VerticalLayout layoutDestinationsSources;
    private DestinationsView destinationsView;
    private NewUpdateDestination newUpdateDestination;
    private ButtonSaveDeleteCancel buttonForwardNodeSaveDeleteCancel;

    public LayoutEditForwardNode(ForwardNodeViewLogic forwardNodeViewLogic, ForwardNodeAPI forwardNodeAPI) {
        this.forwardNodeViewLogic = forwardNodeViewLogic;
        this.forwardNodeAPI = forwardNodeAPI;
        this.currentForwardNode = null;

        setSizeFull();
        editAETitleDescription = new EditAETitleDescription();
        tabSourcesDestination = new TabSourcesDestination();
        layoutDestinationsSources = new VerticalLayout();
        layoutDestinationsSources.setSizeFull();
        destinationsView = new DestinationsView(forwardNodeAPI.getDataProvider().getDataService());
        buttonForwardNodeSaveDeleteCancel = new ButtonSaveDeleteCancel();

        newUpdateDestination = new NewUpdateDestination();
        setEditView();

        setLayoutDestinationsSources(tabSourcesDestination.getSelectedTab().getLabel());
        setEventChangeTabValue();
        setEventCancelButton();
        setEventDeleteButton();
        setEventDestination();

        setEventDestinationsViewDICOM();
        setEventDestinationsViewSTOW();
        setEventDestinationCancelButton();
    }

    private void setEditView() {
        removeAll();
        add(UIS.setWidthFull(editAETitleDescription),
                UIS.setWidthFull(tabSourcesDestination),
                UIS.setWidthFull(layoutDestinationsSources),
                UIS.setWidthFull(buttonForwardNodeSaveDeleteCancel));
    }

    public void load(ForwardNode forwardNode) {
        currentForwardNode = forwardNode;
        editAETitleDescription.setForwardNode(forwardNode);
        destinationsView.setForwardNode(forwardNode);
        setEditView();
        if (forwardNode == null) {
            tabSourcesDestination.setEnabled(false);
            buttonForwardNodeSaveDeleteCancel.setEnabled(false);
        } else {
            tabSourcesDestination.setEnabled(true);
            buttonForwardNodeSaveDeleteCancel.setEnabled(true);
        }
    }

    private void setEventChangeTabValue() {
        tabSourcesDestination.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSource().getSelectedTab();
            setLayoutDestinationsSources(selectedTab.getLabel());
        });
    }

    private void setLayoutDestinationsSources(String currentTab) {
        layoutDestinationsSources.removeAll();
        if (currentTab.equals(tabSourcesDestination.LABEL_SOURCES)) {
            System.out.println("SOURCE");
        } else if (currentTab.equals(tabSourcesDestination.LABEL_DESTINATIONS)) {
            layoutDestinationsSources.add(destinationsView);
        }
    }

    private void setEventDestinationsViewDICOM() {
        destinationsView.getNewDestinationDICOM().addClickListener(event -> {
            newUpdateDestination.load(null, DestinationType.dicom);
            addFormView(newUpdateDestination);
        });
    }

    private void setEventDestinationsViewSTOW() {
        destinationsView.getNewDestinationSTOW().addClickListener(event -> {
            newUpdateDestination.load(null, DestinationType.stow);
            addFormView(newUpdateDestination);
        });
    }

    private void addFormView(Component form) {
        removeAll();
        add(form);
    }

    private void setEventCancelButton() {
        buttonForwardNodeSaveDeleteCancel.getCancel().addClickListener(event -> {
            forwardNodeViewLogic.cancelForwardNode();
        });
    }

    private void setEventDeleteButton() {
        buttonForwardNodeSaveDeleteCancel.getDelete().addClickListener(event -> {
            if (currentForwardNode != null) {
                ConfirmDialog dialog = new ConfirmDialog(
                        "Are you sure to delete the forward node " + currentForwardNode.getFwdAeTitle() + " ?");
                dialog.addConfirmationListener(componentEvent -> {
                    forwardNodeAPI.deleteForwardNode(currentForwardNode);
                    forwardNodeViewLogic.cancelForwardNode();
                });
                dialog.open();
            }
        });
    }

    private void setEventSaveButton() {
        buttonForwardNodeSaveDeleteCancel.getSave().addClickListener(event -> {
        });
    }

    private void setEventDestination() {
        destinationsView.getGridDestination().addItemClickListener(event -> {
            Destination destination = event.getItem();
            newUpdateDestination.load(destination, destination.getType());
            addFormView(newUpdateDestination);
        });
    }

    private void setEventDestinationCancelButton() {
        newUpdateDestination.getButtonCancel().addClickListener(event -> {
            setEditView();
        });
    }
}
