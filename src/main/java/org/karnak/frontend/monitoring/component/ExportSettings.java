/*
 * Copyright (c) 2022 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.monitoring.component;

import com.opencsv.CSVWriter;

/**
 * Model used to collect the export settings of the user
 */
public class ExportSettings {

  private String delimiter;

  private String quoteCharacter;

  public static final char DEFAULT_CSV_DELIMITER = ',';

  /**
   * Constructor with default values
   */
  public ExportSettings() {
    this.delimiter = String.valueOf(DEFAULT_CSV_DELIMITER);
    this.quoteCharacter = String.valueOf(CSVWriter.DEFAULT_QUOTE_CHARACTER);
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public String getQuoteCharacter() {
    return quoteCharacter;
  }

  public void setQuoteCharacter(String quoteCharacter) {
    this.quoteCharacter = quoteCharacter;
  }
}
