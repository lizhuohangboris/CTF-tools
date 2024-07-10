package org.thymeleaf.templateresource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresource/UrlTemplateResource.class */
public final class UrlTemplateResource implements ITemplateResource, Serializable {
    private final URL url;
    private final String characterEncoding;

    public UrlTemplateResource(String path, String characterEncoding) throws MalformedURLException {
        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        this.url = new URL(path);
        this.characterEncoding = characterEncoding;
    }

    public UrlTemplateResource(URL url, String characterEncoding) {
        Validate.notNull(url, "Resource URL cannot be null");
        this.url = url;
        this.characterEncoding = characterEncoding;
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getDescription() {
        return this.url.toString();
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getBaseName() {
        return TemplateResourceUtils.computeBaseName(TemplateResourceUtils.cleanPath(this.url.getPath()));
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public Reader reader() throws IOException {
        InputStream inputStream = inputStream();
        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }
        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
    }

    private InputStream inputStream() throws IOException {
        URLConnection connection = this.url.openConnection();
        if (connection.getClass().getSimpleName().startsWith("JNLP")) {
            connection.setUseCaches(true);
        }
        try {
            InputStream inputStream = connection.getInputStream();
            return inputStream;
        } catch (IOException e) {
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection) connection).disconnect();
            }
            throw e;
        }
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public ITemplateResource relative(String relativeLocation) {
        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");
        try {
            URL relativeURL = new URL(this.url, relativeLocation.charAt(0) == '/' ? relativeLocation.substring(1) : relativeLocation);
            return new UrlTemplateResource(relativeURL, this.characterEncoding);
        } catch (MalformedURLException e) {
            throw new TemplateInputException("Could not create relative URL resource for resource \"" + getDescription() + "\" and relative location \"" + relativeLocation + "\"", e);
        }
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public boolean exists() {
        File file;
        try {
            String protocol = this.url.getProtocol();
            if ("file".equals(protocol)) {
                try {
                    file = new File(toURI(this.url).getSchemeSpecificPart());
                } catch (URISyntaxException e) {
                    file = new File(this.url.getFile());
                }
                return file.exists();
            }
            URLConnection connection = this.url.openConnection();
            if (connection.getClass().getSimpleName().startsWith("JNLP")) {
                connection.setUseCaches(true);
            }
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod(WebContentGenerator.METHOD_HEAD);
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == 200) {
                    return true;
                }
                if (responseCode == 404) {
                    return false;
                }
                if (httpConnection.getContentLength() >= 0) {
                    return true;
                }
                httpConnection.disconnect();
                return false;
            } else if (connection.getContentLength() >= 0) {
                return true;
            } else {
                InputStream is = inputStream();
                is.close();
                return true;
            }
        } catch (IOException e2) {
            return false;
        }
    }

    private static URI toURI(URL url) throws URISyntaxException {
        String location = url.toString();
        if (location.indexOf(32) == -1) {
            return new URI(location);
        }
        return new URI(StringUtils.replace(location, " ", "%20"));
    }
}