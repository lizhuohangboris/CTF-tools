package org.springframework.boot.jdbc.metadata;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.springframework.beans.DirectFieldAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jdbc/metadata/HikariDataSourcePoolMetadata.class */
public class HikariDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<HikariDataSource> {
    public HikariDataSourcePoolMetadata(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getActive() {
        try {
            return Integer.valueOf(getHikariPool().getActiveConnections());
        } catch (Exception e) {
            return null;
        }
    }

    private HikariPool getHikariPool() {
        return (HikariPool) new DirectFieldAccessor(getDataSource()).getPropertyValue("pool");
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMax() {
        return Integer.valueOf(getDataSource().getMaximumPoolSize());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMin() {
        return Integer.valueOf(getDataSource().getMinimumIdle());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public String getValidationQuery() {
        return getDataSource().getConnectionTestQuery();
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Boolean getDefaultAutoCommit() {
        return Boolean.valueOf(getDataSource().isAutoCommit());
    }
}