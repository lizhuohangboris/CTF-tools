package org.springframework.http.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/DefaultPathContainer.class */
public final class DefaultPathContainer implements PathContainer {
    private static final MultiValueMap<String, String> EMPTY_MAP = new LinkedMultiValueMap();
    private static final PathContainer EMPTY_PATH = new DefaultPathContainer("", Collections.emptyList());
    private static final PathContainer.Separator SEPARATOR = () -> {
        return "/";
    };
    private final String path;
    private final List<PathContainer.Element> elements;

    private DefaultPathContainer(String path, List<PathContainer.Element> elements) {
        this.path = path;
        this.elements = Collections.unmodifiableList(elements);
    }

    @Override // org.springframework.http.server.PathContainer
    public String value() {
        return this.path;
    }

    @Override // org.springframework.http.server.PathContainer
    public List<PathContainer.Element> elements() {
        return this.elements;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return this.path.equals(((DefaultPathContainer) other).path);
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public String toString() {
        return value();
    }

    public static PathContainer createFromUrlPath(String path) {
        int begin;
        if (path.equals("")) {
            return EMPTY_PATH;
        }
        PathContainer.Element separatorElement = "/".equals(SEPARATOR.value()) ? SEPARATOR : ()
        /*  JADX ERROR: Method code generation error
            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x002b: TERNARY(r7v0 'separatorElement' org.springframework.http.server.PathContainer$Element) = ((wrap: boolean : 0x0019: INVOKE  (r0v4 boolean A[REMOVE]) = 
              ("/")
              (wrap: java.lang.String : 0x0014: INVOKE  (r1v2 java.lang.String A[REMOVE]) = 
              (wrap: org.springframework.http.server.PathContainer$Separator : 0x0011: SGET  (r1v1 org.springframework.http.server.PathContainer$Separator A[REMOVE]) =  org.springframework.http.server.DefaultPathContainer.SEPARATOR org.springframework.http.server.PathContainer$Separator)
             type: INTERFACE call: org.springframework.http.server.PathContainer.Separator.value():java.lang.String)
             type: VIRTUAL call: java.lang.String.equals(java.lang.Object):boolean) != false) ? (wrap: org.springframework.http.server.PathContainer$Separator : 0x001f: SGET  (r0v37 org.springframework.http.server.PathContainer$Separator A[REMOVE]) =  org.springframework.http.server.DefaultPathContainer.SEPARATOR org.springframework.http.server.PathContainer$Separator) : (wrap: org.springframework.http.server.PathContainer$Element : 0x0026: INVOKE_CUSTOM (r0v6 org.springframework.http.server.PathContainer$Element A[REMOVE]) = ("/")
             handle type: INVOKE_STATIC
             lambda: org.springframework.http.server.PathContainer.Separator.value():java.lang.String
             call insn: ?: INVOKE  (r0 I:java.lang.String) type: STATIC call: org.springframework.http.server.DefaultPathContainer.lambda$createFromUrlPath$1(java.lang.String):java.lang.String) in method: org.springframework.http.server.DefaultPathContainer.createFromUrlPath(java.lang.String):org.springframework.http.server.PathContainer, file: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/DefaultPathContainer.class
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:309)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:272)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:91)
            	at jadx.core.dex.nodes.IBlock.generate(IBlock.java:15)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
            	at jadx.core.dex.regions.Region.generate(Region.java:35)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
            	at jadx.core.dex.regions.Region.generate(Region.java:35)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
            	at jadx.core.dex.regions.Region.generate(Region.java:35)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:296)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:275)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:377)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:306)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:272)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
            	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            Caused by: java.lang.ClassCastException: class jadx.core.dex.instructions.args.InsnWrapArg cannot be cast to class jadx.core.dex.instructions.args.RegisterArg (jadx.core.dex.instructions.args.InsnWrapArg and jadx.core.dex.instructions.args.RegisterArg are in unnamed module of loader 'app')
            	at jadx.core.codegen.InsnGen.makeInlinedLambdaMethod(InsnGen.java:1019)
            	at jadx.core.codegen.InsnGen.makeInvokeLambda(InsnGen.java:924)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:815)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:421)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:144)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:120)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeTernary(InsnGen.java:1144)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:535)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:302)
            	... 19 more
            */
        /*
            r0 = r5
            java.lang.String r1 = ""
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto Ld
            org.springframework.http.server.PathContainer r0 = org.springframework.http.server.DefaultPathContainer.EMPTY_PATH
            return r0
        Ld:
            java.lang.String r0 = "/"
            r6 = r0
            r0 = r6
            org.springframework.http.server.PathContainer$Separator r1 = org.springframework.http.server.DefaultPathContainer.SEPARATOR
            java.lang.String r1 = r1.value()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L25
            org.springframework.http.server.PathContainer$Separator r0 = org.springframework.http.server.DefaultPathContainer.SEPARATOR
            goto L2b
        L25:
            r0 = r6
            org.springframework.http.server.PathContainer r0 = () -> { // org.springframework.http.server.PathContainer.Separator.value():java.lang.String
                return lambda$createFromUrlPath$1(r0);
            }
        L2b:
            r7 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = r0
            r1.<init>()
            r8 = r0
            r0 = r5
            int r0 = r0.length()
            if (r0 <= 0) goto L54
            r0 = r5
            r1 = r6
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L54
            r0 = r6
            int r0 = r0.length()
            r9 = r0
            r0 = r8
            r1 = r7
            boolean r0 = r0.add(r1)
            goto L57
        L54:
            r0 = 0
            r9 = r0
        L57:
            r0 = r9
            r1 = r5
            int r1 = r1.length()
            if (r0 >= r1) goto Lb5
            r0 = r5
            r1 = r6
            r2 = r9
            int r0 = r0.indexOf(r1, r2)
            r10 = r0
            r0 = r10
            r1 = -1
            if (r0 == r1) goto L7a
            r0 = r5
            r1 = r9
            r2 = r10
            java.lang.String r0 = r0.substring(r1, r2)
            goto L80
        L7a:
            r0 = r5
            r1 = r9
            java.lang.String r0 = r0.substring(r1)
        L80:
            r11 = r0
            r0 = r11
            java.lang.String r1 = ""
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L98
            r0 = r8
            r1 = r11
            org.springframework.http.server.PathContainer$PathSegment r1 = parsePathSegment(r1)
            boolean r0 = r0.add(r1)
        L98:
            r0 = r10
            r1 = -1
            if (r0 != r1) goto La1
            goto Lb5
        La1:
            r0 = r8
            r1 = r7
            boolean r0 = r0.add(r1)
            r0 = r10
            r1 = r6
            int r1 = r1.length()
            int r0 = r0 + r1
            r9 = r0
            goto L57
        Lb5:
            org.springframework.http.server.DefaultPathContainer r0 = new org.springframework.http.server.DefaultPathContainer
            r1 = r0
            r2 = r5
            r3 = r8
            r1.<init>(r2, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.http.server.DefaultPathContainer.createFromUrlPath(java.lang.String):org.springframework.http.server.PathContainer");
    }

    private static PathContainer.PathSegment parsePathSegment(String segment) {
        Charset charset = StandardCharsets.UTF_8;
        int index = segment.indexOf(59);
        if (index == -1) {
            String valueToMatch = StringUtils.uriDecode(segment, charset);
            return new DefaultPathSegment(segment, valueToMatch, EMPTY_MAP);
        }
        String valueToMatch2 = StringUtils.uriDecode(segment.substring(0, index), charset);
        String pathParameterContent = segment.substring(index);
        MultiValueMap<String, String> parameters = parsePathParams(pathParameterContent, charset);
        return new DefaultPathSegment(segment, valueToMatch2, parameters);
    }

    private static MultiValueMap<String, String> parsePathParams(String input, Charset charset) {
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        int i = 1;
        while (true) {
            int begin = i;
            if (begin >= input.length()) {
                break;
            }
            int end = input.indexOf(59, begin);
            String param = end != -1 ? input.substring(begin, end) : input.substring(begin);
            parsePathParamValues(param, charset, result);
            if (end == -1) {
                break;
            }
            i = end + 1;
        }
        return result;
    }

    private static void parsePathParamValues(String input, Charset charset, MultiValueMap<String, String> output) {
        String[] commaDelimitedListToStringArray;
        if (StringUtils.hasText(input)) {
            int index = input.indexOf(61);
            if (index != -1) {
                String name = input.substring(0, index);
                String value = input.substring(index + 1);
                for (String v : StringUtils.commaDelimitedListToStringArray(value)) {
                    name = StringUtils.uriDecode(name, charset);
                    if (StringUtils.hasText(name)) {
                        output.add(name, StringUtils.uriDecode(v, charset));
                    }
                }
                return;
            }
            String name2 = StringUtils.uriDecode(input, charset);
            if (StringUtils.hasText(name2)) {
                output.add(input, "");
            }
        }
    }

    public static PathContainer subPath(PathContainer container, int fromIndex, int toIndex) {
        List<PathContainer.Element> elements = container.elements();
        if (fromIndex == 0 && toIndex == elements.size()) {
            return container;
        }
        if (fromIndex == toIndex) {
            return EMPTY_PATH;
        }
        Assert.isTrue(fromIndex >= 0 && fromIndex < elements.size(), () -> {
            return "Invalid fromIndex: " + fromIndex;
        });
        Assert.isTrue(toIndex >= 0 && toIndex <= elements.size(), () -> {
            return "Invalid toIndex: " + toIndex;
        });
        Assert.isTrue(fromIndex < toIndex, () -> {
            return "fromIndex: " + fromIndex + " should be < toIndex " + toIndex;
        });
        List<PathContainer.Element> subList = elements.subList(fromIndex, toIndex);
        String path = (String) subList.stream().map((v0) -> {
            return v0.value();
        }).collect(Collectors.joining(""));
        return new DefaultPathContainer(path, subList);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/DefaultPathContainer$DefaultPathSegment.class */
    public static class DefaultPathSegment implements PathContainer.PathSegment {
        private final String value;
        private final String valueToMatch;
        private final char[] valueToMatchAsChars;
        private final MultiValueMap<String, String> parameters;

        public DefaultPathSegment(String value, String valueToMatch, MultiValueMap<String, String> params) {
            Assert.isTrue(!value.contains("/"), () -> {
                return "Invalid path segment value: " + value;
            });
            this.value = value;
            this.valueToMatch = valueToMatch;
            this.valueToMatchAsChars = valueToMatch.toCharArray();
            this.parameters = CollectionUtils.unmodifiableMultiValueMap(params);
        }

        @Override // org.springframework.http.server.PathContainer.Element
        public String value() {
            return this.value;
        }

        @Override // org.springframework.http.server.PathContainer.PathSegment
        public String valueToMatch() {
            return this.valueToMatch;
        }

        @Override // org.springframework.http.server.PathContainer.PathSegment
        public char[] valueToMatchAsChars() {
            return this.valueToMatchAsChars;
        }

        @Override // org.springframework.http.server.PathContainer.PathSegment
        public MultiValueMap<String, String> parameters() {
            return this.parameters;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            return this.value.equals(((DefaultPathSegment) other).value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return "[value='" + this.value + "']";
        }
    }
}