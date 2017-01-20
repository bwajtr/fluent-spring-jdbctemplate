# Fluent Spring JDBCTemplate

Introducing "fluent" style of coding into [Spring JDBCTemplate](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html)

This small library provides a facade to the commonly used NamedParameterJdbcTemplate from spring-jdbc module, offering, in addition to usual jdbc template methods, fluent "Java8 like" style of writing SQL statements and commands. We do not aim to replace the NamedParameterJdbcTemplate, we just want to extend its functionality and offer a simpler way how to use it.

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

So instead of creating and autowiring bean of type `NamedParameterJdbcTemplate`, create and autowire bean of type `FluentNamedParameterJdbcTemplate` and that's it. If you project already uses `NamedParameterJdbcTemplate` and you want to switch to our fluent version, then go ahead and replace all occurences of the `NamedParameterJdbcTemplate` type with `FluentNamedParameterJdbcTemplate`. It's a safe operation to do because it's backwards compatible with `NamedParameterJdbcTemplate`...

If you want to use `FluentNamedParameterJdbcTemplate` in completely new project, then just create the bean in Spring configuration class:

```java
@Configuration
public class SpringContextConfiguration {

    @Bean
    public DataSource dataSource() {
        // setup your datasource here, this is just an example (PostgreSQL on localhost with HikariCP)
        final HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(30);
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setJdbcUrl("jdbc:postgresql:testdb");
        ds.setUsername("test");
        ds.setPassword("test");
        return ds;
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
       // you can then use the template like this:
       return jdbc.query("SELECT * FROM users WHERE ID = :id")
                    .bind("id", 1)
                    .fetchOne(User.class);
    }
```

## How to use it

First of all, `FluentNamedParameterJdbcTemplate` really extends from class `NamedParameterJdbcTemplate`, so all of the methods offered by the spring template are present here as well. Check documentation of these methods [in Spring documentation here](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html#jdbc-NamedParameterJdbcTemplate)

So Beside all of the methods present in usual spring jdbc template our library offers two additional methods, which you can use for "fluent querying":

### Query database (SELECT operations)

Let's see the example:

```java
List<User> adults = jdbc.query("SELECT * FROM users where age > :age")
                        .bind("age", 18)
                        .fetch(User.class);
```                    

In this example `jdbc` is an instance of `FluentNamedParameterJdbcTemplate` and we are querying for all users which are older than 18 years. Notice the use of the named parameter notation in the SQL query and how the value to this parameter is bound to the prepared statment using `.bind()` method. The `.fetch()` method then executes the query (with parameters bound) and maps the sql output to the Java object using the class type passed to the `.fetch()` method as a parameter. Note that in our example the `User` class is ordinary Java POJO and the underlying algorithm will try map the SQL output to this class using `BeanPropertyRowMapper` (provided by Spring) - that means that the table columns names and `User` class properties names has to match in order for such mapping to be successful. See [BeanPropertyRowMapper documentation](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/core/BeanPropertyRowMapper.html) for more details on the matching strategy.

If you need to query for just a single column of some primitive type, you don't have to wrap this primitive to a POJO class, you can use your primitive directly:

```java
List<Integer> allIds = jdbc.query("SELECT id FROM users")
                           .fetch(Integer.class);
```                    

In a case, that you don't want mapping to rely on `BeanPropertyRowMapper`, you can write your own custom RowMapper and pass it to the `.fetch()` method:

```java
List<User> adults = jdbc.query("SELECT * FROM users where age > :age")
                        .bind("age", 18)
                        .fetch((rs, rowNum) -> {
                              User ret = new User();
                              ret.setId(rs.getInt("id"));
                              ret.setName(rs.getString("name"));        
                              return ret;
                         });
```              

You can use special `.fetchOne()` method for cases where you are 100% sure that your query will return exactly one record. Querying by ID is one of such typical cases:

```java
User user = jdbc.query("SELECT * FROM users WHERE id = :id")
                .bind("id", 1)
                .fetchOne(User.class);
```                    

Note, that even `.fetchOne()` method accepts primitive types or custom mappers.

### Update database (UPDATE, INSERT, DELETE operations) 




## License
Fluent Spring JDBCTemplate library is released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
