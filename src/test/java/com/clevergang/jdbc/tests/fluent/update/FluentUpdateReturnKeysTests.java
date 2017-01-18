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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

/**
 * Tests of capability to return generated keys.
 *
 * @author Bretislav Wajtr
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestSpringContext.class})
@Transactional
@Rollback
public class FluentUpdateReturnKeysTests {

    @Autowired
    private FluentNamedParameterJdbcTemplate jdbc;

    @Before
    public void resetSequences() {
        jdbc.update("ALTER SEQUENCE users_pk_seq RESTART WITH 4").execute();
    }

    @Test
    public void testInsertAndReturnAutogeneratedPrimaryKey() {
        String testedName = "someNameAutogenerated";
        Integer key = jdbc.update("INSERT INTO USERS (NAME) VALUES (:name)")
                .bind("name", testedName)
                .executeAndReturnKey("ID");

        // check postconditions
        assertUserCount(4);

        Assert.assertThat(key, equalTo(4));

        String name = jdbc.query("SELECT name from USERS where id = :id").bind("id", key).fetchOne(String.class);
        Assert.assertThat(name, equalTo(testedName));
    }

    private void assertUserCount(Integer expectedUserCount) {
        Integer count = jdbc.query("SELECT COUNT(*) from USERS").fetchOne(Integer.class);
        Assert.assertThat(count, equalTo(expectedUserCount));
    }

    @Test
    public void testInsertAndReturnAutogeneratedDefaultColumn() {
        Integer defaultColumnValue = jdbc.update("INSERT INTO USERS (NAME) VALUES (:name)")
                .bind("name", "someNameDefaultColumn")
                .executeAndReturnKey("COLUMN_WITH_DEFAULT");

        // check postconditions
        assertUserCount(4);

        Assert.assertThat(defaultColumnValue, equalTo(100));
    }

    @Test
    public void testInsertAndReturnAutogeneratedMultipleColumns() {
        Map<String, Object> generatedKeys = jdbc.update("INSERT INTO USERS (NAME) VALUES (:name)")
                .bind("name", "someNameDefaultColumn")
                .executeAndReturnKeys("ID", "COLUMN_WITH_DEFAULT");

        // check postconditions
        assertUserCount(4);

        Assert.assertThat(generatedKeys, is(notNullValue()));
        Assert.assertThat(generatedKeys.size(), equalTo(2));
        Assert.assertThat(generatedKeys.get("ID"), equalTo(4));
        Assert.assertThat(generatedKeys.get("COLUMN_WITH_DEFAULT"), equalTo(100));
    }

}
