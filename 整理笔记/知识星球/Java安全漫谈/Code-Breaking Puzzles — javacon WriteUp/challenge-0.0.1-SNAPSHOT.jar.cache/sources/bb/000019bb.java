package org.springframework.boot.jdbc.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jdbc/metadata/CompositeDataSourcePoolMetadataProvider.class */
public class CompositeDataSourcePoolMetadataProvider implements DataSourcePoolMetadataProvider {
    private final List<DataSourcePoolMetadataProvider> providers;

    public CompositeDataSourcePoolMetadataProvider(Collection<? extends DataSourcePoolMetadataProvider> providers) {
        List<DataSourcePoolMetadataProvider> emptyList;
        if (providers != null) {
            emptyList = Collections.unmodifiableList(new ArrayList(providers));
        } else {
            emptyList = Collections.emptyList();
        }
        this.providers = emptyList;
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider
    public DataSourcePoolMetadata getDataSourcePoolMetadata(DataSource dataSource) {
        for (DataSourcePoolMetadataProvider provider : this.providers) {
            DataSourcePoolMetadata metadata = provider.getDataSourcePoolMetadata(dataSource);
            if (metadata != null) {
                return metadata;
            }
        }
        return null;
    }
}