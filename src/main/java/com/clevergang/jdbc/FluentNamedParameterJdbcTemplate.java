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
 * @author Bretislav Wajtr
 */
public class FluentNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate implements FluentNamedParameterJdbcOperations {

    public FluentNamedParameterJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

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
