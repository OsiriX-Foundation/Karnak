/*
* Copyright (c) 2021 Weasis Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.model.profilebody;

import java.util.List;

public class MaskBody {

  private String stationName;
  private String color;
  private List<String> rectangles;

  public String getStationName() {
    return stationName;
  }

  public void setStationName(String stationName) {
    this.stationName = stationName;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public List<String> getRectangles() {
    return rectangles;
  }

  public void setRectangles(List<String> rectangles) {
    this.rectangles = rectangles;
  }
}
