package org.springframework.web.servlet.view.feed;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.WireFeedOutput;
import java.io.OutputStreamWriter;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/feed/AbstractFeedView.class */
public abstract class AbstractFeedView<T extends WireFeed> extends AbstractView {
    protected abstract T newFeed();

    protected abstract void buildFeedEntries(Map<String, Object> map, T t, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;

    @Override // org.springframework.web.servlet.view.AbstractView
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        T wireFeed = newFeed();
        buildFeedMetadata(model, wireFeed, request);
        buildFeedEntries(model, wireFeed, request, response);
        setResponseContentType(request, response);
        if (!StringUtils.hasText(wireFeed.getEncoding())) {
            wireFeed.setEncoding(UriEscape.DEFAULT_ENCODING);
        }
        WireFeedOutput feedOutput = new WireFeedOutput();
        ServletOutputStream out = response.getOutputStream();
        feedOutput.output(wireFeed, new OutputStreamWriter(out, wireFeed.getEncoding()));
        out.flush();
    }

    protected void buildFeedMetadata(Map<String, Object> model, T feed, HttpServletRequest request) {
    }
}