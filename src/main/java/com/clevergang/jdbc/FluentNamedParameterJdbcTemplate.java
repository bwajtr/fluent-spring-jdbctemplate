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
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * JDBC template class with a basic set of JDBC operations, allowing the use
 * of named parameters rather than traditional '?' placeholders. Additionally, this
 * template class provides methods for building the SQL queries and their execution
 * in fluent, "java8-like" style.
 *
 * <p>This class is an extension of commonly used NamedParameterJdbcTemplate. Therefore it also delegates
 * to a wrapped {@link #getJdbcOperations() JdbcTemplate} once the substitution from named parameters to
 * JDBC style '?' placeholders is done at execution time. It also allows for expanding a {@link java.util.List}
 * of values to the appropriate number of placeholders.
 *
 * <p>The underlying {@link org.springframework.jdbc.core.JdbcTemplate} is
 * exposed to allow for convenient access to the traditional
 * {@link org.springframework.jdbc.core.JdbcTemplate} methods.
 *
 * <p><b>NOTE: An instance of this class is thread-safe once configured.</b>
 *
 * @author Bretislav Wajtr
 * @see FluentNamedParameterJdbcOperations
 * @see org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 * @see org.springframework.jdbc.core.JdbcTemplate
 */
public class FluentNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate implements FluentNamedParameterJdbcOperations {

    /**
     * Create a new FluentNamedParameterJdbcTemplate for the given {@link DataSource}.
     * <p>Creates a classic Spring {@link org.springframework.jdbc.core.JdbcTemplate} and wraps it.
     * @param dataSource the JDBC DataSource to access
     */
    public FluentNamedParameterJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Create a new FluentNamedParameterJdbcTemplate for the given classic
     * Spring {@link org.springframework.jdbc.core.JdbcTemplate}.
     * @param classicJdbcTemplate the classic Spring JdbcTemplate to wrap
     */
    public FluentNamedParameterJdbcTemplate(JdbcOperations classicJdbcTemplate) {
        super(classicJdbcTemplate);
    }

    @Override
    public FluentQueryBuilder query(String sql) {
        return new FluentQueryBuilder(sql, this);
    }

    @Override
    public FluentUpdateBuilder update(String sql) {
        return new FluentUpdateBuilder(sql, this);
    }


}
