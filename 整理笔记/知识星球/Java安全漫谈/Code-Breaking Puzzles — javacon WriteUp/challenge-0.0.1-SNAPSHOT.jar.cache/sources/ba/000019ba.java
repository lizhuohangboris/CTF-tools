package org.springframework.boot.jdbc.metadata;

import org.apache.commons.dbcp2.BasicDataSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jdbc/metadata/CommonsDbcp2DataSourcePoolMetadata.class */
public class CommonsDbcp2DataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<BasicDataSource> {
    public CommonsDbcp2DataSourcePoolMetadata(BasicDataSource dataSource) {
        super(dataSource);
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getActive() {
        return Integer.valueOf(getDataSource().getNumActive());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMax() {
        return Integer.valueOf(getDataSource().getMaxTotal());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMin() {
        return Integer.valueOf(getDataSource().getMinIdle());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public String getValidationQuery() {
        return getDataSource().getValidationQuery();
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Boolean getDefaultAutoCommit() {
        return getDataSource().getDefaultAutoCommit();
    }
}