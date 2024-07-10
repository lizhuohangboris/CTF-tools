package org.springframework.web.bind;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.core.CollectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.MultipartFile;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/WebDataBinder.class */
public class WebDataBinder extends DataBinder {
    public static final String DEFAULT_FIELD_MARKER_PREFIX = "_";
    public static final String DEFAULT_FIELD_DEFAULT_PREFIX = "!";
    @Nullable
    private String fieldMarkerPrefix;
    @Nullable
    private String fieldDefaultPrefix;
    private boolean bindEmptyMultipartFiles;

    public WebDataBinder(@Nullable Object target) {
        super(target);
        this.fieldMarkerPrefix = "_";
        this.fieldDefaultPrefix = "!";
        this.bindEmptyMultipartFiles = true;
    }

    public WebDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
        this.fieldMarkerPrefix = "_";
        this.fieldDefaultPrefix = "!";
        this.bindEmptyMultipartFiles = true;
    }

    public void setFieldMarkerPrefix(@Nullable String fieldMarkerPrefix) {
        this.fieldMarkerPrefix = fieldMarkerPrefix;
    }

    @Nullable
    public String getFieldMarkerPrefix() {
        return this.fieldMarkerPrefix;
    }

    public void setFieldDefaultPrefix(@Nullable String fieldDefaultPrefix) {
        this.fieldDefaultPrefix = fieldDefaultPrefix;
    }

    @Nullable
    public String getFieldDefaultPrefix() {
        return this.fieldDefaultPrefix;
    }

    public void setBindEmptyMultipartFiles(boolean bindEmptyMultipartFiles) {
        this.bindEmptyMultipartFiles = bindEmptyMultipartFiles;
    }

    public boolean isBindEmptyMultipartFiles() {
        return this.bindEmptyMultipartFiles;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.validation.DataBinder
    public void doBind(MutablePropertyValues mpvs) {
        checkFieldDefaults(mpvs);
        checkFieldMarkers(mpvs);
        super.doBind(mpvs);
    }

    protected void checkFieldDefaults(MutablePropertyValues mpvs) {
        String fieldDefaultPrefix = getFieldDefaultPrefix();
        if (fieldDefaultPrefix != null) {
            PropertyValue[] pvArray = mpvs.getPropertyValues();
            for (PropertyValue pv : pvArray) {
                if (pv.getName().startsWith(fieldDefaultPrefix)) {
                    String field = pv.getName().substring(fieldDefaultPrefix.length());
                    if (getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
                        mpvs.add(field, pv.getValue());
                    }
                    mpvs.removePropertyValue(pv);
                }
            }
        }
    }

    protected void checkFieldMarkers(MutablePropertyValues mpvs) {
        String fieldMarkerPrefix = getFieldMarkerPrefix();
        if (fieldMarkerPrefix != null) {
            PropertyValue[] pvArray = mpvs.getPropertyValues();
            for (PropertyValue pv : pvArray) {
                if (pv.getName().startsWith(fieldMarkerPrefix)) {
                    String field = pv.getName().substring(fieldMarkerPrefix.length());
                    if (getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
                        Class<?> fieldType = getPropertyAccessor().getPropertyType(field);
                        mpvs.add(field, getEmptyValue(field, fieldType));
                    }
                    mpvs.removePropertyValue(pv);
                }
            }
        }
    }

    @Nullable
    protected Object getEmptyValue(String field, @Nullable Class<?> fieldType) {
        if (fieldType != null) {
            return getEmptyValue(fieldType);
        }
        return null;
    }

    @Nullable
    public Object getEmptyValue(Class<?> fieldType) {
        try {
            if (Boolean.TYPE == fieldType || Boolean.class == fieldType) {
                return Boolean.FALSE;
            }
            if (fieldType.isArray()) {
                return Array.newInstance(fieldType.getComponentType(), 0);
            }
            if (Collection.class.isAssignableFrom(fieldType)) {
                return CollectionFactory.createCollection(fieldType, 0);
            }
            if (Map.class.isAssignableFrom(fieldType)) {
                return CollectionFactory.createMap(fieldType, 0);
            }
            return null;
        } catch (IllegalArgumentException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to create default value - falling back to null: " + ex.getMessage());
                return null;
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void bindMultipart(Map<String, List<MultipartFile>> multipartFiles, MutablePropertyValues mpvs) {
        multipartFiles.forEach(key, values -> {
            if (values.size() == 1) {
                MultipartFile value = (MultipartFile) values.get(0);
                if (isBindEmptyMultipartFiles() || !value.isEmpty()) {
                    mpvs.add(key, value);
                    return;
                }
                return;
            }
            mpvs.add(key, values);
        });
    }
}