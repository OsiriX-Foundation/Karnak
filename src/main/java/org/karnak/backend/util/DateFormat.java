/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.karnak.backend.data.entity.ArgumentEntity;
import org.karnak.backend.dicom.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateFormat {
  private static final Logger LOGGER = LoggerFactory.getLogger(DateFormat.class);

  public static String formatDA(String date, String option) {
    LocalDate localDate = DateTimeUtils.parseDA(date);
    switch (option) {
      case "day":
        localDate = localDate.minusDays(localDate.getDayOfMonth() - 1L);
        break;
      case "month_day":
        localDate = localDate.minusDays(localDate.getDayOfMonth() - 1L);
        localDate = localDate.minusMonths(localDate.getMonthValue() - 1L);
    }

    return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

  public static String formatDT(String dateTime, String option) {

    Temporal localDateTime = DateTimeUtils.parseDT(dateTime);

    switch (option) {
      case "day":
        localDateTime =
            localDateTime.minus(localDateTime.get(ChronoField.DAY_OF_MONTH) - 1L, ChronoUnit.DAYS);
        break;
      case "month_day":
        localDateTime =
            localDateTime.minus(localDateTime.get(ChronoField.DAY_OF_MONTH) - 1L, ChronoUnit.DAYS);
        localDateTime =
            localDateTime.minus(
                localDateTime.get(ChronoField.MONTH_OF_YEAR) - 1L, ChronoUnit.MONTHS);
    }

    return DateTimeUtils.formatDT(localDateTime);
  }

  public static String format(Attributes dcm, int tag, List<ArgumentEntity> argumentEntities)
      throws DateTimeException {
    try {
      verifyPatternArguments(argumentEntities);
    } catch (IllegalArgumentException e) {
      throw e;
    }

    String dcmElValue = dcm.getString(tag);
    String format = "";

    for (ArgumentEntity argumentEntity : argumentEntities) {
      final String key = argumentEntity.getKey();
      final String value = argumentEntity.getValue();

      try {
        if (key.equals("remove")) {
          format = value;
        }
      } catch (Exception e) {
        LOGGER.error("args {} is not correct", value, e);
      }
    }
    if (dcmElValue != null) {
      return switch (dcm.getVR(tag)) {
        case DA -> formatDA(dcmElValue, format);
        case DT -> formatDT(dcmElValue, format);
        default -> null;
      };
    } else {
      return null;
    }
  }

  public static void verifyPatternArguments(List<ArgumentEntity> argumentEntities)
      throws IllegalArgumentException {
    List<String> listValue = new ArrayList<>();
    listValue.add("day");
    listValue.add("month_day");

    if (argumentEntities.stream()
        .noneMatch(
            argument ->
                argument.getKey().equals("remove") && listValue.contains(argument.getValue()))) {
      IllegalArgumentException missingParameters =
          new IllegalArgumentException(
              "Cannot build the option date_format, arguments are not correct");
      LOGGER.error("Missing argument, the class need pattern as parameters", missingParameters);
      throw missingParameters;
    }
  }
}
