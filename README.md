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

    RowMapper<User> rowMapper = (rs, rowNum) -> {
            User ret = new User();
            ret.setId(rs.getInt("id"));
            ret.setName(rs.getString("name"));
            ret.setEmail(rs.getString("email"));
            return ret;
        };

User user = jdbc.queryForObject(query, params, rowMapper);
```

## Where to get it

Fluent Spring JDBCTemplate is **available in [Maven Central](https://search.maven.org/#artifactdetails%7Ccom.clevergang.libs%7Cfluent-spring-jdbctemplate%7C1.0.0%7Cjar)**:

```xml
<dependency>
    <groupId>com.clevergang.libs</groupId>
    <artifactId>fluent-spring-jdbctemplate</artifactId>
    <version>1.0.0</version>
</dependency>
```

or for Gradle:

```groovy
compile 'com.clevergang.libs:fluent-spring-jdbctemplate:1.0.0'
```
Note that Fluent Spring JDBCTemplate is an extension of the spring-jdbc library, so

## How to set it up

The central class in this library is `FluentNamedParameterJdbcTemplate` which should be set up in spring application context in exactly same way as commonly used `NamedParameterJdbcTemplate` from the spring-jdbc library.

So instead of creating and autowiring bean of type `NamedParameterJdbcTemplate`, create and autowire bean of type `FluentNamedParameterJdbcTemplate`. If you project already uses `NamedParameterJdbcTemplate` and you want to switch to our fluent version, then go ahead and replace all occurences of the `NamedParameterJdbcTemplate` type with `FluentNamedParameterJdbcTemplate`. It's a safe operation to do...

If you want to use `FluentNamedParameterJdbcTemplate` in completely new project, then just create the bean in Spring configuration class:

```java
@Configuration
public class SpringContextConfiguration {

    @Bean
    public DataSource dataSource() {
        // setup your datasource here and return it
    }


    @Bean
    public FluentNamedParameterJdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return new FluentNamedParameterJdbcTemplate(dataSource);
    }
}
```

and then autowire it in your DAO classes:

```java
@Repository
public class UsersDAO {

    @Autowired
    private FluentNamedParameterJdbcTemplate jdbc;

    public User find(Integer id) {
       return jdbc.query("SELECT * FROM users WHERE ID = :id")
                    .bind("id", 1)
                    .fetchOne(User.class);
    }
```

## How to use it

## License
Fluent Spring JDBCTemplate library is released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
