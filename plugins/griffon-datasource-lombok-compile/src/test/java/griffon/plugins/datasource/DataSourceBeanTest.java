/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.plugins.datasource;

import griffon.core.test.GriffonUnitRule;
import griffon.inject.BindTo;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class DataSourceBeanTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Inject
    DataSourceHandler dataSourceHandler;

    @Singleton
    @BindTo(DataSourceBean.class)
    private DataSourceBean dataSourceBean = new DataSourceBean();

    @Test
    public void dataSourceBeanCanLoadDatabase() {
        // given:
        assertEquals(0, datasetSize("people"));

        // when:
        dataSourceBean.loadDatabase();

        // then:
        assertEquals(7, datasetSize("people"));
    }

    @SuppressWarnings("ConstantConditions")
    private int datasetSize(final String datasetName) {
        return dataSourceHandler.withConnection(new ConnectionCallback<Integer>() {
            @Override
            public Integer handle(@Nonnull String dataSourceName, @Nonnull DataSource dataSource, @Nonnull Connection connection) throws SQLException {
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("SELECT COUNT(id) FROM " + datasetName);
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }
}
