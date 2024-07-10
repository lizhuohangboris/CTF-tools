package javax.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ResourceBundleELResolver.class */
public class ResourceBundleELResolver extends ELResolver {
    @Override // javax.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(base, property);
            if (property != null) {
                try {
                    return ((ResourceBundle) base).getObject(property.toString());
                } catch (MissingResourceException e) {
                    return "???" + property.toString() + "???";
                }
            }
            return null;
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(base, property);
            return null;
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(base, property);
            throw new PropertyNotWritableException(Util.message(context, "resolverNotWriteable", base.getClass().getName()));
        }
    }

    @Override // javax.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(base, property);
            return true;
        }
        return false;
    }

    @Override // javax.el.ELResolver
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base instanceof ResourceBundle) {
            List<FeatureDescriptor> feats = new ArrayList<>();
            Enumeration<String> e = ((ResourceBundle) base).getKeys();
            while (e.hasMoreElements()) {
                String key = e.nextElement();
                FeatureDescriptor feat = new FeatureDescriptor();
                feat.setDisplayName(key);
                feat.setShortDescription("");
                feat.setExpert(false);
                feat.setHidden(false);
                feat.setName(key);
                feat.setPreferred(true);
                feat.setValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
                feat.setValue("type", String.class);
                feats.add(feat);
            }
            return feats.iterator();
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof ResourceBundle) {
            return String.class;
        }
        return null;
    }
}