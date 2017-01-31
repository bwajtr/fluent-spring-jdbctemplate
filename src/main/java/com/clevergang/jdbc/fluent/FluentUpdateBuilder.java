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

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Fluent style builder for execution of  statements which update the database state (INSERT, UPDATE, DELETE operations).
 * This builder is initialized with the given SQL statement, provides methods for binding query parameters and offers
 * various methods for final query execution. Example usage:
 * <pre>{@code
 * jdbc.update("UPDATE users SET name = :name WHERE id = :id")
 *     .bind("name", "Alex")
 *     .bind("id", 2)
 *     .execute();
 * }</pre>
 *
 * @author Bretislav Wajtr
 *
 * NOT THREAD SAFE  
 */
public class FluentUpdateBuilder extends AbstractFluentBuilder<FluentUpdateBuilder> {

    private final String statement;
    private final NamedParameterJdbcOperations baseTemplate;

    /**
     * Creates new FluentUpdateBuilder using given "query" and namedParameterTemplate.
     * Note that FluentUpdateBuilder class is designed to be created for each statement and due to the mutable nature
     * of bind parameters it's not recommended to share instances of FluentUpdateBuilder by threads (== not thread safe).
     *
     * @param statement SQL statement to be executed
     * @param namedParameterTemplate Template to be used for statement execution
     */
    public FluentUpdateBuilder(String statement, NamedParameterJdbcOperations namedParameterTemplate) {
        this.statement = statement;
        this.baseTemplate = namedParameterTemplate;
    }

    /**
     * Executes prepared update statement (with parameters bound using the bind() methods) and returns number of updated rows.
     * Example usage:
     * <pre>{@code
     * int updatedRowsCount = jdbc.update("UPDATE users SET name = :name WHERE id = :id")
     *                            .bind("name", "Alex")
     *                            .bind("id", 2)
     *                            .execute();
     * }</pre>
     * @return the number of rows affected
     * @throws org.springframework.dao.DataAccessException if there is any problem issuing the update
     */
    public int execute() {
        return baseTemplate.update(statement, getBoundParameters());
    }

    /**
     * Executes prepared update statement (with parameters bound using the bind() methods) and return generated key.
     * You have to specify name of the table column, which holds the generated key. A automatic type conversion based on
     * the return type is attempted. Example usage:
     * <pre>{@code
     * Integer key = jdbc.update("INSERT INTO user (name) VALUES (:name)")
     *                   .bind("name", "Ivan")
     *                   .executeAndReturnKey("id");
     * }</pre>
     *
     * @param keyName name of the column that will have key generated for it
     * @param <T> Return type
     * @return the generated key
     * @throws org.springframework.dao.DataAccessException if there is any problem issuing the update
     * @throws InvalidDataAccessApiUsageException if multiple keys are encountered (we normally expect just single key to be generated).
     * @see GeneratedKeyHolder
     */
    @SuppressWarnings("unchecked")
    public <T extends Number> T executeAndReturnKey(String keyName) {
        Assert.notNull(keyName);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        baseTemplate.update(statement, getBoundParameters(), keyHolder, new String[] {keyName});

        return (T) keyHolder.getKey();
    }

    /**
     * Executes prepared update statement (with parameters bound using the bind() methods) and return generated keys.
     * You have to specify names of the table columns, which hold the generated keys.  Example usage:
     * <pre>{@code
     * Map<String, Object> keys = jdbc.update("INSERT INTO user (name) VALUES (:name)")
     *                   .bind("name", "Ivan")
     *                   .executeAndReturnKey("id", "columnWithDefaultValueInDB");
     *
     * System.out.println("First key: " + keys.get("id"));
     * System.out.println("Second key: " + keys.get("columnWithDefaultValueInDB"));
     * }</pre>
     *
     * @param keys names of the columns that will have keys generated for them
     * @return the Map of generated keys
     * @throws InvalidDataAccessApiUsageException if keys for multiple rows are encountered
     * @see GeneratedKeyHolder
     */
    public Map<String, Object> executeAndReturnKeys(String... keys) {
        Assert.notNull(keys);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        baseTemplate.update(statement, getBoundParameters(), keyHolder, keys);

        return keyHolder.getKeys();
    }
}
