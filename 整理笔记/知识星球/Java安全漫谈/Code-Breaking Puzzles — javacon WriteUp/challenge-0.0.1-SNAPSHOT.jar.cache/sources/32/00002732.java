package org.springframework.web.servlet.view.xml;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamResult;
import org.springframework.lang.Nullable;
import org.springframework.oxm.Marshaller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/xml/MarshallingView.class */
public class MarshallingView extends AbstractView {
    public static final String DEFAULT_CONTENT_TYPE = "application/xml";
    @Nullable
    private Marshaller marshaller;
    @Nullable
    private String modelKey;

    public MarshallingView() {
        setContentType("application/xml");
        setExposePathVariables(false);
    }

    public MarshallingView(Marshaller marshaller) {
        this();
        Assert.notNull(marshaller, "Marshaller must not be null");
        this.marshaller = marshaller;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext() {
        Assert.notNull(this.marshaller, "Property 'marshaller' is required");
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object toBeMarshalled = locateToBeMarshalled(model);
        if (toBeMarshalled == null) {
            throw new IllegalStateException("Unable to locate object to be marshalled in model: " + model);
        }
        Assert.state(this.marshaller != null, "No Marshaller set");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        this.marshaller.marshal(toBeMarshalled, new StreamResult(baos));
        setResponseContentType(request, response);
        response.setContentLength(baos.size());
        baos.writeTo(response.getOutputStream());
    }

    @Nullable
    protected Object locateToBeMarshalled(Map<String, Object> model) throws IllegalStateException {
        if (this.modelKey != null) {
            Object value = model.get(this.modelKey);
            if (value == null) {
                throw new IllegalStateException("Model contains no object with key [" + this.modelKey + "]");
            }
            if (!isEligibleForMarshalling(this.modelKey, value)) {
                throw new IllegalStateException("Model object [" + value + "] retrieved via key [" + this.modelKey + "] is not supported by the Marshaller");
            }
            return value;
        }
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            Object value2 = entry.getValue();
            if (value2 != null && (model.size() == 1 || !(value2 instanceof BindingResult))) {
                if (isEligibleForMarshalling(entry.getKey(), value2)) {
                    return value2;
                }
            }
        }
        return null;
    }

    protected boolean isEligibleForMarshalling(String modelKey, Object value) {
        Assert.state(this.marshaller != null, "No Marshaller set");
        Class<?> classToCheck = value.getClass();
        if (value instanceof JAXBElement) {
            classToCheck = ((JAXBElement) value).getDeclaredType();
        }
        return this.marshaller.supports(classToCheck);
    }
}