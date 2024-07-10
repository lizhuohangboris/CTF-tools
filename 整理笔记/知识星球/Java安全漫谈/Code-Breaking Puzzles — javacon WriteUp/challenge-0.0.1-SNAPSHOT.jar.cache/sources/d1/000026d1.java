package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.Map;
import javax.servlet.jsp.JspException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.spring5.processor.SpringOptionInSelectFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/form/OptionWriter.class */
class OptionWriter {
    private final Object optionSource;
    private final BindStatus bindStatus;
    @Nullable
    private final String valueProperty;
    @Nullable
    private final String labelProperty;
    private final boolean htmlEscape;

    public OptionWriter(Object optionSource, BindStatus bindStatus, @Nullable String valueProperty, @Nullable String labelProperty, boolean htmlEscape) {
        Assert.notNull(optionSource, "'optionSource' must not be null");
        Assert.notNull(bindStatus, "'bindStatus' must not be null");
        this.optionSource = optionSource;
        this.bindStatus = bindStatus;
        this.valueProperty = valueProperty;
        this.labelProperty = labelProperty;
        this.htmlEscape = htmlEscape;
    }

    public void writeOptions(TagWriter tagWriter) throws JspException {
        if (this.optionSource.getClass().isArray()) {
            renderFromArray(tagWriter);
        } else if (this.optionSource instanceof Collection) {
            renderFromCollection(tagWriter);
        } else if (this.optionSource instanceof Map) {
            renderFromMap(tagWriter);
        } else if ((this.optionSource instanceof Class) && ((Class) this.optionSource).isEnum()) {
            renderFromEnum(tagWriter);
        } else {
            throw new JspException("Type [" + this.optionSource.getClass().getName() + "] is not valid for option items");
        }
    }

    private void renderFromArray(TagWriter tagWriter) throws JspException {
        doRenderFromCollection(CollectionUtils.arrayToList(this.optionSource), tagWriter);
    }

    private void renderFromMap(TagWriter tagWriter) throws JspException {
        Map<?, ?> optionMap = (Map) this.optionSource;
        for (Map.Entry<?, ?> entry : optionMap.entrySet()) {
            Object mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            Object renderValue = this.valueProperty != null ? PropertyAccessorFactory.forBeanPropertyAccess(mapKey).getPropertyValue(this.valueProperty) : mapKey;
            Object renderLabel = this.labelProperty != null ? PropertyAccessorFactory.forBeanPropertyAccess(mapValue).getPropertyValue(this.labelProperty) : mapValue;
            renderOption(tagWriter, mapKey, renderValue, renderLabel);
        }
    }

    private void renderFromCollection(TagWriter tagWriter) throws JspException {
        doRenderFromCollection((Collection) this.optionSource, tagWriter);
    }

    private void renderFromEnum(TagWriter tagWriter) throws JspException {
        doRenderFromCollection(CollectionUtils.arrayToList(((Class) this.optionSource).getEnumConstants()), tagWriter);
    }

    private void doRenderFromCollection(Collection<?> optionCollection, TagWriter tagWriter) throws JspException {
        Object value;
        for (Object item : optionCollection) {
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(item);
            if (this.valueProperty != null) {
                value = wrapper.getPropertyValue(this.valueProperty);
            } else if (item instanceof Enum) {
                value = ((Enum) item).name();
            } else {
                value = item;
            }
            Object label = this.labelProperty != null ? wrapper.getPropertyValue(this.labelProperty) : item;
            renderOption(tagWriter, item, value, label);
        }
    }

    private void renderOption(TagWriter tagWriter, Object item, @Nullable Object value, @Nullable Object label) throws JspException {
        tagWriter.startTag(SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME);
        writeCommonAttributes(tagWriter);
        String valueDisplayString = getDisplayString(value);
        String labelDisplayString = getDisplayString(label);
        tagWriter.writeAttribute("value", processOptionValue(valueDisplayString));
        if (isOptionSelected(value) || (value != item && isOptionSelected(item))) {
            tagWriter.writeAttribute("selected", "selected");
        }
        if (isOptionDisabled()) {
            tagWriter.writeAttribute("disabled", "disabled");
        }
        tagWriter.appendValue(labelDisplayString);
        tagWriter.endTag();
    }

    private String getDisplayString(@Nullable Object value) {
        PropertyEditor editor = value != null ? this.bindStatus.findEditor(value.getClass()) : null;
        return ValueFormatter.getDisplayString(value, editor, this.htmlEscape);
    }

    protected String processOptionValue(String resolvedValue) {
        return resolvedValue;
    }

    private boolean isOptionSelected(@Nullable Object resolvedValue) {
        return SelectedValueComparator.isSelected(this.bindStatus, resolvedValue);
    }

    protected boolean isOptionDisabled() throws JspException {
        return false;
    }

    protected void writeCommonAttributes(TagWriter tagWriter) throws JspException {
    }
}