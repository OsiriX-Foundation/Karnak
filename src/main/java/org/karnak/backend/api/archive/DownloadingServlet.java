/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.api.archive;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.karnak.backend.service.gateway.AbstractGateway;
import org.karnak.backend.service.gateway.GatewaySetUpService;
import org.karnak.backend.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.weasis.core.util.FileUtil;

@WebServlet(urlPatterns = "/download")
public class DownloadingServlet extends HttpServlet {

  @Serial private static final long serialVersionUID = -3991470951272725755L;
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadingServlet.class);

  @Autowired private GatewaySetUpService globalConfig;

  @Override
  public final void init() throws ServletException {
    if (globalConfig == null) {
      LOGGER.error("DownloadingServlet service cannot start: GatewaySetUpService is missing.");
      destroy();
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    if (globalConfig == null) {
      String errorMsg = "Missing 'GlobalConfig' from current ServletContext";
      LOGGER.error(errorMsg);
      ServletUtil.sendResponseError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
      return;
    }
    final Path archiveDir = globalConfig.getStorePath();

    try {
      if (archiveDir == null || !Files.isDirectory(archiveDir) || !Files.isReadable(archiveDir)) {
        throw new IllegalStateException("Cannot access to the archive directory");
      } else {
        String aet = req.getParameter("aet");
        String filename = req.getParameter("sopuid");
        if (aet != null && filename != null) {
          Path file = Path.of(archiveDir.toString(), aet, filename);
          String delete = req.getParameter("delete");
          if ("true".equals(delete)) {
            FileUtil.delete(file);
          } else {
            download(res, file);
          }
        }
      }
    } catch (Exception e1) {
      LOGGER.error("Unexpected exception when downloading", e1);
      ServletUtil.sendResponseError(
          res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.getMessage());
    }

    try {
      if (archiveDir != null) {
        AbstractGateway.deleteOldFiles(archiveDir.toFile());
      }
    } catch (SecurityException e) {
      LOGGER.error("SecurityException:", e);
    }
  }

  /**
   * Sends a file to the ServletResponse output stream. Typically you want the browser to receive a
   * different name than the name the file has been saved in your local database, since your local
   * names need to be unique.
   *
   * @param resp The response
   * @param path The path of the file you want to download.
   * @throws IOException
   */
  private boolean download(HttpServletResponse resp, Path path) throws IOException {
    if (path == null || !Files.isReadable(path)) {
      LOGGER.warn("Cannot get this file for downloading: {}", path);
      return false;
    }

    try (DataInputStream in = new DataInputStream(Files.newInputStream(path));
        ServletOutputStream op = resp.getOutputStream()) {
      int length = 0;
      resp.setContentType("application/octet-stream");
      resp.setContentLength((int) Files.size(path));
      resp.setHeader("Content-Disposition", "attachment; filename=\"" + path.getFileName() + "\"");

      byte[] buf = new byte[4096];
      while ((length = in.read(buf)) != -1) {
        op.write(buf, 0, length);
      }
      op.flush();
      return true;
    }
  }
}
