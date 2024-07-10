package org.springframework.web.servlet.view.feed;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/feed/AbstractRssFeedView.class */
public abstract class AbstractRssFeedView extends AbstractFeedView<Channel> {
    protected abstract List<Item> buildFeedItems(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;

    @Override // org.springframework.web.servlet.view.feed.AbstractFeedView
    protected /* bridge */ /* synthetic */ void buildFeedEntries(Map map, Channel channel, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        buildFeedEntries2((Map<String, Object>) map, channel, httpServletRequest, httpServletResponse);
    }

    public AbstractRssFeedView() {
        setContentType(MediaType.APPLICATION_RSS_XML_VALUE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.feed.AbstractFeedView
    public Channel newFeed() {
        return new Channel("rss_2.0");
    }

    /* renamed from: buildFeedEntries  reason: avoid collision after fix types in other method */
    protected final void buildFeedEntries2(Map<String, Object> model, Channel channel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Item> items = buildFeedItems(model, request, response);
        channel.setItems(items);
    }
}