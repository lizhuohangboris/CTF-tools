package org.thymeleaf.standard.serializer;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.unbescape.css.CssEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/serializer/StandardCSSSerializer.class */
public final class StandardCSSSerializer implements IStandardCSSSerializer {
    @Override // org.thymeleaf.standard.serializer.IStandardCSSSerializer
    public void serializeValue(Object object, Writer writer) {
        try {
            writeValue(writer, object);
        } catch (IOException e) {
            throw new TemplateProcessingException("An exception was raised while trying to serialize object to CSS", e);
        }
    }

    private static void writeValue(Writer writer, Object object) throws IOException {
        if (object == null) {
            writeNull(writer);
        } else if (object instanceof CharSequence) {
            writeString(writer, object.toString());
        } else if (object instanceof Character) {
            writeString(writer, object.toString());
        } else if (object instanceof Number) {
            writeNumber(writer, (Number) object);
        } else if (object instanceof Boolean) {
            writeBoolean(writer, (Boolean) object);
        } else {
            writeString(writer, object.toString());
        }
    }

    private static void writeNull(Writer writer) throws IOException {
        writer.write("");
    }

    private static void writeString(Writer writer, String str) throws IOException {
        writer.write(CssEscape.escapeCssIdentifier(str));
    }

    private static void writeNumber(Writer writer, Number number) throws IOException {
        writer.write(number.toString());
    }

    private static void writeBoolean(Writer writer, Boolean bool) throws IOException {
        writer.write(bool.toString());
    }
}