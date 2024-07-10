package org.springframework.web.servlet.view.document;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/document/AbstractXlsxStreamingView.class */
public abstract class AbstractXlsxStreamingView extends AbstractXlsxView {
    @Override // org.springframework.web.servlet.view.document.AbstractXlsxView, org.springframework.web.servlet.view.document.AbstractXlsView
    /* renamed from: createWorkbook  reason: collision with other method in class */
    protected /* bridge */ /* synthetic */ Workbook mo1884createWorkbook(Map map, HttpServletRequest httpServletRequest) {
        return createWorkbook((Map<String, Object>) map, httpServletRequest);
    }

    protected SXSSFWorkbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new SXSSFWorkbook();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.document.AbstractXlsView
    public void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
        super.renderWorkbook(workbook, response);
        ((SXSSFWorkbook) workbook).dispose();
    }
}