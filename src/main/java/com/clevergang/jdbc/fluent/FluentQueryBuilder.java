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

package com.clevergang.jdbc.fluent;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.Assert;

import java.util.List;

/**
 * To get single object:
 *
 * jdbc.query("SELECT * from Users where id = :idParam")
 *  .bind("idParam", 1)
 *  .fetchOne(User.class)
 *
 * or to get list of objects:
 *
 * jdbc.query("SELECT * from Users where id > :idParam")
 *  .bind("idParam", 1)
 *  .fetch(User.class)
 *
 * or to get list of objects with custom mapping:
 *
 * jdbc.query("SELECT id, name, email FROM users")
 *  .fetch((rs, rowNum) -> {
 *           User ret = new User();
 *           ret.setId(rs.getInt("id"));
 *           ret.setName(rs.getString("name"));
 *           ret.setEmail(rs.getString("email"));
 *           return ret;
 *        });
 *
 * @author Bretislav Wajtr
 */
public class FluentQueryBuilder extends AbstractFluentBuilder<FluentQueryBuilder> {

    private String query;
    private NamedParameterJdbcOperations baseTemplate;

    public FluentQueryBuilder(String query, NamedParameterJdbcOperations namedParameterTemplate) {
        this.query = query;
        this.baseTemplate = namedParameterTemplate;
    }

    public <T> T fetchOne(RowMapper<T> rowMapper) {
        Assert.notNull(rowMapper, "You HAVE TO provide row mapper");

        return baseTemplate.queryForObject(query, getBoundParameters(), rowMapper);
    }

    public <T> T fetchOne(Class<T> resultType) {
        Assert.notNull(resultType, "You HAVE TO provide type to map the result to");

        if (isSingleColumnMapperType(resultType)) {
            return baseTemplate.queryForObject(query, getBoundParameters(), resultType);
        } else {
            return baseTemplate.queryForObject(query, getBoundParameters(), BeanPropertyRowMapper.newInstance(resultType));
        }
    }

    private <T> boolean isSingleColumnMapperType(Class<T> resultType) {
        /*
         * We rely on javaTypeToSqlParameterType because it's internally used by Spring for converting between
         * SQL types and java primitive/simple types. Therefore if java type is convertible to sql type (in other words that
         * result of this call is not TYPE_UNKNOWN), then it's very likely that result of the SQL operation will be directly
         * mappable to the class "resultType" - and therefore it's possible to use for example SingleColumnRowMapper
         */
        return StatementCreatorUtils.javaTypeToSqlParameterType(resultType) != SqlTypeValue.TYPE_UNKNOWN;
    }


    public <T> List<T> fetch(Class<T> resultType) {
        Assert.notNull(resultType, "You HAVE TO provide type to map the result to");

        if (isSingleColumnMapperType(resultType)) {
            return baseTemplate.queryForList(query, getBoundParameters(), resultType);
        } else {
            return baseTemplate.query(query, getBoundParameters(), BeanPropertyRowMapper.newInstance(resultType));
        }
    }

    public <T> List<T> fetch(RowMapper<T> rowMapper) {
        Assert.notNull(rowMapper, "You HAVE TO provide row mapper");

        return baseTemplate.query(query, getBoundParameters(), rowMapper);
    }


}