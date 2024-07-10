package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.VersionUtil;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/json/JsonGeneratorImpl.class */
public abstract class JsonGeneratorImpl extends GeneratorBase {
    protected static final int[] sOutputEscapes = CharTypes.get7BitOutputEscapes();
    protected final IOContext _ioContext;
    protected int[] _outputEscapes;
    protected int _maximumNonEscapedChar;
    protected CharacterEscapes _characterEscapes;
    protected SerializableString _rootValueSeparator;
    protected boolean _cfgUnqNames;

    public JsonGeneratorImpl(IOContext ctxt, int features, ObjectCodec codec) {
        super(features, codec);
        this._outputEscapes = sOutputEscapes;
        this._rootValueSeparator = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
        this._ioContext = ctxt;
        if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(features)) {
            this._maximumNonEscapedChar = 127;
        }
        this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(features);
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return VersionUtil.versionFor(getClass());
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator enable(JsonGenerator.Feature f) {
        super.enable(f);
        if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
            this._cfgUnqNames = false;
        }
        return this;
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator disable(JsonGenerator.Feature f) {
        super.disable(f);
        if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
            this._cfgUnqNames = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.GeneratorBase
    public void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
        super._checkStdFeatureChanges(newFeatureFlags, changedFeatures);
        this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(newFeatureFlags);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator setHighestNonEscapedChar(int charCode) {
        this._maximumNonEscapedChar = charCode < 0 ? 0 : charCode;
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int getHighestEscapedChar() {
        return this._maximumNonEscapedChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
        this._characterEscapes = esc;
        if (esc == null) {
            this._outputEscapes = sOutputEscapes;
        } else {
            this._outputEscapes = esc.getEscapeCodesForAscii();
        }
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public CharacterEscapes getCharacterEscapes() {
        return this._characterEscapes;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator setRootValueSeparator(SerializableString sep) {
        this._rootValueSeparator = sep;
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeStringField(String fieldName, String value) throws IOException {
        writeFieldName(fieldName);
        writeString(value);
    }

    public void _verifyPrettyValueWrite(String typeMsg, int status) throws IOException {
        switch (status) {
            case 0:
                if (this._writeContext.inArray()) {
                    this._cfgPrettyPrinter.beforeArrayValues(this);
                    return;
                } else if (this._writeContext.inObject()) {
                    this._cfgPrettyPrinter.beforeObjectEntries(this);
                    return;
                } else {
                    return;
                }
            case 1:
                this._cfgPrettyPrinter.writeArrayValueSeparator(this);
                return;
            case 2:
                this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
                return;
            case 3:
                this._cfgPrettyPrinter.writeRootValueSeparator(this);
                return;
            case 4:
            default:
                _throwInternal();
                return;
            case 5:
                _reportCantWriteValueExpectName(typeMsg);
                return;
        }
    }

    public void _reportCantWriteValueExpectName(String typeMsg) throws IOException {
        _reportError(String.format("Can not %s, expecting field name (context: %s)", typeMsg, this._writeContext.typeDesc()));
    }
}