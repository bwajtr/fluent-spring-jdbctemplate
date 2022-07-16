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

import static org.hamcrest.CoreMatchers.*;

import java.time.Instant;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.clevergang.jdbc.FluentNamedParameterJdbcTemplate;
import com.clevergang.jdbc.fluent.FluentQueryBuilder;
import com.clevergang.jdbc.tests.TestSpringContext;

/**
 * The very basic tests of FluentQueryBuilder - testing of the various forms of .bind() method
 *
 * @author Bretislav Wajtr
 * @author S.Kashihara
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestSpringContext.class})
@Transactional
@Rollback
public class FluentQueryBindingTest {

    @Autowired
    private FluentNamedParameterJdbcTemplate jdbc;

    @Test
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

    @Test
    public void testModifyBoundParameter() {
        FluentQueryBuilder builder = jdbc.query("SELECT name FROM users WHERE id = :id")
                .bind("id", 1);

        builder.getMapBoundParameters().addValue("id", 2);
        String name = builder.fetchOne(String.class);

        Assert.assertThat(name, equalTo("alex"));
    }

    
	@SuppressWarnings("unused")
    private static class TestParameterBean {
    	public int id;
    	public int id1;
    	public int id2;
    	public String nameTemplate;
    	public Date maxDate;
		public int getId() {
			return id;
		}
		public int getId1() {
			return id1;
		}
		public int getId2() {
			return id2;
		}
		public String getNameTemplate() {
			return nameTemplate;
		}
		public Date getMaxDate() {
			return maxDate;
		}
    }
    
    @Test
    public void testBindBeanSingleNumberParameter() {
        // original code
        String query = "SELECT name FROM users WHERE id = :id";
        TestParameterBean bean = new TestParameterBean();
        bean.id = 1;
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(bean);
        String userName = jdbc.queryForObject(query, params, String.class);

        Assert.assertThat(userName, equalTo("mkyong"));

        // fluent code
        String userName2 = jdbc.query("SELECT name FROM users WHERE id = :id")
                .bind(bean)
                .fetchOne(String.class);

        Assert.assertThat(userName2, equalTo("mkyong"));
    }

    @Test
    public void testBindBeanTwoNumberParameters() {
        // original code
        String query = "SELECT name FROM users WHERE id > :id1 AND id < :id2";
        TestParameterBean bean = new TestParameterBean();
        bean.id1 = 1;
        bean.id2 = 3;
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(bean);
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
    public void testBindBeanTwoNumberAndOneStringParameters() {
        // original code
        String query = "SELECT name FROM users WHERE id > :id1 AND id < :id2 AND name LIKE :nameTemplate";
        TestParameterBean bean = new TestParameterBean();
        bean.id1 = 1;
        bean.id2 = 3;
        bean.nameTemplate = "a%";
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(bean);
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
    public void testBindBeanDateParameter() {
        // original code
        String query = "SELECT name FROM users WHERE BIRTH_DATE < :maxDate";
        TestParameterBean bean = new TestParameterBean();
        bean.maxDate = Date.from(Instant.parse("1981-01-01T00:00:00.00Z"));
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(bean);
        String userName = jdbc.queryForObject(query, params, String.class);

        Assert.assertThat(userName, equalTo("mkyong"));

        // fluent code
        String userName2 = jdbc.query("SELECT name FROM users WHERE BIRTH_DATE < :maxDate")
                .bind("maxDate", Date.from(Instant.parse("1981-01-01T00:00:00.00Z")))
                .fetchOne(String.class);

        Assert.assertThat(userName2, equalTo("mkyong"));
    }

    @Test
    public void testModifyBeanBoundParameter() {
        TestParameterBean bean = new TestParameterBean();
        bean.id = 1;
        FluentQueryBuilder builder = jdbc.query("SELECT name FROM users WHERE id = :id")
                .bind(bean);

        bean.id = 2;
        String name = builder.fetchOne(String.class);

        Assert.assertThat(name, equalTo("alex"));
    }

}
