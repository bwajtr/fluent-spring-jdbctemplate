/*
 * Copyright 2016 Bretislav Wajtr
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

package com.clevergang.jdbc;

import com.clevergang.jdbc.fluent.FluentQueryBuilder;
import com.clevergang.jdbc.fluent.FluentUpdateBuilder;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

/**
 * This interface is an extension of default Spring NamedParameterJdbcOperations adding
 * new methods, which allow "fluent" style of interacting with a database. Basically, the intent
 * of this interface and its methods is to simplify and shorten day to day operations with JDBC.
 *
 * @author Bretislav Wajtr
 */
public interface FluentNamedParameterJdbcOperations extends NamedParameterJdbcOperations {

    /**
     * Creates "fluent" style builder for querying the database (SELECT operations). The builder
     * provides methods for binding the parameters, provides several methods for configuration
     * of the mapping from SQL to Java classes and the final execution of the statement. Example usage:
     * <pre>{@code
     *  UserBean user = jdbc.query("SELECT * FROM users WHERE id = :id")
     *                      .bind("id", 1)
     *                      .fetchOne(UserBean.class);
     * }</pre>
     *
     * @param sql SQL query to execute
     * @return Returns builder, which provides methods for binding parameters and for
     * execution of the query
     * @see FluentQueryBuilder
     */
    FluentQueryBuilder query(String sql);

    /**
     * Creates "fluent" style builder for execution of statements which update the database state (INSERT, UPDATE, DELETE operations). The
     * builder provides methods for binding the parameters and also provides several methods for execution
     * of the final statement. There is a possibility to return generated keys where appropriate. Example usage:
     * <pre>{@code
     * jdbc.update("UPDATE users SET name = :name WHERE id = :id")
     *     .bind("name", "Alex")
     *     .bind("id", 2)
     *     .execute();
     * }</pre>
     *
     * @param sql SQL statement to execute
     * @return Returns builder, which provides methods for binding parameters and for
     * execution of the statement
     * @see FluentUpdateBuilder
     */
    FluentUpdateBuilder update(String sql);

}
