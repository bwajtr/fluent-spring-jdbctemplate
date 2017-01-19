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
import com.clevergang.jdbc.tests.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.CoreMatchers.*;

/**
 * FluentQueryBuilder class tests for queries where only single object is returned.
 *
 * @author Bretislav Wajtr
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestSpringContext.class})
@Transactional
@Rollback
public class FluentQuerySingleObjectResultTest {

    @Autowired
    private FluentNamedParameterJdbcTemplate jdbc;

    @Test
    public void testQuerySinglePrimitiveNumberResult() {
        // original code
        String query = "SELECT count(*) FROM users";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        Integer userCount = jdbc.queryForObject(query, params, Integer.class);

        Assert.assertThat(userCount, is(notNullValue()));
        Assert.assertThat(userCount, equalTo(3));

        // fluent code
        Integer userCount2 = jdbc.query("SELECT count(*) FROM USERS")
                .fetchOne(Integer.class);

        Assert.assertThat(userCount2, equalTo(userCount));
    }

    @Test
    public void testQuerySinglePrimitiveStringResult() {
        // original code
        String query = "SELECT name FROM users WHERE ID = 1";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        String userName = jdbc.queryForObject(query, params, String.class);

        Assert.assertThat(userName, is(notNullValue()));
        Assert.assertThat(userName, equalTo("mkyong"));

        // fluent code
        String userName2 = jdbc.query("SELECT name FROM users WHERE ID = 1")
                .fetchOne(String.class);

        Assert.assertThat(userName2, equalTo(userName));
    }

    @Test
    public void testQuerySinglePrimitiveDateResult() {
        // original code
        String query = "SELECT BIRTH_DATE FROM users WHERE ID = 1";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        LocalDate birthDate = jdbc.queryForObject(query, params, (rs, rowNum) -> rs.getDate("BIRTH_DATE").toLocalDate());

        Assert.assertThat(birthDate, is(notNullValue()));
        Assert.assertThat(birthDate, equalTo(LocalDate.of(1980, Month.MAY, 20)));

        // fluent code
        LocalDate birthDate2 = jdbc.query("SELECT BIRTH_DATE FROM users WHERE ID = 1")
                .fetchOne((rs, rowNum) -> rs.getDate("BIRTH_DATE").toLocalDate());

        Assert.assertThat(birthDate2, equalTo(birthDate));
    }

    @Test
    public void testQuerySinglePrimitiveSQLDateResult() {
        // original code
        String query = "SELECT BIRTH_DATE FROM users WHERE ID = 1";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        Date birthDate = jdbc.queryForObject(query, params, Date.class);

        Assert.assertThat(birthDate, is(notNullValue()));
        Assert.assertThat(birthDate, equalTo(Date.valueOf(LocalDate.of(1980, Month.MAY, 20))));

        // fluent code
        Date birthDate2 = jdbc.query("SELECT BIRTH_DATE FROM users WHERE ID = 1")
                .fetchOne(Date.class);

        Assert.assertThat(birthDate2, equalTo(birthDate));
    }

    @Test
    public void testQuerySinglePrimitiveUtilDateResult() throws ParseException {
        // original code
        String query = "SELECT BIRTH_DATE FROM users WHERE ID = 1";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        java.util.Date birthDate = jdbc.queryForObject(query, params, java.util.Date.class);

        SimpleDateFormat formatter = new SimpleDateFormat("d.M.yyyy");
        Assert.assertThat(formatter.format(birthDate), equalTo("20.5.1980"));

        // fluent code
        java.util.Date birthDate2 = jdbc.query("SELECT BIRTH_DATE FROM users WHERE ID = 1")
                .fetchOne(java.util.Date.class);

        Assert.assertThat(formatter.format(birthDate2), equalTo("20.5.1980"));
    }

    @Test
    public void testQuerySinglePrimitiveDateTimeResult() {
        // original code
        String query = "SELECT TIME_OF_DEATH FROM users WHERE ID = 1";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        LocalDateTime dateOfDeath = jdbc.queryForObject(query, params, (rs, rowNum) -> rs.getTimestamp("TIME_OF_DEATH").toLocalDateTime());

        Assert.assertThat(dateOfDeath, is(notNullValue()));
        Assert.assertThat(dateOfDeath, equalTo(LocalDateTime.of(2016, Month.APRIL, 1, 12, 33)));

        // fluent code
        LocalDateTime dateOfDeath2 = jdbc.query("SELECT TIME_OF_DEATH FROM users WHERE ID = 1")
                .fetchOne((rs, rowNum) -> rs.getTimestamp("TIME_OF_DEATH").toLocalDateTime());

        Assert.assertThat(dateOfDeath2, equalTo(dateOfDeath));
    }

    @Test
    public void testQuerySingleCustomClassResult() throws Throwable {
        // original code
        String query = "SELECT * FROM users WHERE ID = 1";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        User user = jdbc.queryForObject(query, params, BeanPropertyRowMapper.newInstance(User.class));

        Assert.assertThat(user, is(notNullValue()));
        Assert.assertThat(user.getId(), equalTo(1));
        Assert.assertThat(user.getEmail(), equalTo("mkyong@gmail.com"));
        Assert.assertThat(user.getName(), equalTo("mkyong"));
        Assert.assertThat(user.getBirthDate(), equalTo(LocalDate.of(1980, 5, 20)));
        Assert.assertThat(user.getTimeOfDeath(), equalTo(LocalDateTime.of(2016, Month.APRIL, 1, 12, 33)));

        // fluent code
        User user2 = jdbc.query("SELECT * FROM users WHERE ID = 1")
                .fetchOne(User.class);

        Assert.assertThat(user2, equalTo(user));
    }

    @Test
    public void testQuerySingleResultUsingMapper() {
        // original code
        String query = "SELECT name, email FROM users WHERE ID = 1";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        String userName = jdbc.queryForObject(query, params, (rs, rowNum) ->
                rs.getString("name") + " (" + rs.getString("email") + ")"
        );

        Assert.assertThat(userName, is(notNullValue()));
        Assert.assertThat(userName, equalTo("mkyong (mkyong@gmail.com)"));

        // fluent code
        String userName2 = jdbc.query("SELECT name, email FROM users WHERE ID = 1")
                .fetchOne((rs, rowNum) ->
                        rs.getString("name") + " " + rs.getString("email")
                );

        Assert.assertThat(userName2, equalTo("mkyong mkyong@gmail.com"));
    }


}