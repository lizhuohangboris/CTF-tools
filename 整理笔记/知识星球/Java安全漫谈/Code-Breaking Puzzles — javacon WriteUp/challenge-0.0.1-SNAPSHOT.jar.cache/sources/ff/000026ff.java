package org.springframework.web.servlet.view.document;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/document/AbstractXlsxView.class */
public abstract class AbstractXlsxView extends AbstractXlsView {
    public AbstractXlsxView() {
        setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @Override // org.springframework.web.servlet.view.document.AbstractXlsView
    /* renamed from: createWorkbook */
    protected Workbook mo1884createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new XSSFWorkbook();
    }
}