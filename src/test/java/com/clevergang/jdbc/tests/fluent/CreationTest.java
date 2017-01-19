/*
 * Copyright 2017 Bretislav Wajtr
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

package com.clevergang.jdbc.tests.fluent;

import com.clevergang.jdbc.FluentNamedParameterJdbcOperations;
import com.clevergang.jdbc.FluentNamedParameterJdbcTemplate;
import com.clevergang.jdbc.tests.TestSpringContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Testing not-so-common usage scenarios of creation and usage of FluentNamedParameterJdbcTemplate.
 *
 * @author Bretislav Wajtr
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestSpringContext.class})
@Transactional
@Rollback
public class CreationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void templateWorksWhenCustomJdbcTemplateIsUsed() {
        // create the template completely outside of the spring application context
        JdbcOperations jdbcOperations = new JdbcTemplate(dataSource);
        FluentNamedParameterJdbcOperations jdbc = new FluentNamedParameterJdbcTemplate(jdbcOperations);

        // test select operation using the FluentNamedParameterJdbcOperations instance
        Integer userCount = jdbc.query("SELECT count(*) FROM users")
                .fetchOne(Integer.class);

        Assert.assertThat(userCount, equalTo(userCount));

        // test update operation using the FluentNamedParameterJdbcOperations instance
        Integer updatedRows = jdbc.update("UPDATE users SET NAME = 'changed'")
                .execute();

        Assert.assertThat(updatedRows, equalTo(3));
    }

}
