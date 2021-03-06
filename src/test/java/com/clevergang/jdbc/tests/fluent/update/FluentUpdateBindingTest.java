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

package com.clevergang.jdbc.tests.fluent.update;

import com.clevergang.jdbc.FluentNamedParameterJdbcTemplate;
import com.clevergang.jdbc.tests.TestSpringContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.*;

/**
 * Tests for .bind() method of FluentUpdateBuilder
 *
 * @author Bretislav Wajtr
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestSpringContext.class})
@Transactional
@Rollback
public class FluentUpdateBindingTest {

    @Autowired
    private FluentNamedParameterJdbcTemplate jdbc;

    @Test
    public void testUpdateBindSingleNumberParameter() {
        jdbc.update("UPDATE USERS SET name = NULL WHERE id = :id")
                .bind("id", 2)
                .execute();

        // check post-conditions
        String name = jdbc.query("select name from users where id = :id").bind("id", 2).fetchOne(String.class);
        Assert.assertThat(name, is(nullValue()));
    }

    @Test
    public void testUpdateBindMultipleParameters() {
        jdbc.update("UPDATE USERS SET name = :name WHERE id = :id")
                .bind("name", "alexUpdated")
                .bind("id", 2)
                .execute();

        // check post-conditions
        String name = jdbc.query("select name from users where id = :id").bind("id", 2).fetchOne(String.class);
        Assert.assertThat(name, equalTo("alexUpdated"));
    }

    @Test
    public void testUpdateBindMultipleParametersReverseOrder() {
        jdbc.update("UPDATE USERS SET name = :name WHERE id = :id")
                .bind("id", 2)
                .bind("name", "alexUpdated2")
                .execute();

        // check post-conditions
        String name = jdbc.query("select name from users where id = :id").bind("id", 2).fetchOne(String.class);
        Assert.assertThat(name, equalTo("alexUpdated2"));
    }

    @Test
    public void testInsertBindSingleNumberParameter() {
        jdbc.update("INSERT INTO USERS (ID) VALUES (:id)")
                .bind("id", 4)
                .execute();

        // check post-conditions
        Integer count = jdbc.query("SELECT COUNT(*) from USERS").fetchOne(Integer.class);
        Assert.assertThat(count, equalTo(4));
    }

    @Test
    public void testInsertBindMultipleParameters() {
        jdbc.update("INSERT INTO USERS (ID, NAME) VALUES (:id, :name)")
                .bind("id", 4)
                .bind("name", "someName")
                .execute();

        // check post-conditions
        Integer count = jdbc.query("SELECT COUNT(*) from USERS").fetchOne(Integer.class);
        Assert.assertThat(count, equalTo(4));
    }

    @Test
    public void testDeleteBindSingleNumberParameter() {
        jdbc.update("DELETE FROM USERS where id = :id")
                .bind("id", 3)
                .execute();

        // check post-conditions
        Integer count = jdbc.query("SELECT COUNT(*) from USERS").fetchOne(Integer.class);
        Assert.assertThat(count, equalTo(2));
    }

    @Test
    public void testDeleteBindMultipleParameters() {
        jdbc.update("DELETE FROM USERS where id = :id and name = :name")
                .bind("id", 3)
                .bind("name", "joel")
                .execute();

        // check post-conditions
        Integer count = jdbc.query("SELECT COUNT(*) from USERS").fetchOne(Integer.class);
        Assert.assertThat(count, equalTo(2));
    }

}
