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

import griffon.transform.DataSourceAware;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@DataSourceAware
public class DataSourceBean {
    public void loadDatabase() {
        withConnection(new ConnectionCallback<Void>() {
            @Override
            public Void handle(@Nonnull String dataSourceName, @Nonnull DataSource dataSource, @Nonnull Connection connection) throws SQLException {
                String[] data = new String[]{
                    "1, Danno, Ferrin",
                    "2, Andres, Almiray",
                    "3, James, Williams",
                    "4, Guillaume, Laforge",
                    "5, Jim, Shingler",
                    "6, Alexander, Klein",
                    "7, Rene, Groeschke"
                };
                for (String str : data) {
                    insert(str.split(","), connection);
                }
                return null;
            }
        });
    }

    private void insert(String[] data, Connection conn) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement("INSERT INTO people(id, name, lastname) values(?, ?, ?)");
        stmnt.setInt(1, Integer.parseInt(data[0]));
        stmnt.setString(2, data[1]);
        stmnt.setString(3, data[2]);
        stmnt.execute();
        stmnt.close();
    }
}
