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

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Abstract class aggregating what all fluent builders have in common.
 *
 * @author Bretislav Wajtr
 */
abstract class AbstractFluentBuilder<T> {

    private MapSqlParameterSource mapParameterSource;

    /**
     * Bind a parameter of this query/statement builder.
     * @param parameterName the name of the parameter
     * @param parameterValue the value of the parameter
     * @return a reference to the same query/statement builder,
     * so it's possible to chain several calls together
     */
    @SuppressWarnings("unchecked")
    public T bind(String parameterName, Object parameterValue) {
        getBoundParameters().addValue(parameterName, parameterValue);
        return (T) this;
    }

    /**
     * @return Returns a MapSqlParameterSource representing parameters which were already bound to this query/statement builder.
     * If no parameters were bound, then empty MapSqlParameterSource will be returned.
     */
    public MapSqlParameterSource getBoundParameters() {
        if (mapParameterSource == null) {
            mapParameterSource = new MapSqlParameterSource();
        }

        return mapParameterSource;
    }

}
