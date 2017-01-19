# Fluent Spring JDBCTemplate

Introducing "fluent" style of coding into Spring JDBCTemplate

This small library provides a facade to the commonly used NamedParameterJdbcTemplate from spring-jdbc module, offering fluent "Java8 like" style of writing SQL statements and commands. We do not aim to replace the NamedParameterJdbcTemplate, we just want to extend its functionality and try to offer a simpler way how to use it.

So you can write this:
```java
User user = jdbc.query("SELECT * FROM users WHERE ID = :id")
                .bind("id", 1)
                .fetchOne(User.class);
```


instead of this:

```java
String query = "SELECT * FROM users WHERE ID = :id";

MapSqlParameterSource params = new MapSqlParameterSource();
params.addValue("id", 1);

User user = jdbc.queryForObject(query, params, BeanPropertyRowMapper.newInstance(User.class));
```

## License
Fluent Spring JDBCTemplate library is released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
