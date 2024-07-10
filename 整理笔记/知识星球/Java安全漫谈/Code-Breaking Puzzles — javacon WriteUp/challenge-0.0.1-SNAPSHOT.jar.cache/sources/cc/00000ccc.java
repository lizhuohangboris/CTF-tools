package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.apache.tomcat.util.http.fileupload.util.Closeable;
import org.apache.tomcat.util.http.fileupload.util.FileItemHeadersImpl;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.apache.tomcat.util.http.fileupload.util.Streams;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase.class */
public abstract class FileUploadBase {
    public static final String CONTENT_TYPE = "Content-type";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String FORM_DATA = "form-data";
    public static final String ATTACHMENT = "attachment";
    public static final String MULTIPART = "multipart/";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String MULTIPART_MIXED = "multipart/mixed";
    private long sizeMax = -1;
    private long fileSizeMax = -1;
    private String headerEncoding;
    private ProgressListener listener;

    public abstract FileItemFactory getFileItemFactory();

    public abstract void setFileItemFactory(FileItemFactory fileItemFactory);

    public static final boolean isMultipartContent(RequestContext ctx) {
        String contentType = ctx.getContentType();
        if (contentType != null && contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART)) {
            return true;
        }
        return false;
    }

    public long getSizeMax() {
        return this.sizeMax;
    }

    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }

    public long getFileSizeMax() {
        return this.fileSizeMax;
    }

    public void setFileSizeMax(long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }

    public String getHeaderEncoding() {
        return this.headerEncoding;
    }

    public void setHeaderEncoding(String encoding) {
        this.headerEncoding = encoding;
    }

    public FileItemIterator getItemIterator(RequestContext ctx) throws FileUploadException, IOException {
        try {
            return new FileItemIteratorImpl(ctx);
        } catch (FileUploadIOException e) {
            throw ((FileUploadException) e.getCause());
        }
    }

    public List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException {
        List<FileItem> items = new ArrayList<>();
        boolean successful = false;
        try {
            try {
                FileItemIterator iter = getItemIterator(ctx);
                FileItemFactory fac = getFileItemFactory();
                if (fac == null) {
                    throw new NullPointerException("No FileItemFactory has been set.");
                }
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    String fileName = ((FileItemIteratorImpl.FileItemStreamImpl) item).name;
                    FileItem fileItem = fac.createItem(item.getFieldName(), item.getContentType(), item.isFormField(), fileName);
                    items.add(fileItem);
                    try {
                        Streams.copy(item.openStream(), fileItem.getOutputStream(), true);
                        FileItemHeaders fih = item.getHeaders();
                        fileItem.setHeaders(fih);
                    } catch (FileUploadIOException e) {
                        throw ((FileUploadException) e.getCause());
                    } catch (IOException e2) {
                        throw new IOFileUploadException(String.format("Processing of %s request failed. %s", "multipart/form-data", e2.getMessage()), e2);
                    }
                }
                successful = true;
                return items;
            } catch (FileUploadIOException e3) {
                throw ((FileUploadException) e3.getCause());
            } catch (IOException e4) {
                throw new FileUploadException(e4.getMessage(), e4);
            }
        } finally {
            if (!successful) {
                for (FileItem fileItem2 : items) {
                    try {
                        fileItem2.delete();
                    } catch (Exception e5) {
                    }
                }
            }
        }
    }

    public Map<String, List<FileItem>> parseParameterMap(RequestContext ctx) throws FileUploadException {
        List<FileItem> items = parseRequest(ctx);
        Map<String, List<FileItem>> itemsMap = new HashMap<>(items.size());
        for (FileItem fileItem : items) {
            String fieldName = fileItem.getFieldName();
            List<FileItem> mappedItems = itemsMap.get(fieldName);
            if (mappedItems == null) {
                mappedItems = new ArrayList<>();
                itemsMap.put(fieldName, mappedItems);
            }
            mappedItems.add(fileItem);
        }
        return itemsMap;
    }

    protected byte[] getBoundary(String contentType) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        Map<String, String> params = parser.parse(contentType, new char[]{';', ','});
        String boundaryStr = params.get("boundary");
        if (boundaryStr == null) {
            return null;
        }
        byte[] boundary = boundaryStr.getBytes(StandardCharsets.ISO_8859_1);
        return boundary;
    }

    protected String getFileName(FileItemHeaders headers) {
        return getFileName(headers.getHeader(CONTENT_DISPOSITION));
    }

    private String getFileName(String pContentDisposition) {
        String fileName = null;
        if (pContentDisposition != null) {
            String cdl = pContentDisposition.toLowerCase(Locale.ENGLISH);
            if (cdl.startsWith(FORM_DATA) || cdl.startsWith(ATTACHMENT)) {
                ParameterParser parser = new ParameterParser();
                parser.setLowerCaseNames(true);
                Map<String, String> params = parser.parse(pContentDisposition, ';');
                if (params.containsKey("filename")) {
                    String fileName2 = params.get("filename");
                    fileName = fileName2 != null ? fileName2.trim() : "";
                }
            }
        }
        return fileName;
    }

    protected String getFieldName(FileItemHeaders headers) {
        return getFieldName(headers.getHeader(CONTENT_DISPOSITION));
    }

    private String getFieldName(String pContentDisposition) {
        String fieldName = null;
        if (pContentDisposition != null && pContentDisposition.toLowerCase(Locale.ENGLISH).startsWith(FORM_DATA)) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);
            Map<String, String> params = parser.parse(pContentDisposition, ';');
            fieldName = params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }
        return fieldName;
    }

    protected FileItemHeaders getParsedHeaders(String headerPart) {
        char c;
        int len = headerPart.length();
        FileItemHeadersImpl headers = newFileItemHeaders();
        int start = 0;
        while (true) {
            int end = parseEndOfLine(headerPart, start);
            if (start != end) {
                StringBuilder header = new StringBuilder(headerPart.substring(start, end));
                while (true) {
                    start = end + 2;
                    if (start < len) {
                        int nonWs = start;
                        while (nonWs < len && ((c = headerPart.charAt(nonWs)) == ' ' || c == '\t')) {
                            nonWs++;
                        }
                        if (nonWs == start) {
                            break;
                        }
                        end = parseEndOfLine(headerPart, nonWs);
                        header.append(" ").append(headerPart.substring(nonWs, end));
                    }
                }
                parseHeaderLine(headers, header.toString());
            } else {
                return headers;
            }
        }
    }

    protected FileItemHeadersImpl newFileItemHeaders() {
        return new FileItemHeadersImpl();
    }

    private int parseEndOfLine(String headerPart, int end) {
        int i = end;
        while (true) {
            int index = i;
            int offset = headerPart.indexOf(13, index);
            if (offset == -1 || offset + 1 >= headerPart.length()) {
                break;
            } else if (headerPart.charAt(offset + 1) == '\n') {
                return offset;
            } else {
                i = offset + 1;
            }
        }
        throw new IllegalStateException("Expected headers to be terminated by an empty line.");
    }

    private void parseHeaderLine(FileItemHeadersImpl headers, String header) {
        int colonOffset = header.indexOf(58);
        if (colonOffset == -1) {
            return;
        }
        String headerName = header.substring(0, colonOffset).trim();
        String headerValue = header.substring(header.indexOf(58) + 1).trim();
        headers.addHeader(headerName, headerValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase$FileItemIteratorImpl.class */
    public class FileItemIteratorImpl implements FileItemIterator {
        private final MultipartStream multi;
        private final MultipartStream.ProgressNotifier notifier;
        private final byte[] boundary;
        private FileItemStreamImpl currentItem;
        private String currentFieldName;
        private boolean skipPreamble;
        private boolean itemValid;
        private boolean eof;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase$FileItemIteratorImpl$FileItemStreamImpl.class */
        public class FileItemStreamImpl implements FileItemStream {
            private final String contentType;
            private final String fieldName;
            private final String name;
            private final boolean formField;
            private final InputStream stream;
            private FileItemHeaders headers;

            FileItemStreamImpl(String pName, String pFieldName, String pContentType, boolean pFormField, long pContentLength) throws IOException {
                this.name = pName;
                this.fieldName = pFieldName;
                this.contentType = pContentType;
                this.formField = pFormField;
                final MultipartStream.ItemInputStream itemStream = FileItemIteratorImpl.this.multi.newInputStream();
                InputStream istream = itemStream;
                if (FileUploadBase.this.fileSizeMax != -1) {
                    if (pContentLength != -1 && pContentLength > FileUploadBase.this.fileSizeMax) {
                        FileSizeLimitExceededException e = new FileSizeLimitExceededException(String.format("The field %s exceeds its maximum permitted size of %s bytes.", this.fieldName, Long.valueOf(FileUploadBase.this.fileSizeMax)), pContentLength, FileUploadBase.this.fileSizeMax);
                        e.setFileName(pName);
                        e.setFieldName(pFieldName);
                        throw new FileUploadIOException(e);
                    }
                    istream = new LimitedInputStream(istream, FileUploadBase.this.fileSizeMax) { // from class: org.apache.tomcat.util.http.fileupload.FileUploadBase.FileItemIteratorImpl.FileItemStreamImpl.1
                        @Override // org.apache.tomcat.util.http.fileupload.util.LimitedInputStream
                        protected void raiseError(long pSizeMax, long pCount) throws IOException {
                            itemStream.close(true);
                            FileSizeLimitExceededException e2 = new FileSizeLimitExceededException(String.format("The field %s exceeds its maximum permitted size of %s bytes.", FileItemStreamImpl.this.fieldName, Long.valueOf(pSizeMax)), pCount, pSizeMax);
                            e2.setFieldName(FileItemStreamImpl.this.fieldName);
                            e2.setFileName(FileItemStreamImpl.this.name);
                            throw new FileUploadIOException(e2);
                        }
                    };
                }
                this.stream = istream;
            }

            @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
            public String getContentType() {
                return this.contentType;
            }

            @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
            public String getFieldName() {
                return this.fieldName;
            }

            @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
            public String getName() {
                return Streams.checkFileName(this.name);
            }

            @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
            public boolean isFormField() {
                return this.formField;
            }

            @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
            public InputStream openStream() throws IOException {
                if (((Closeable) this.stream).isClosed()) {
                    throw new FileItemStream.ItemSkippedException();
                }
                return this.stream;
            }

            void close() throws IOException {
                this.stream.close();
            }

            @Override // org.apache.tomcat.util.http.fileupload.FileItemHeadersSupport
            public FileItemHeaders getHeaders() {
                return this.headers;
            }

            @Override // org.apache.tomcat.util.http.fileupload.FileItemHeadersSupport
            public void setHeaders(FileItemHeaders pHeaders) {
                this.headers = pHeaders;
            }
        }

        FileItemIteratorImpl(RequestContext ctx) throws FileUploadException, IOException {
            InputStream input;
            if (ctx == null) {
                throw new NullPointerException("ctx parameter");
            }
            String contentType = ctx.getContentType();
            if (null == contentType || !contentType.toLowerCase(Locale.ENGLISH).startsWith(FileUploadBase.MULTIPART)) {
                throw new InvalidContentTypeException(String.format("the request doesn't contain a %s or %s stream, content type header is %s", "multipart/form-data", FileUploadBase.MULTIPART_MIXED, contentType));
            }
            long requestSize = ((UploadContext) ctx).contentLength();
            if (FileUploadBase.this.sizeMax >= 0) {
                if (requestSize != -1 && requestSize > FileUploadBase.this.sizeMax) {
                    throw new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", Long.valueOf(requestSize), Long.valueOf(FileUploadBase.this.sizeMax)), requestSize, FileUploadBase.this.sizeMax);
                }
                input = new LimitedInputStream(ctx.getInputStream(), FileUploadBase.this.sizeMax) { // from class: org.apache.tomcat.util.http.fileupload.FileUploadBase.FileItemIteratorImpl.1
                    @Override // org.apache.tomcat.util.http.fileupload.util.LimitedInputStream
                    protected void raiseError(long pSizeMax, long pCount) throws IOException {
                        FileUploadException ex = new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", Long.valueOf(pCount), Long.valueOf(pSizeMax)), pCount, pSizeMax);
                        throw new FileUploadIOException(ex);
                    }
                };
            } else {
                input = ctx.getInputStream();
            }
            String charEncoding = FileUploadBase.this.headerEncoding;
            charEncoding = charEncoding == null ? ctx.getCharacterEncoding() : charEncoding;
            this.boundary = FileUploadBase.this.getBoundary(contentType);
            if (this.boundary == null) {
                IOUtils.closeQuietly(input);
                throw new FileUploadException("the request was rejected because no multipart boundary was found");
            }
            this.notifier = new MultipartStream.ProgressNotifier(FileUploadBase.this.listener, requestSize);
            try {
                this.multi = new MultipartStream(input, this.boundary, this.notifier);
                this.multi.setHeaderEncoding(charEncoding);
                this.skipPreamble = true;
                findNextItem();
            } catch (IllegalArgumentException iae) {
                IOUtils.closeQuietly(input);
                throw new InvalidContentTypeException(String.format("The boundary specified in the %s header is too long", FileUploadBase.CONTENT_TYPE), iae);
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:29:0x00be, code lost:
            r0 = r11.this$0.getFileName(r0);
            r6 = r0.getHeader(org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE);
         */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x00db, code lost:
            if (r0 != null) goto L36;
         */
        /* JADX WARN: Code restructure failed: missing block: B:31:0x00de, code lost:
            r7 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:32:0x00e2, code lost:
            r7 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:33:0x00e3, code lost:
            r11.currentItem = new org.apache.tomcat.util.http.fileupload.FileUploadBase.FileItemIteratorImpl.FileItemStreamImpl(r11, r0, r0, r6, r7, getContentLength(r0));
            r11.currentItem.setHeaders(r0);
            r11.notifier.noteItem();
            r11.itemValid = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:34:0x0103, code lost:
            return true;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private boolean findNextItem() throws java.io.IOException {
            /*
                Method dump skipped, instructions count: 340
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.fileupload.FileUploadBase.FileItemIteratorImpl.findNextItem():boolean");
        }

        private long getContentLength(FileItemHeaders pHeaders) {
            try {
                return Long.parseLong(pHeaders.getHeader(FileUploadBase.CONTENT_LENGTH));
            } catch (Exception e) {
                return -1L;
            }
        }

        @Override // org.apache.tomcat.util.http.fileupload.FileItemIterator
        public boolean hasNext() throws FileUploadException, IOException {
            if (this.eof) {
                return false;
            }
            if (this.itemValid) {
                return true;
            }
            try {
                return findNextItem();
            } catch (FileUploadIOException e) {
                throw ((FileUploadException) e.getCause());
            }
        }

        @Override // org.apache.tomcat.util.http.fileupload.FileItemIterator
        public FileItemStream next() throws FileUploadException, IOException {
            if (this.eof || (!this.itemValid && !hasNext())) {
                throw new NoSuchElementException();
            }
            this.itemValid = false;
            return this.currentItem;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase$FileUploadIOException.class */
    public static class FileUploadIOException extends IOException {
        private static final long serialVersionUID = -3082868232248803474L;

        public FileUploadIOException() {
        }

        public FileUploadIOException(String message, Throwable cause) {
            super(message, cause);
        }

        public FileUploadIOException(String message) {
            super(message);
        }

        public FileUploadIOException(Throwable cause) {
            super(cause);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase$InvalidContentTypeException.class */
    public static class InvalidContentTypeException extends FileUploadException {
        private static final long serialVersionUID = -9073026332015646668L;

        public InvalidContentTypeException() {
        }

        public InvalidContentTypeException(String message) {
            super(message);
        }

        public InvalidContentTypeException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase$IOFileUploadException.class */
    public static class IOFileUploadException extends FileUploadException {
        private static final long serialVersionUID = -5858565745868986701L;

        public IOFileUploadException() {
        }

        public IOFileUploadException(String message, Throwable cause) {
            super(message, cause);
        }

        public IOFileUploadException(String message) {
            super(message);
        }

        public IOFileUploadException(Throwable cause) {
            super(cause);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase$SizeException.class */
    public static abstract class SizeException extends FileUploadException {
        private static final long serialVersionUID = -8776225574705254126L;
        private final long actual;
        private final long permitted;

        protected SizeException(String message, long actual, long permitted) {
            super(message);
            this.actual = actual;
            this.permitted = permitted;
        }

        public long getActualSize() {
            return this.actual;
        }

        public long getPermittedSize() {
            return this.permitted;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase$SizeLimitExceededException.class */
    public static class SizeLimitExceededException extends SizeException {
        private static final long serialVersionUID = -2474893167098052828L;

        public SizeLimitExceededException(String message, long actual, long permitted) {
            super(message, actual, permitted);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase$FileSizeLimitExceededException.class */
    public static class FileSizeLimitExceededException extends SizeException {
        private static final long serialVersionUID = 8150776562029630058L;
        private String fileName;
        private String fieldName;

        public FileSizeLimitExceededException(String message, long actual, long permitted) {
            super(message, actual, permitted);
        }

        public String getFileName() {
            return this.fileName;
        }

        public void setFileName(String pFileName) {
            this.fileName = pFileName;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public void setFieldName(String pFieldName) {
            this.fieldName = pFieldName;
        }
    }

    public ProgressListener getProgressListener() {
        return this.listener;
    }

    public void setProgressListener(ProgressListener pListener) {
        this.listener = pListener;
    }
}