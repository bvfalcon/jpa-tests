## JPA Tests

Based of article ["Hibernate inheritance"](https://www.baeldung.com/hibernate-inheritance)

### Benchmark description

Environment:

- Oracle JDK 11.0.9
- MariaDB 10.6.3 with configuration options query_cache_type=0, query_cache_size=0 (global)

```
docker run --name mariadb -e MYSQL_ROOT_PASSWORD=test -p 3306:3306 -d mariadb:10.6.3 --character-set-server=utf8 --collation-server=utf8_general_ci
```

- PostgreSQL 9.6.22 with effective_cache_size = '8 kB'

```
docker run --name postgres -e POSTGRES_PASSWORD=test -p 5432:5432 -d postgres:9.6.22
```

### Build and run

Build with command `mvn clean package assembly:single`

Run with command `java -jar target\jpa-tests-1.0.0-SNAPSHOT-jar-with-dependencies.jar`
