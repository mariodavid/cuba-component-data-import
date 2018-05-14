
## Docker container for DB scripts

#### PostgreSQL
`docker run --name postgres -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres postgres:alpine`

Studio settings: 
* JDBC: `jdbc:postgres//localhost/postgres`
* user: `postgres`
* pass: `postgres`

#### MySQL
`docker run --name mysql -e MYSQL_ROOT_PASSWORD=mysql -e MYSQL_DATABASE=cuba -e MYSQL_USER=cuba -e MYSQL_PASS=cuba -p 3306:3306 -d mysq`

Studio settings: 
* JDBC: `jdbc:mysql//localhost/cuba`
* connection params: `?useSSL=false&allowMultiQueries=true&allowPublicKeyRetrieval=true`
* user: `root`
* pass: `mysql`

#### Oracle
`docker run -d -p 1521:1521 --name oracle -e ORACLE_ALLOW_REMOTE=true wnameless/oracle-xe-11g`
Studio settings: 
* JDBC: `jdbc:oracle:thin:@//localhost/xe`
* connection params: ``
* user: `system`
* pass: `oracle`

#### MS-SQL
`docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=mssqlserver' -p 1433:1433 -d --name mssql microsoft/mssql-server-linux:latest`
