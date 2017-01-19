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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

/**
 * FluentQueryBuilder class tests for queries which return multiple records (and therefore result needs
 * to be mapped to a List)
 *
 * @author Bretislav Wajtr
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestSpringContext.class})
@Transactional
@Rollback
public class FluentQueryListResultTests {

    @Autowired
    private FluentNamedParameterJdbcTemplate jdbc;

    @Test
    public void testQueryListCustomClassResult() {
        // original code
        String query = "SELECT * FROM users ORDER BY id";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        List<User> users = jdbc.query(query, params, BeanPropertyRowMapper.newInstance(User.class));

        verifyAllThreeUsers(users);

        // fluent code
        List<User> users2 = jdbc.query("SELECT * FROM users ORDER BY id")
                .fetch(User.class);

        verifyAllThreeUsers(users2);
    }

    private void verifyAllThreeUsers(List<User> users) {
        Assert.assertThat(users, is(notNullValue()));
        Assert.assertThat(users.size(), equalTo(3));

        Assert.assertThat(users.get(0).getId(), equalTo(1));
        Assert.assertThat(users.get(0).getEmail(), equalTo("mkyong@gmail.com"));
        Assert.assertThat(users.get(0).getName(), equalTo("mkyong"));
        Assert.assertThat(users.get(0).getBirthDate(), equalTo(LocalDate.of(1980, 5, 20)));
        Assert.assertThat(users.get(0).getTimeOfDeath(), equalTo(LocalDateTime.of(2016, Month.APRIL, 1, 12, 33)));

        Assert.assertThat(users.get(1).getId(), equalTo(2));
        Assert.assertThat(users.get(1).getEmail(), equalTo("alex@yahoo.com"));
        Assert.assertThat(users.get(1).getName(), equalTo("alex"));
        Assert.assertThat(users.get(1).getBirthDate(), equalTo(LocalDate.of(1981, Month.MARCH, 11)));
        Assert.assertThat(users.get(1).getTimeOfDeath(), equalTo(LocalDateTime.of(2016, Month.SEPTEMBER, 13, 9, 1)));

        Assert.assertThat(users.get(2).getId(), equalTo(3));
        Assert.assertThat(users.get(2).getEmail(), equalTo("joel@gmail.com"));
        Assert.assertThat(users.get(2).getName(), equalTo("joel"));
        Assert.assertThat(users.get(2).getBirthDate(), equalTo(LocalDate.of(1982, Month.SEPTEMBER, 17)));
        Assert.assertThat(users.get(2).getTimeOfDeath(), equalTo(LocalDateTime.of(2016, Month.OCTOBER, 22, 17, 41)));
    }

    @Test
    public void testQueryListOfIntegersResult() {
        // original code
        String query = "SELECT id FROM users ORDER BY id";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        List<Integer> ids = jdbc.queryForList(query, params, Integer.class);

        verifyIds(ids);

        // fluent code
        List<Integer> ids2 = jdbc.query("SELECT id FROM users ORDER BY id")
                .fetch(Integer.class);

        verifyIds(ids2);
    }

    private void verifyIds(List<Integer> ids) {
        Assert.assertThat(ids, is(notNullValue()));
        Assert.assertThat(ids.size(), equalTo(3));
        Assert.assertThat(ids.get(0), equalTo(1));
        Assert.assertThat(ids.get(1), equalTo(2));
        Assert.assertThat(ids.get(2), equalTo(3));
    }

    @Test
    public void testQueryListOfStringsResult() {
        // original code
        String query = "SELECT name FROM users ORDER BY id";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        List<String> names = jdbc.queryForList(query, params, String.class);

        verifyNames(names);

        // fluent code
        List<String> names2 = jdbc.query("SELECT name FROM users ORDER BY id")
                .fetch(String.class);

        verifyNames(names2);
    }

    private void verifyNames(List<String> names) {
        Assert.assertThat(names, is(notNullValue()));
        Assert.assertThat(names.get(0), equalTo("mkyong"));
        Assert.assertThat(names.get(1), equalTo("alex"));
        Assert.assertThat(names.get(2), equalTo("joel"));
    }

    @Test
    public void testQueryListOfUtilDateResult() {
        // original code
        String query = "SELECT BIRTH_DATE FROM users ORDER BY id";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        List<java.util.Date> dates = jdbc.queryForList(query, params, java.util.Date.class);

        verifyBirthDates(dates);

        // fluent code
        List<java.util.Date> dates2 = jdbc.query("SELECT BIRTH_DATE FROM users ORDER BY id")
                .fetch(java.util.Date.class);

        verifyBirthDates(dates2);
    }

    private void verifyBirthDates(List<Date> dates) {
        SimpleDateFormat formatter = new SimpleDateFormat("d.M.yyyy");
        Assert.assertThat(dates, is(notNullValue()));
        Assert.assertThat(formatter.format(dates.get(0)), equalTo("20.5.1980")); //mkyong
        Assert.assertThat(formatter.format(dates.get(1)), equalTo("11.3.1981")); //alex
        Assert.assertThat(formatter.format(dates.get(2)), equalTo("17.9.1982")); //joel
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Test
    public void testQueryListResultUsingMapper() {
        // original code
        String query = "SELECT id, name, email FROM users";
        EmptySqlParameterSource params = EmptySqlParameterSource.INSTANCE;
        List<String> descriptions = jdbc.query(query, params, (rs, rowNum) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(rs.getString("id"));
            builder.append(" ");
            builder.append(rs.getString("name"));
            builder.append(" (");
            builder.append(rs.getString("email"));
            builder.append(")");
            return builder.toString();
        });

        Assert.assertThat(descriptions, is(notNullValue()));
        Assert.assertThat(descriptions.size(), equalTo(3));
        Assert.assertThat(descriptions.get(0), equalTo("1 mkyong (mkyong@gmail.com)"));

        // fluent code
        List<User> partialUsers = jdbc.query("SELECT id, name, email FROM users")
                .fetch((rs, rowNum) -> {
                    User ret = new User();
                    ret.setId(rs.getInt("id"));
                    ret.setName(rs.getString("name"));
                    ret.setEmail(rs.getString("email"));
                    return ret;
                });

        Assert.assertThat(partialUsers, is(notNullValue()));
        Assert.assertThat(partialUsers.size(), equalTo(3));
        Assert.assertThat(partialUsers.get(0).getId(), equalTo(1));
        Assert.assertThat(partialUsers.get(0).getName(), equalTo("mkyong"));
        Assert.assertThat(partialUsers.get(0).getEmail(), equalTo("mkyong@gmail.com"));
        Assert.assertThat(partialUsers.get(0).getBirthDate(), is(nullValue()));
    }

}