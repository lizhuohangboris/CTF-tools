package org.attoparser;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.naming.EjbRef;
import org.attoparser.util.TextUtil;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.web.servlet.tags.form.ErrorsTag;
import org.springframework.web.servlet.tags.form.FormTag;
import org.thymeleaf.engine.DocType;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringOptionInSelectFieldTagProcessor;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlElements.class */
final class HtmlElements {
    private static final HtmlElementRepository ELEMENTS = new HtmlElementRepository();
    static final HtmlElement HTML = new HtmlElement(DocType.DEFAULT_ELEMENT_NAME);
    static final HtmlElement HEAD = new HtmlAutoOpenElement("head", new String[]{DocType.DEFAULT_ELEMENT_NAME}, null);
    static final HtmlElement TITLE = new HtmlHeadElement("title");
    static final HtmlElement BASE = new HtmlVoidHeadElement("base");
    static final HtmlElement LINK = new HtmlVoidHeadElement(EjbRef.LINK);
    static final HtmlElement META = new HtmlVoidHeadElement(BeanDefinitionParserDelegate.META_ELEMENT);
    static final HtmlElement STYLE = new HtmlHeadCDATAContentElement("style");
    static final HtmlElement SCRIPT = new HtmlHeadCDATAContentElement("script");
    static final HtmlElement NOSCRIPT = new HtmlHeadElement("noscript");
    static final HtmlElement BODY = new HtmlAutoOpenCloseElement(StandardRemoveTagProcessor.VALUE_BODY, new String[]{DocType.DEFAULT_ELEMENT_NAME}, null, new String[]{"head"}, null);
    static final HtmlElement ARTICLE = new HtmlBodyBlockElement("article");
    static final HtmlElement SECTION = new HtmlBodyBlockElement("section");
    static final HtmlElement NAV = new HtmlBodyBlockElement("nav");
    static final HtmlElement ASIDE = new HtmlBodyBlockElement("aside");
    static final HtmlElement H1 = new HtmlBodyBlockElement("h1");
    static final HtmlElement H2 = new HtmlBodyBlockElement("h2");
    static final HtmlElement H3 = new HtmlBodyBlockElement("h3");
    static final HtmlElement H4 = new HtmlBodyBlockElement("h4");
    static final HtmlElement H5 = new HtmlBodyBlockElement("h5");
    static final HtmlElement H6 = new HtmlBodyBlockElement("h6");
    static final HtmlElement HGROUP = new HtmlBodyBlockElement("hgroup");
    static final HtmlElement HEADER = new HtmlBodyBlockElement("header");
    static final HtmlElement FOOTER = new HtmlBodyBlockElement("footer");
    static final HtmlElement ADDRESS = new HtmlBodyBlockElement("address");
    static final HtmlElement MAIN = new HtmlBodyBlockElement("main");
    static final HtmlElement P = new HtmlBodyBlockElement("p");
    static final HtmlElement HR = new HtmlVoidBodyBlockElement("hr");
    static final HtmlElement PRE = new HtmlBodyBlockElement("pre");
    static final HtmlElement BLOCKQUOTE = new HtmlBodyBlockElement("blockquote");
    static final HtmlElement OL = new HtmlBodyBlockElement("ol");
    static final HtmlElement UL = new HtmlBodyBlockElement("ul");
    static final HtmlElement LI = new HtmlBodyAutoCloseElement("li", new String[]{"li"}, new String[]{"ul", "ol"});
    static final HtmlElement DL = new HtmlBodyBlockElement("dl");
    static final HtmlElement DT = new HtmlBodyAutoCloseElement("dt", new String[]{"dt", "dd"}, new String[]{"dl"});
    static final HtmlElement DD = new HtmlBodyAutoCloseElement("dd", new String[]{"dt", "dd"}, new String[]{"dl"});
    static final HtmlElement FIGURE = new HtmlBodyElement("figure");
    static final HtmlElement FIGCAPTION = new HtmlBodyElement("figcaption");
    static final HtmlElement DIV = new HtmlBodyBlockElement("div");
    static final HtmlElement A = new HtmlBodyElement("a");
    static final HtmlElement EM = new HtmlBodyElement("em");
    static final HtmlElement STRONG = new HtmlBodyElement("strong");
    static final HtmlElement SMALL = new HtmlBodyElement("small");
    static final HtmlElement S = new HtmlBodyElement("s");
    static final HtmlElement CITE = new HtmlBodyElement("cite");
    static final HtmlElement G = new HtmlBodyElement("g");
    static final HtmlElement DFN = new HtmlBodyElement("dfn");
    static final HtmlElement ABBR = new HtmlBodyElement("abbr");
    static final HtmlElement TIME = new HtmlBodyElement(SpringInputGeneralFieldTagProcessor.TIME_INPUT_TYPE_ATTR_VALUE);
    static final HtmlElement CODE = new HtmlBodyElement("code");
    static final HtmlElement VAR = new HtmlBodyElement("var");
    static final HtmlElement SAMP = new HtmlBodyElement("samp");
    static final HtmlElement KBD = new HtmlBodyElement("kbd");
    static final HtmlElement SUB = new HtmlBodyElement("sub");
    static final HtmlElement SUP = new HtmlBodyElement("sup");
    static final HtmlElement I = new HtmlBodyElement(IntegerTokenConverter.CONVERTER_KEY);
    static final HtmlElement B = new HtmlBodyElement("b");
    static final HtmlElement U = new HtmlBodyElement("u");
    static final HtmlElement MARK = new HtmlBodyElement("mark");
    static final HtmlElement RUBY = new HtmlBodyElement("ruby");
    static final HtmlElement RB = new HtmlBodyAutoCloseElement("rb", new String[]{"rb", "rt", "rtc", "rp"}, new String[]{"ruby"});
    static final HtmlElement RT = new HtmlBodyAutoCloseElement("rt", new String[]{"rb", "rt", "rp"}, new String[]{"ruby", "rtc"});
    static final HtmlElement RTC = new HtmlBodyAutoCloseElement("rtc", new String[]{"rb", "rt", "rtc", "rp"}, new String[]{"ruby"});
    static final HtmlElement RP = new HtmlBodyAutoCloseElement("rp", new String[]{"rb", "rt", "rp"}, new String[]{"ruby", "rtc"});
    static final HtmlElement BDI = new HtmlBodyElement("bdi");
    static final HtmlElement BDO = new HtmlBodyElement("bdo");
    static final HtmlElement SPAN = new HtmlBodyElement(ErrorsTag.SPAN_TAG);
    static final HtmlElement BR = new HtmlVoidBodyElement("br");
    static final HtmlElement WBR = new HtmlVoidBodyElement("wbr");
    static final HtmlElement INS = new HtmlBodyElement("ins");
    static final HtmlElement DEL = new HtmlBodyElement("del");
    static final HtmlElement IMG = new HtmlVoidBodyElement("img");
    static final HtmlElement IFRAME = new HtmlBodyElement("iframe");
    static final HtmlElement EMBED = new HtmlVoidBodyElement("embed");
    static final HtmlElement OBJECT = new HtmlHeadElement("object");
    static final HtmlElement PARAM = new HtmlVoidBodyElement("param");
    static final HtmlElement VIDEO = new HtmlBodyElement("video");
    static final HtmlElement AUDIO = new HtmlBodyElement("audio");
    static final HtmlElement SOURCE = new HtmlVoidBodyElement("source");
    static final HtmlElement TRACK = new HtmlVoidBodyElement("track");
    static final HtmlElement CANVAS = new HtmlBodyElement("canvas");
    static final HtmlElement MAP = new HtmlBodyElement(BeanDefinitionParserDelegate.MAP_ELEMENT);
    static final HtmlElement AREA = new HtmlVoidBodyElement("area");
    static final HtmlElement TABLE = new HtmlBodyBlockElement("table");
    static final HtmlElement CAPTION = new HtmlBodyAutoCloseElement("caption", new String[]{"tr", "td", "th", "thead", "tfoot", "tbody", "caption", "colgroup"}, new String[]{"table"});
    static final HtmlElement COLGROUP = new HtmlBodyAutoCloseElement("colgroup", new String[]{"tr", "td", "th", "thead", "tfoot", "tbody", "caption", "colgroup"}, new String[]{"table"});
    static final HtmlElement COL = new HtmlVoidAutoOpenCloseElement("col", new String[]{"colgroup"}, new String[]{"colgroup"}, new String[]{"tr", "td", "th", "thead", "tfoot", "tbody", "caption"}, new String[]{"table"});
    static final HtmlElement TBODY = new HtmlBodyAutoCloseElement("tbody", new String[]{"tr", "td", "th", "thead", "tfoot", "tbody", "caption", "colgroup"}, new String[]{"table"});
    static final HtmlElement THEAD = new HtmlBodyAutoCloseElement("thead", new String[]{"tr", "td", "th", "thead", "tfoot", "tbody", "caption", "colgroup"}, new String[]{"table"});
    static final HtmlElement TFOOT = new HtmlBodyAutoCloseElement("tfoot", new String[]{"tr", "td", "th", "thead", "tfoot", "tbody", "caption", "colgroup"}, new String[]{"table"});
    static final HtmlElement TR = new HtmlAutoOpenCloseElement("tr", new String[]{"tbody"}, new String[]{"thead", "tfoot", "tbody"}, new String[]{"tr", "td", "th", "caption", "colgroup"}, new String[]{"table", "thead", "tbody", "tfoot"});
    static final HtmlElement TD = new HtmlBodyAutoCloseElement("td", new String[]{"td", "th"}, new String[]{"tr"});
    static final HtmlElement TH = new HtmlBodyAutoCloseElement("th", new String[]{"td", "th"}, new String[]{"tr"});
    static final HtmlElement FORM = new HtmlBodyBlockElement("form");
    static final HtmlElement FIELDSET = new HtmlBodyBlockElement("fieldset");
    static final HtmlElement LEGEND = new HtmlBodyElement("legend");
    static final HtmlElement LABEL = new HtmlBodyElement("label");
    static final HtmlElement INPUT = new HtmlVoidBodyElement("input");
    static final HtmlElement BUTTON = new HtmlBodyElement("button");
    static final HtmlElement SELECT = new HtmlBodyElement("select");
    static final HtmlElement DATALIST = new HtmlBodyElement("datalist");
    static final HtmlElement OPTGROUP = new HtmlBodyAutoCloseElement("optgroup", new String[]{"optgroup", SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME}, new String[]{"select"});
    static final HtmlElement OPTION = new HtmlBodyAutoCloseElement(SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME, new String[]{SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME}, new String[]{"select", "optgroup", "datalist"});
    static final HtmlElement TEXTAREA = new HtmlBodyElement("textarea");
    static final HtmlElement KEYGEN = new HtmlVoidBodyElement("keygen");
    static final HtmlElement OUTPUT = new HtmlBodyElement("output");
    static final HtmlElement PROGRESS = new HtmlBodyElement("progress");
    static final HtmlElement METER = new HtmlBodyElement("meter");
    static final HtmlElement DETAILS = new HtmlBodyElement("details");
    static final HtmlElement SUMMARY = new HtmlBodyElement("summary");
    static final HtmlElement COMMAND = new HtmlBodyElement(FormTag.DEFAULT_COMMAND_NAME);
    static final HtmlElement MENU = new HtmlBodyBlockElement("menu");
    static final HtmlElement MENUITEM = new HtmlVoidBodyElement("menuitem");
    static final HtmlElement DIALOG = new HtmlBodyElement("dialog");
    static final HtmlElement TEMPLATE = new HtmlHeadElement("template");
    static final HtmlElement ELEMENT = new HtmlHeadElement("element");
    static final HtmlElement DECORATOR = new HtmlHeadElement("decorator");
    static final HtmlElement CONTENT = new HtmlHeadElement("content");
    static final HtmlElement SHADOW = new HtmlHeadElement("shadow");
    static final Set<HtmlElement> ALL_STANDARD_ELEMENTS = Collections.unmodifiableSet(new LinkedHashSet(Arrays.asList(HTML, HEAD, TITLE, BASE, LINK, META, STYLE, SCRIPT, NOSCRIPT, BODY, ARTICLE, SECTION, NAV, ASIDE, H1, H2, H3, H4, H5, H6, HGROUP, HEADER, FOOTER, ADDRESS, P, HR, PRE, BLOCKQUOTE, OL, UL, LI, DL, DT, DD, FIGURE, FIGCAPTION, DIV, A, EM, STRONG, SMALL, S, CITE, G, DFN, ABBR, TIME, CODE, VAR, SAMP, KBD, SUB, SUP, I, B, U, MARK, RUBY, RB, RT, RTC, RP, BDI, BDO, SPAN, BR, WBR, INS, DEL, IMG, IFRAME, EMBED, OBJECT, PARAM, VIDEO, AUDIO, SOURCE, TRACK, CANVAS, MAP, AREA, TABLE, CAPTION, COLGROUP, COL, TBODY, THEAD, TFOOT, TR, TD, TH, FORM, FIELDSET, LEGEND, LABEL, INPUT, BUTTON, SELECT, DATALIST, OPTGROUP, OPTION, TEXTAREA, KEYGEN, OUTPUT, PROGRESS, METER, DETAILS, SUMMARY, COMMAND, MENU, MENUITEM, DIALOG, MAIN, TEMPLATE, ELEMENT, DECORATOR, CONTENT, SHADOW)));

    static {
        for (HtmlElement element : ALL_STANDARD_ELEMENTS) {
            ELEMENTS.storeStandardElement(element);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HtmlElement forName(char[] elementNameBuffer, int offset, int len) {
        if (elementNameBuffer == null) {
            throw new IllegalArgumentException("Buffer cannot be null");
        }
        return ELEMENTS.getElement(elementNameBuffer, offset, len);
    }

    private HtmlElements() {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlElements$HtmlElementRepository.class */
    static final class HtmlElementRepository {
        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();
        private final List<HtmlElement> standardRepository = new ArrayList(150);
        private final List<HtmlElement> repository = new ArrayList(150);

        HtmlElementRepository() {
        }

        HtmlElement getElement(char[] text, int offset, int len) {
            int index = binarySearch(this.standardRepository, text, offset, len);
            if (index >= 0) {
                return this.standardRepository.get(index);
            }
            this.readLock.lock();
            try {
                int index2 = binarySearch(this.repository, text, offset, len);
                if (index2 >= 0) {
                    HtmlElement htmlElement = this.repository.get(index2);
                    this.readLock.unlock();
                    return htmlElement;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    HtmlElement storeElement = storeElement(text, offset, len);
                    this.writeLock.unlock();
                    return storeElement;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        private HtmlElement storeElement(char[] text, int offset, int len) {
            int index = binarySearch(this.repository, text, offset, len);
            if (index >= 0) {
                return this.repository.get(index);
            }
            HtmlElement element = new HtmlElement(new String(text, offset, len).toLowerCase());
            this.repository.add((index + 1) * (-1), element);
            return element;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public HtmlElement storeStandardElement(HtmlElement element) {
            this.standardRepository.add(element);
            this.repository.add(element);
            Collections.sort(this.standardRepository, ElementComparator.INSTANCE);
            Collections.sort(this.repository, ElementComparator.INSTANCE);
            return element;
        }

        private static int binarySearch(List<HtmlElement> values, char[] text, int offset, int len) {
            int low = 0;
            int high = values.size() - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                char[] midVal = values.get(mid).name;
                int cmp = TextUtil.compareTo(false, midVal, 0, midVal.length, text, offset, len);
                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    return mid;
                }
            }
            return -(low + 1);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlElements$HtmlElementRepository$ElementComparator.class */
        public static class ElementComparator implements Comparator<HtmlElement> {
            private static ElementComparator INSTANCE = new ElementComparator();

            private ElementComparator() {
            }

            @Override // java.util.Comparator
            public int compare(HtmlElement o1, HtmlElement o2) {
                return TextUtil.compareTo(false, o1.name, o2.name);
            }
        }
    }
}