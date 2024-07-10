package org.springframework.web.servlet.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/AppCacheManifestTransformer.class */
public class AppCacheManifestTransformer extends ResourceTransformerSupport {
    private final String fileExtension;
    private static final String MANIFEST_HEADER = "CACHE MANIFEST";
    private static final String CACHE_HEADER = "CACHE:";
    private static final Collection<String> MANIFEST_SECTION_HEADERS = Arrays.asList(MANIFEST_HEADER, "NETWORK:", "FALLBACK:", CACHE_HEADER);
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Log logger = LogFactory.getLog(AppCacheManifestTransformer.class);

    public AppCacheManifestTransformer() {
        this("appcache");
    }

    public AppCacheManifestTransformer(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override // org.springframework.web.servlet.resource.ResourceTransformer
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain chain) throws IOException {
        Resource resource2 = chain.transform(request, resource);
        if (!this.fileExtension.equals(StringUtils.getFilenameExtension(resource2.getFilename()))) {
            return resource2;
        }
        byte[] bytes = FileCopyUtils.copyToByteArray(resource2.getInputStream());
        String content = new String(bytes, DEFAULT_CHARSET);
        if (!content.startsWith(MANIFEST_HEADER)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Skipping " + resource2 + ": Manifest does not start with 'CACHE MANIFEST'");
            }
            return resource2;
        }
        Scanner scanner = new Scanner(content);
        LineInfo previous = null;
        LineAggregator aggregator = new LineAggregator(resource2, content);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            LineInfo current = new LineInfo(line, previous);
            LineOutput lineOutput = processLine(current, request, resource2, chain);
            aggregator.add(lineOutput);
            previous = current;
        }
        return aggregator.createResource();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] getResourceBytes(Resource resource) throws IOException {
        return FileCopyUtils.copyToByteArray(resource.getInputStream());
    }

    private LineOutput processLine(LineInfo info, HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) {
        if (!info.isLink()) {
            return new LineOutput(info.getLine(), null);
        }
        Resource appCacheResource = transformerChain.getResolverChain().resolveResource(null, info.getLine(), Collections.singletonList(resource));
        String path = info.getLine();
        String absolutePath = toAbsolutePath(path, request);
        String newPath = resolveUrlPath(absolutePath, request, resource, transformerChain);
        return new LineOutput(newPath != null ? newPath : path, appCacheResource);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/AppCacheManifestTransformer$LineInfo.class */
    public static class LineInfo {
        private final String line;
        private final boolean cacheSection;
        private final boolean link;

        public LineInfo(String line, @Nullable LineInfo previous) {
            this.line = line;
            this.cacheSection = initCacheSectionFlag(line, previous);
            this.link = iniLinkFlag(line, this.cacheSection);
        }

        private static boolean initCacheSectionFlag(String line, @Nullable LineInfo previousLine) {
            if (AppCacheManifestTransformer.MANIFEST_SECTION_HEADERS.contains(line.trim())) {
                return line.trim().equals(AppCacheManifestTransformer.CACHE_HEADER);
            }
            if (previousLine != null) {
                return previousLine.isCacheSection();
            }
            throw new IllegalStateException("Manifest does not start with CACHE MANIFEST: " + line);
        }

        private static boolean iniLinkFlag(String line, boolean isCacheSection) {
            return (!isCacheSection || !StringUtils.hasText(line) || line.startsWith("#") || line.startsWith("//") || hasScheme(line)) ? false : true;
        }

        private static boolean hasScheme(String line) {
            int index = line.indexOf(58);
            return line.startsWith("//") || (index > 0 && !line.substring(0, index).contains("/"));
        }

        public String getLine() {
            return this.line;
        }

        public boolean isCacheSection() {
            return this.cacheSection;
        }

        public boolean isLink() {
            return this.link;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/AppCacheManifestTransformer$LineOutput.class */
    public static class LineOutput {
        private final String line;
        @Nullable
        private final Resource resource;

        public LineOutput(String line, @Nullable Resource resource) {
            this.line = line;
            this.resource = resource;
        }

        public String getLine() {
            return this.line;
        }

        @Nullable
        public Resource getResource() {
            return this.resource;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/AppCacheManifestTransformer$LineAggregator.class */
    private static class LineAggregator {
        private final StringWriter writer = new StringWriter();
        private final ByteArrayOutputStream baos;
        private final Resource resource;

        public LineAggregator(Resource resource, String content) {
            this.resource = resource;
            this.baos = new ByteArrayOutputStream(content.length());
        }

        public void add(LineOutput lineOutput) throws IOException {
            this.writer.write(lineOutput.getLine() + "\n");
            byte[] bytes = lineOutput.getResource() != null ? DigestUtils.md5Digest(AppCacheManifestTransformer.getResourceBytes(lineOutput.getResource())) : lineOutput.getLine().getBytes(AppCacheManifestTransformer.DEFAULT_CHARSET);
            this.baos.write(bytes);
        }

        public TransformedResource createResource() {
            String hash = DigestUtils.md5DigestAsHex(this.baos.toByteArray());
            this.writer.write("\n# Hash: " + hash);
            byte[] bytes = this.writer.toString().getBytes(AppCacheManifestTransformer.DEFAULT_CHARSET);
            return new TransformedResource(this.resource, bytes);
        }
    }
}