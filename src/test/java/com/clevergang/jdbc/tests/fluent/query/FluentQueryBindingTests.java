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

package com.clevergang.jdbc.tests.fluent.query;

import com.clevergang.jdbc.FluentNamedParameterJdbcTemplate;
import com.clevergang.jdbc.tests.TestSpringContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * The very basic tests of FluentQueryBuilder - testing of the various forms of .bind() method
 *
 * @author Bretislav Wajtr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestSpringContext.class})
@Transactional
public class FluentQueryBindingTests {

    @Autowired
    private FluentNamedParameterJdbcTemplate jdbc;

    @Test
    @Rollback
    public void testBindSingleNumberParameter() {
        // original code
        String query = "SELECT name FROM users WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", 1);
        String userName = jdbc.queryForObject(query, params, String.class);

        Assert.assertThat(userName, equalTo("mkyong"));

        // fluent code
        String userName2 = jdbc.query("SELECT name FROM users WHERE id = :id")
                .bind("id", 1)
                .fetchOne(String.class);

        Assert.assertThat(userName2, equalTo("mkyong"));
    }

    @Test
    @Rollback
    public void testBindTwoNumberParameters() {
        // original code
        String query = "SELECT name FROM users WHERE id > :id1 AND id < :id2";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id1", 1);
        params.addValue("id2", 3);
        String userName = jdbc.queryForObject(query, params, String.class);

        Assert.assertThat(userName, equalTo("alex"));

        // fluent code
        String userName2 = jdbc.query("SELECT name FROM users WHERE id > :id1 AND id < :id2")
                .bind("id1", 1)
                .bind("id2", 3)
                .fetchOne(String.class);

        Assert.assertThat(userName2, equalTo("alex"));
    }

    @Test
    @Rollback
    public void testBindTwoNumberAndOneStringParameters() {
        // original code
        String query = "SELECT name FROM users WHERE id > :id1 AND id < :id2 AND name LIKE :nameTemplate";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id1", 1);
        params.addValue("id2", 3);
        params.addValue("nameTemplate", "a%");
        String userName = jdbc.queryForObject(query, params, String.class);

        Assert.assertThat(userName, equalTo("alex"));

        // fluent code
        String userName2 = jdbc.query("SELECT name FROM users WHERE id > :id1 AND id < :id2 AND name LIKE :nameTemplate")
                .bind("id1", 1)
                .bind("id2", 3)
                .bind("nameTemplate", "a%")
                .fetchOne(String.class);

        Assert.assertThat(userName2, equalTo("alex"));
    }

    @Test
    @Rollback
    public void testBindDateParameter() {
        // original code
        String query = "SELECT name FROM users WHERE BIRTH_DATE < :maxDate";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("maxDate", Date.from(Instant.parse("1981-01-01T00:00:00.00Z")));
        String userName = jdbc.queryForObject(query, params, String.class);

        Assert.assertThat(userName, equalTo("mkyong"));

        // fluent code
        String userName2 = jdbc.query("SELECT name FROM users WHERE BIRTH_DATE < :maxDate")
                .bind("maxDate", Date.from(Instant.parse("1981-01-01T00:00:00.00Z")))
                .fetchOne(String.class);

        Assert.assertThat(userName2, equalTo("mkyong"));
    }

}
