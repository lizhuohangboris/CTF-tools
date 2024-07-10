package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.Iterator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.SafeHtml;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/SafeHtmlValidator.class */
public class SafeHtmlValidator implements ConstraintValidator<SafeHtml, CharSequence> {
    private Whitelist whitelist;
    private String baseURI;

    @Override // javax.validation.ConstraintValidator
    public void initialize(SafeHtml safeHtmlAnnotation) {
        SafeHtml.Tag[] additionalTagsWithAttributes;
        SafeHtml.Attribute[] attributesWithProtocols;
        switch (safeHtmlAnnotation.whitelistType()) {
            case BASIC:
                this.whitelist = Whitelist.basic();
                break;
            case BASIC_WITH_IMAGES:
                this.whitelist = Whitelist.basicWithImages();
                break;
            case NONE:
                this.whitelist = Whitelist.none();
                break;
            case RELAXED:
                this.whitelist = Whitelist.relaxed();
                break;
            case SIMPLE_TEXT:
                this.whitelist = Whitelist.simpleText();
                break;
        }
        this.baseURI = safeHtmlAnnotation.baseURI();
        this.whitelist.addTags(safeHtmlAnnotation.additionalTags());
        for (SafeHtml.Tag tag : safeHtmlAnnotation.additionalTagsWithAttributes()) {
            this.whitelist.addTags(new String[]{tag.name()});
            if (tag.attributes().length > 0) {
                this.whitelist.addAttributes(tag.name(), tag.attributes());
            }
            for (SafeHtml.Attribute attribute : tag.attributesWithProtocols()) {
                this.whitelist.addAttributes(tag.name(), new String[]{attribute.name()});
                if (attribute.protocols().length > 0) {
                    this.whitelist.addProtocols(tag.name(), attribute.name(), attribute.protocols());
                }
            }
        }
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return new Cleaner(this.whitelist).isValid(getFragmentAsDocument(value));
    }

    private Document getFragmentAsDocument(CharSequence value) {
        Document fragment = Jsoup.parse(value.toString(), this.baseURI, Parser.xmlParser());
        Document document = Document.createShell(this.baseURI);
        Iterator<Element> nodes = fragment.children().iterator();
        while (nodes.hasNext()) {
            document.body().appendChild((Node) nodes.next());
        }
        return document;
    }
}