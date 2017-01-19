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
 * Fluent style builder for execution of the SQL queries (SQL SELECT statements). This builder is initialized with the given
 * SQL statement, provides methods for binding query parameters and offers methods for final query execution. Some of the provided
 * methods provide possibility of returning generated key. Example:
 * <pre>{@code
 *  User user = jdbc.query("SELECT * FROM users WHERE id = :id")
 *                  .bind("id", 1)
 *                  .fetchOne(User.class);
 * }</pre>
 *
 * @author Bretislav Wajtr
 * @implNote Not thread safe
 */
public class FluentQueryBuilder extends AbstractFluentBuilder<FluentQueryBuilder> {

    private final String query;
    private final NamedParameterJdbcOperations baseTemplate;

    /**
     * Creates new FluentQueryBuilder using given "query" and namedParameterTemplate.
     * Note that FluentQueryBuilder class is designed to be created for each query and due to the mutable nature
     * of bind parameters it's not recommended to share instances of FluentQueryBuilder by threads (== not thread safe).
     *
     * @param query Query to be executed
     * @param namedParameterTemplate Template to be used for query execution
     */
    public FluentQueryBuilder(String query, NamedParameterJdbcOperations namedParameterTemplate) {
        this.query = query;
        this.baseTemplate = namedParameterTemplate;
    }

    /**
     * Executes prepared SQL query, returning single result object. The query is expected to be a single row query; the SQL ResultSet will be mapped
     * to Java class using provided RowMapper. Example:<br/>
     * <pre>{@code
     * EmployeeInfo user = jdbc.query("SELECT count(*) as count, avg(salary) as avg_salary FROM employees")
     *     .fetchOne((rs, rowNum) -> {
     *           EmployeeInfo ret = new EmployeeInfo();
     *           ret.setCount(rs.getInt("count"));
     *           ret.setAverageSalary(rs.getBigDecimal("avg_salary"));
     *           return ret;
     *        });
     * }</pre>
     *
     * @param rowMapper RowMapper to use for JDBC ResultSet mapping to java object
     * @return Returns single mapped object
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if the query does not return exactly one row, or does not return exactly
     *                                                                        one column in that row
     * @throws org.springframework.dao.DataAccessException                    if the query fails
     */
    public <T> T fetchOne(RowMapper<T> rowMapper) {
        Assert.notNull(rowMapper, "You HAVE TO provide row mapper");

        return baseTemplate.queryForObject(query, getBoundParameters(), rowMapper);
    }

    /**
     * Executes prepared SQL query, returning single result object. This method accepts two types of classes a parameter:<br/>
     * <ul>
     * <li>In a case that the parameter is one of the primitive wrapper classes or one
     * of the classes which are directly convertible to the SQL types, then the this method
     * will expect that the SQL query is "single row/single column" query (simply returning single value)
     * and will try to map the SQL result directly to the primitive type. Example:
     * <pre>{@code
     * Integer employeeCount = jdbc.query("SELECT count(*) FROM employees")
     *                             .fetchOne(Integer.class);
     * }</pre>
     * </li>
     * <li>If the class passed in as a parameter is detected to be a ordinary Java POJO (custom class) then this method will
     * expect that the SQL query in this builder will return just single row with multiple columns, and will try to map this single row
     * to the POJO class using BeanPropertyRowMapper. Example:
     * <pre>{@code
     *  User user = jdbc.query("SELECT * FROM users WHERE id = :id")
     *                  .bind("id", 1)
     *                  .fetchOne(User.class);
     * }</pre>
     * </li>
     * </ul>
     *
     * @param resultType the type that the result object is expected to match
     * @return Returns single mapped object
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if the query does not return exactly one row, or in case of primitive types does not return exactly
     *                                                                        one column in that row
     * @throws org.springframework.dao.DataAccessException                    if the query fails
     */
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


    /**
     * Executes prepared SQL query, returning list of objects. This method accepts two types of classes as a parameter:<br/>
     * <ul>
     * <li>In a case that the parameter is one of the primitive wrapper classes or one
     * of the classes which are directly convertible to the SQL types, then the this method
     * will expect that the SQL query will return single column ResultSet (returning single value for each row)
     * and will try to map each row from the SQL result directly to the primitive type. Example:
     * <pre>{@code
     * List<String> employeeNames = jdbc.query("SELECT name FROM employees")
     *                          .fetch(String.class);
     * }</pre>
     * </li>
     * <li>If the class passed in as a parameter is detected to be a ordinary Java POJO (custom class) then this method will
     * expect that the SQL query in this builder will return rows with multiple columns, and will try to map these rows
     * to the POJO objects using BeanPropertyRowMapper. Example:
     * <pre>{@code
     *  List<User> users = jdbc.query("SELECT * FROM users")
     *                   .fetch(User.class);
     * }</pre>
     * </li>
     * </ul>
     *
     * @param resultType the type that the result object is expected to match
     * @return Returns single mapped object
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if the query does not return exactly one row, or in case of primitive types does not return exactly
     *                                                                        one column in that row
     * @throws org.springframework.dao.DataAccessException                    if the query fails
     */
    public <T> List<T> fetch(Class<T> resultType) {
        Assert.notNull(resultType, "You HAVE TO provide type to map the result to");

        if (isSingleColumnMapperType(resultType)) {
            return baseTemplate.queryForList(query, getBoundParameters(), resultType);
        } else {
            return baseTemplate.query(query, getBoundParameters(), BeanPropertyRowMapper.newInstance(resultType));
        }
    }

    /**
     * Executes prepared SQL query, returning list of objects. The query is expected to return zero to many rows; returned rows will be mapped
     * to Java objects using provided RowMapper. Example:
     * <pre>{@code
     * List<User> users = jdbc.query("SELECT id, name, email FROM users")
     *                  .fetch((rs, rowNum) -> {
     *                              User ret = new User();
     *                              ret.setId(rs.getInt("id"));
     *                              ret.setName(rs.getString("name"));
     *                              ret.setEmail(rs.getString("email"));
     *                              return ret;
     *                          });
     * }</pre>
     *
     * @param rowMapper RowMapper to use for JDBC ResultSet mapping to java object
     * @return the result List, containing mapped objects
     * @throws org.springframework.dao.DataAccessException  if the query fails
     */
    public <T> List<T> fetch(RowMapper<T> rowMapper) {
        Assert.notNull(rowMapper, "You HAVE TO provide row mapper");

        return baseTemplate.query(query, getBoundParameters(), rowMapper);
    }


}