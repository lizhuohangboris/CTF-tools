package org.springframework.web.servlet.view.feed;

import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/feed/AbstractAtomFeedView.class */
public abstract class AbstractAtomFeedView extends AbstractFeedView<Feed> {
    public static final String DEFAULT_FEED_TYPE = "atom_1.0";
    private String feedType = DEFAULT_FEED_TYPE;

    protected abstract List<Entry> buildFeedEntries(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;

    @Override // org.springframework.web.servlet.view.feed.AbstractFeedView
    protected /* bridge */ /* synthetic */ void buildFeedEntries(Map map, Feed feed, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        buildFeedEntries2((Map<String, Object>) map, feed, httpServletRequest, httpServletResponse);
    }

    public AbstractAtomFeedView() {
        setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.feed.AbstractFeedView
    public Feed newFeed() {
        return new Feed(this.feedType);
    }

    /* renamed from: buildFeedEntries  reason: avoid collision after fix types in other method */
    protected final void buildFeedEntries2(Map<String, Object> model, Feed feed, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Entry> entries = buildFeedEntries(model, request, response);
        feed.setEntries(entries);
    }
}