package org.springframework.web.servlet;

import java.util.Map;
import org.springframework.beans.PropertyAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/ModelAndView.class */
public class ModelAndView {
    @Nullable
    private Object view;
    @Nullable
    private ModelMap model;
    @Nullable
    private HttpStatus status;
    private boolean cleared = false;

    public ModelAndView() {
    }

    public ModelAndView(String viewName) {
        this.view = viewName;
    }

    public ModelAndView(View view) {
        this.view = view;
    }

    public ModelAndView(String viewName, @Nullable Map<String, ?> model) {
        this.view = viewName;
        if (model != null) {
            getModelMap().addAllAttributes(model);
        }
    }

    public ModelAndView(View view, @Nullable Map<String, ?> model) {
        this.view = view;
        if (model != null) {
            getModelMap().addAllAttributes(model);
        }
    }

    public ModelAndView(String viewName, HttpStatus status) {
        this.view = viewName;
        this.status = status;
    }

    public ModelAndView(@Nullable String viewName, @Nullable Map<String, ?> model, @Nullable HttpStatus status) {
        this.view = viewName;
        if (model != null) {
            getModelMap().addAllAttributes(model);
        }
        this.status = status;
    }

    public ModelAndView(String viewName, String modelName, Object modelObject) {
        this.view = viewName;
        addObject(modelName, modelObject);
    }

    public ModelAndView(View view, String modelName, Object modelObject) {
        this.view = view;
        addObject(modelName, modelObject);
    }

    public void setViewName(@Nullable String viewName) {
        this.view = viewName;
    }

    @Nullable
    public String getViewName() {
        if (this.view instanceof String) {
            return (String) this.view;
        }
        return null;
    }

    public void setView(@Nullable View view) {
        this.view = view;
    }

    @Nullable
    public View getView() {
        if (this.view instanceof View) {
            return (View) this.view;
        }
        return null;
    }

    public boolean hasView() {
        return this.view != null;
    }

    public boolean isReference() {
        return this.view instanceof String;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Map<String, Object> getModelInternal() {
        return this.model;
    }

    public ModelMap getModelMap() {
        if (this.model == null) {
            this.model = new ModelMap();
        }
        return this.model;
    }

    public Map<String, Object> getModel() {
        return getModelMap();
    }

    public void setStatus(@Nullable HttpStatus status) {
        this.status = status;
    }

    @Nullable
    public HttpStatus getStatus() {
        return this.status;
    }

    public ModelAndView addObject(String attributeName, @Nullable Object attributeValue) {
        getModelMap().addAttribute(attributeName, attributeValue);
        return this;
    }

    public ModelAndView addObject(Object attributeValue) {
        getModelMap().addAttribute(attributeValue);
        return this;
    }

    public ModelAndView addAllObjects(@Nullable Map<String, ?> modelMap) {
        getModelMap().addAllAttributes(modelMap);
        return this;
    }

    public void clear() {
        this.view = null;
        this.model = null;
        this.cleared = true;
    }

    public boolean isEmpty() {
        return this.view == null && CollectionUtils.isEmpty(this.model);
    }

    public boolean wasCleared() {
        return this.cleared && isEmpty();
    }

    public String toString() {
        return "ModelAndView [view=" + formatView() + "; model=" + this.model + "]";
    }

    private String formatView() {
        return isReference() ? "\"" + this.view + "\"" : PropertyAccessor.PROPERTY_KEY_PREFIX + this.view + "]";
    }
}