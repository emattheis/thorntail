/**
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.jpa.mysql;

import org.wildfly.swarm.config.JPA;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.spi.api.Fraction;
import org.wildfly.swarm.spi.api.annotations.Configuration;
import org.wildfly.swarm.spi.api.annotations.Default;

@Configuration(
        marshal = true,
        extension = "org.jboss.as.jpa",
        parserFactoryClassName = "org.wildfly.swarm.jpa.mysql.ParserFactory"
)
public class MySQLJPAFraction extends JPA<MySQLJPAFraction> implements Fraction {

    public MySQLJPAFraction() {
    }

    @Default
    public static MySQLJPAFraction createDefaultFraction() {
        return new MySQLJPAFraction()
                .defaultExtendedPersistenceInheritance(DefaultExtendedPersistenceInheritance.DEEP);

    }

    public MySQLJPAFraction inhibitDefaultDatasource() {
        this.inhibitDefaultDatasource = true;
        return this;
    }

    @Override
    public void initialize(Fraction.InitContext initContext) {
        if (!inhibitDefaultDatasource) {
            final DatasourcesFraction datasources = new DatasourcesFraction()
                    .jdbcDriver("mysql", (d) -> {
                        d.driverClassName("com.mysql.cj.jdbc.Driver");
                        d.xaDatasourceClass("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
                        d.driverModuleName("com.mysql.jdbc.driver");
                    })
                    .dataSource("ExampleDS", (ds) -> {
                        ds.driverName("mysql");
                        ds.connectionUrl("jdbc:mysql://localhost:3306/test");
                        ds.userName("root");
                        ds.password("password");
                    });

            initContext.fraction(datasources);
            System.err.println("setting default Datasource to ExampleDS");
            defaultDatasource("jboss/datasources/ExampleDS");
        }
    }

    private boolean inhibitDefaultDatasource = false;
}