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

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * The very basic testing of FluentUpdateBuilder - mostly executions without any bound parameters
 *
 * @author Bretislav Wajtr
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestSpringContext.class})
@Transactional
@Rollback
public class FluentUpdateBasicTest {

    @Autowired
    private FluentNamedParameterJdbcTemplate jdbc;

    @Test
    public void testUpdateNoBindings() {
        jdbc.update("UPDATE USERS SET name = 'updated'")
                .execute();

        // check post-conditions
        List<String> name = jdbc.query("SELECT name FROM users").fetch(String.class);
        name.forEach(s -> Assert.assertThat(s, equalTo("updated")));
    }

    @Test
    public void testUpdateReturnResultNoBindings() {
        int updatedRows = jdbc.update("UPDATE USERS SET name = 'updated'")
                .execute();

        // check post-conditions
        Assert.assertThat(updatedRows, equalTo(3));
    }


    @Test
    public void testDeleteNoBindings() {
        jdbc.update("DELETE FROM USERS")
                .execute();

        // check post-conditions
        Integer count = jdbc.query("SELECT COUNT(*) from USERS").fetchOne(Integer.class);
        Assert.assertThat(count, equalTo(0));
    }

}
