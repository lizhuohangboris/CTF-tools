package org.springframework.http.converter.feed;

import com.rometools.rome.feed.rss.Channel;
import org.springframework.http.MediaType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/feed/RssChannelHttpMessageConverter.class */
public class RssChannelHttpMessageConverter extends AbstractWireFeedHttpMessageConverter<Channel> {
    public RssChannelHttpMessageConverter() {
        super(MediaType.APPLICATION_RSS_XML);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        return Channel.class.isAssignableFrom(clazz);
    }
}