package org.karnak.service.archive;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.karnak.service.AbstractGateway;
import org.karnak.service.GatewayConfig;
import org.karnak.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.weasis.dicom.param.DicomFileStream;

@WebServlet(urlPatterns = "/archive.xml")
public class ShowArchiveServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = -4229230848823235305L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowArchiveServlet.class);

    @Autowired
    private GatewayConfig globalConfig;
    
    @Override
    public final void init() throws ServletException {
        if (globalConfig == null) {
            LOGGER.error("ShowArchiveServlet service cannot start: GatewayConfig is missing.");
            destroy();
        }
    }
    
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/xml");
        PrintWriter out = res.getWriter();
        if (globalConfig == null) {
            String errorMsg = "Missing 'GlobalConfig' from current ServletContext";
            LOGGER.error(errorMsg);
            ServletUtil.sendResponseError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
            return;
        }
        
        final Path archiveDir = globalConfig.getStorePath();
        String result;
        if (archiveDir == null || !Files.isDirectory(archiveDir) || !Files.isReadable(archiveDir)) {
            result = "<archive/>";
        } else {
            StringBuilder sb = new StringBuilder("<archive>\n");
            try (Stream<Path> walk = Files.walk(archiveDir)) {
                walk.filter(p -> Files.isDirectory(p) && AbstractGateway.isFolderContainsFile(p.toFile())).forEach(p ->{
                    sb.append("<aet name=\"");
                    sb.append(p.getFileName());
                    sb.append("\">\n");
                    scanFiles(p, sb);
                    sb.append("</aet>\n");
                });
            } catch (IOException e) {
                LOGGER.error("Cannot check archive", e);
            }



            sb.append("</archive>\n");
            result = sb.toString();
        }
        out.println(result);
    }

    private static void scanFiles(Path aStartingDir, StringBuilder sb) {
        try (Stream<Path> walk = Files.walk(aStartingDir)) {
            walk.filter(p -> !Files.isDirectory(p) && Files.isReadable(p) && !p.endsWith(".part")).forEach(p ->{
                DicomFileStream info = new DicomFileStream(p);
                sb.append("<file tsuid=\"");
                sb.append(info.getTransferSyntax());
                sb.append("\" cuid\"");
                sb.append(info.getSopClassUID());
                sb.append("\" iuid\"");
                sb.append(info.getSopInstanceUID());
                sb.append("\">\n");
                sb.append(p.getFileName());
                sb.append("</file>\n");
            });
        } catch (IOException e) {
            LOGGER.error("Cannot check archive", e);
        }
    }
}
