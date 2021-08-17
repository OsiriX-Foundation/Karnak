/*
 * Copyright (c) 2021 Karnak Team and other contributors.
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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import liquibase.util.csv.opencsv.CSVReader;
import org.karnak.backend.cache.ExternalIDCSVPatient;
import org.karnak.backend.data.entity.ProjectEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVDialog extends Dialog {

  private static final Logger LOGGER = LoggerFactory.getLogger(CSVDialog.class);
  private static final String EXTERNAL_PSEUDONYM = "External Pseudonym";
  private static final String PATIENT_ID = "Patient ID";
  private static final String PATIENT_FIRST_NAME = "Patient first name";
  private static final String PATIENT_LAST_NAME = "Patient last name";
  private static final String ISSUER_OF_PATIENT_ID = "Issuer of patient ID";
  private static final String TITLE =
      "Upload CSV that contains the correspondence table with the externals pseudonyms";
  private final String[] selectValues = {
    "", EXTERNAL_PSEUDONYM, PATIENT_ID, PATIENT_FIRST_NAME, PATIENT_LAST_NAME, ISSUER_OF_PATIENT_ID
  };
  private final List<ExternalIDCSVPatient> patientsList;
  private NumberField fromLineField;
  private Button readCSVButton;
  private Button cancelButton;
  private Div divGridContent;
  private Div errorMsg;
  private Div divTitle;
  private Grid<String[]> csvGrid;
  private List<Select<String>> listOfSelect;
  private List<String[]> allRows;
  private HashMap<String, Integer> selectValuesPositionHashMap;
  private final transient ProjectEntity projectEntity;

  public CSVDialog(InputStream inputStream, char separator, ProjectEntity projectEntity) {
    removeAll();

    setWidth("50%");

    this.projectEntity = projectEntity;
    patientsList = new ArrayList<>();
    allRows = null;
    try {
      CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream), separator);
      allRows = csvReader.readAll();
    } catch (IOException e) {
      LOGGER.error("Error while reading the CSV", e);
    }

    setElement();
    buildGrid();

    divGridContent.add(csvGrid);
    HorizontalLayout horizontalLayout = new HorizontalLayout(cancelButton, readCSVButton);
    horizontalLayout.setWidthFull();
    add(divTitle, fromLineField, divGridContent, errorMsg, horizontalLayout);
  }

  private void setElement() {
    divTitle = new Div();
    divTitle.setText(TITLE);
    divTitle
        .getStyle()
        .set("font-size", "large")
        .set("font-weight", "bolder")
        .set("padding-bottom", "10px");

    divGridContent = new Div();
    errorMsg = new Div();
    errorMsg
        .getStyle()
        .set("font-weight", "bolder")
        .set("padding-bottom", "10px")
        .set("color", "red");

    fromLineField = new NumberField("From line ");
    fromLineField.setValue(1d);
    fromLineField.setHasControls(true);
    fromLineField.setMin(1);
    fromLineField.setMax((double) allRows.size() + 1);

    readCSVButton =
        new Button(
            "Upload CSV",
            event -> {
              if (selectValuesPositionHashMap.get(EXTERNAL_PSEUDONYM).equals(-1)
                  || selectValuesPositionHashMap.get(PATIENT_ID).equals(-1)
                  || selectValuesPositionHashMap.get(PATIENT_FIRST_NAME).equals(-1)
                  || selectValuesPositionHashMap.get(PATIENT_LAST_NAME).equals(-1)) {
                generateErrorMsg();
              } else {
                readCSVPatients();
                close();
              }
            });
    readCSVButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    readCSVButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());

    cancelButton = new Button("Cancel", event -> close());
  }

  public void buildGrid() {
    csvGrid = new Grid<>();

    String[] headers = allRows.isEmpty() ? new String[0] : allRows.get(0);
    listOfSelect = new ArrayList<>();

    selectValuesPositionHashMap = new HashMap<>();

    for (String val : selectValues) {
      selectValuesPositionHashMap.put(val, -1);
    }

    for (int i = 0; i < headers.length; i++) {
      int idx = i;
      Select<String> currentSelect = new Select<>();
      currentSelect.setId(String.format("%d", i));
      currentSelect.setItems(selectValues);
      currentSelect.setSizeFull();
      currentSelect.addValueChangeListener(
          value -> {
            int currentPosition = Integer.parseInt(currentSelect.getId().orElse("-1"));
            // reset value of old key
            if (selectValuesPositionHashMap.containsValue(currentPosition)) {
              String valueInHashMap = getValueWithKey(selectValuesPositionHashMap, currentPosition);
              if (valueInHashMap != null) {
                selectValuesPositionHashMap.replace(valueInHashMap, -1);
              }
            }
            // update new key
            selectValuesPositionHashMap.replace(value.getValue(), currentPosition);
            updateSelectGrid();
          });
      listOfSelect.add(currentSelect);
      csvGrid.addColumn(lineArray -> lineArray[idx]).setHeader(currentSelect);
    }

    csvGrid.setItems(allRows);
    fromLineField.addValueChangeListener(
        numberValue -> {
          if (numberValue.getValue().intValue() > allRows.size()) {
            csvGrid.setItems(allRows.subList(allRows.size(), allRows.size()));
          } else {
            csvGrid.setItems(
                allRows.subList(numberValue.getValue().intValue() - 1, allRows.size()));
          }
        });
  }

  private void updateSelectGrid() {
    for (Select<String> currentSelect : listOfSelect) {
      int currentPosition = Integer.parseInt(currentSelect.getId().orElse("-1"));

      currentSelect.setItemEnabledProvider(
          item ->
              selectValuesPositionHashMap.get(item).equals(-1)
                  || selectValuesPositionHashMap.get(item).equals(currentPosition)
                  || item.equals(""));
    }
  }

  public <K, V> String getValueWithKey(Map<K, V> map, V value) {
    Stream<K> keyStream1 =
        map.entrySet().stream()
            .filter(entry -> value.equals(entry.getValue()))
            .map(Map.Entry::getKey);

    return (String) keyStream1.findFirst().orElse(null);
  }

  private void generateErrorMsg() {
    final Stream<String> streamFieldNotSelected =
        selectValuesPositionHashMap.entrySet().stream()
            .map(
                stringIntegerEntry -> {
                  if (stringIntegerEntry.getValue().equals(-1)
                      && !stringIntegerEntry.getKey().equals("")
                      && !stringIntegerEntry.getKey().equals(ISSUER_OF_PATIENT_ID)) {
                    return stringIntegerEntry.getKey();
                  } else {
                    return "";
                  }
                })
            .filter(s -> !s.equals(""));
    final String concatFieldNotSelected = streamFieldNotSelected.collect(Collectors.joining(", "));
    errorMsg.setText(String.format("These fields are not selected: %s", concatFieldNotSelected));
  }

  private void readCSVPatients() {
    try {
      // Read CSV line by line and use the string array as you want
      for (String[] row :
          allRows.subList(fromLineField.getValue().intValue() - 1, allRows.size())) {
        String issuerOfPatientID =
            selectValuesPositionHashMap.get(ISSUER_OF_PATIENT_ID).equals(-1)
                ? ""
                : row[selectValuesPositionHashMap.get(ISSUER_OF_PATIENT_ID)];
        final ExternalIDCSVPatient newPatient =
            new ExternalIDCSVPatient(
                row[selectValuesPositionHashMap.get(EXTERNAL_PSEUDONYM)],
                row[selectValuesPositionHashMap.get(PATIENT_ID)],
                row[selectValuesPositionHashMap.get(PATIENT_FIRST_NAME)],
                row[selectValuesPositionHashMap.get(PATIENT_LAST_NAME)],
                issuerOfPatientID,
                projectEntity.getId());
        patientsList.add(newPatient);
      }
    } catch (Exception e) {
      LOGGER.error("Error when reading selected columns", e);
    }
  }

  public Button getReadCSVButton() {
    return readCSVButton;
  }

  public List<ExternalIDCSVPatient> getPatientsList() {
    return patientsList;
  }

  public void resetPatientsList() {
    patientsList.clear();
  }
}
