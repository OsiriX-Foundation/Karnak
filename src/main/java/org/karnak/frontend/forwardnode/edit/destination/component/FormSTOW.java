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

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.frontend.forwardnode.edit.component.ButtonSaveDeleteCancel;
import org.karnak.frontend.forwardnode.edit.destination.DestinationLogic;
import org.karnak.frontend.kheops.SwitchingAlbumsView;
import org.karnak.frontend.util.UIS;

public class FormSTOW extends VerticalLayout {

  private final LayoutDesidentification layoutDesidentification;
  private Binder<DestinationEntity> binder;
  private TextField description;
  private TextField url;
  private TextField urlCredentials;
  private TextArea headers;

  private final FilterBySOPClassesForm filterBySOPClassesForm;
  private SwitchingAlbumsView switchingAlbumsView;
  private Checkbox activate;
  private final DestinationCondition destinationCondition;
  private final NotificationComponent notificationComponent;
  private final TransferSyntaxComponent transferSyntaxComponent;
  private final TranscodeOnlyUncompressedComponent transcodeOnlyUncompressedComponent;
  private DestinationLogic destinationLogic;

  public FormSTOW() {
    this.layoutDesidentification = new LayoutDesidentification();
    this.filterBySOPClassesForm = new FilterBySOPClassesForm();
    this.destinationCondition = new DestinationCondition();
    this.notificationComponent = new NotificationComponent();
    this.transferSyntaxComponent = new TransferSyntaxComponent();
    this.transcodeOnlyUncompressedComponent = new TranscodeOnlyUncompressedComponent();
  }

  public void init(
      Binder<DestinationEntity> binder, ButtonSaveDeleteCancel buttonSaveDeleteCancel) {
    setSizeFull();
    this.binder = binder;
    this.layoutDesidentification.init(this.binder);
    this.filterBySOPClassesForm.init(this.binder);
    this.destinationCondition.init(binder);
    notificationComponent.init(binder);
    transferSyntaxComponent.init(this.binder);
    transcodeOnlyUncompressedComponent.init(this.binder);

    this.description = new TextField("Description");
    this.url = new TextField("URL");
    this.urlCredentials = new TextField("URL credentials");
    this.headers = new TextArea("Headers");
    this.switchingAlbumsView = new SwitchingAlbumsView();
    this.activate = new Checkbox("Enable destination");

    // Define layout
    VerticalLayout destinationLayout =
        new VerticalLayout(
            UIS.setWidthFull(new HorizontalLayout(description)),
            destinationCondition,
            UIS.setWidthFull(new HorizontalLayout(url, urlCredentials)),
            UIS.setWidthFull(headers));
    VerticalLayout transferLayout =
        new VerticalLayout(
            new HorizontalLayout(transferSyntaxComponent, transcodeOnlyUncompressedComponent));
    VerticalLayout activateLayout = new VerticalLayout(activate);
    VerticalLayout switchingLayout = new VerticalLayout(switchingAlbumsView);

    // Set padding
    transferLayout.setPadding(true);
    destinationLayout.setPadding(true);
    activateLayout.setPadding(true);
    activateLayout.setPadding(true);

    // Add components
    add(UIS.setWidthFull(new BoxShadowComponent(UIS.setWidthFull(destinationLayout))));
    add(UIS.setWidthFull(new BoxShadowComponent(UIS.setWidthFull(transferLayout))));
    add(UIS.setWidthFull(new BoxShadowComponent(UIS.setWidthFull(notificationComponent))));
    add(UIS.setWidthFull(new BoxShadowComponent(UIS.setWidthFull(layoutDesidentification))));
    add(UIS.setWidthFull(new BoxShadowComponent(UIS.setWidthFull(filterBySOPClassesForm))));
    add(UIS.setWidthFull(new BoxShadowComponent(UIS.setWidthFull(switchingLayout))));
    add(UIS.setWidthFull(new BoxShadowComponent(UIS.setWidthFull(activateLayout))));
    add(UIS.setWidthFull(buttonSaveDeleteCancel));

    setElements();
    setBinder();
  }

  private void setElements() {
    description.setWidth("100%");

    url.setWidth("50%");
    UIS.setTooltip(url, "The destination STOW-RS URL");

    urlCredentials.setWidth("50%");
    UIS.setTooltip(
        urlCredentials, "Credentials of the STOW-RS service (format is \"user:password\")");

    headers.setMinHeight("10em");
    headers.setWidth("100%");
    UIS.setTooltip(
        headers,
        "Headers for HTTP request. Example of format:\n<key>Authorization</key>\n<value>Bearer 1v1pwxT4Ww4DCFzyaMt0NP</value>");
  }

  private void setBinder() {
    binder
        .forField(url)
        .withValidator(StringUtils::isNotBlank, "URL is mandatory")
        .bind(DestinationEntity::getUrl, DestinationEntity::setUrl);

    binder
        .forField(switchingAlbumsView)
        .bind(DestinationEntity::getKheopsAlbumEntities, DestinationEntity::setKheopsAlbumEntities);

    binder.bindInstanceFields(this);
  }

  public LayoutDesidentification getLayoutDesidentification() {
    return layoutDesidentification;
  }

  public FilterBySOPClassesForm getFilterBySOPClassesForm() {
    return filterBySOPClassesForm;
  }

  public void setDestinationLogic(DestinationLogic destinationLogic) {
    this.destinationLogic = destinationLogic;
  }
}
