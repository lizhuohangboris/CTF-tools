package org.springframework.web.servlet.view.document;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/document/AbstractPdfView.class */
public abstract class AbstractPdfView extends AbstractView {
    protected abstract void buildPdfDocument(Map<String, Object> map, Document document, PdfWriter pdfWriter, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;

    public AbstractPdfView() {
        setContentType(MediaType.APPLICATION_PDF_VALUE);
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream baos = createTemporaryOutputStream();
        Document document = newDocument();
        PdfWriter writer = newWriter(document, baos);
        prepareWriter(model, writer, request);
        buildPdfMetadata(model, document, request);
        document.open();
        buildPdfDocument(model, document, writer, request, response);
        document.close();
        writeToResponse(response, baos);
    }

    protected Document newDocument() {
        return new Document(PageSize.A4);
    }

    protected PdfWriter newWriter(Document document, OutputStream os) throws DocumentException {
        return PdfWriter.getInstance(document, os);
    }

    protected void prepareWriter(Map<String, Object> model, PdfWriter writer, HttpServletRequest request) throws DocumentException {
        writer.setViewerPreferences(getViewerPreferences());
    }

    protected int getViewerPreferences() {
        return 2053;
    }

    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
    }
}