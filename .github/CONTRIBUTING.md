# Welcome to the Bug Wars Server contributing guide

## New Contributor Guide

In order to run the project on your computer, you need to do the following:

Clone the repo onto your machine
```
git clone https://github.com/yuneKim/bug-wars-server.git
```

It is assumed you have [PostgreSQL](https://www.postgresql.org/download/) installed on your machine. Create a new database for use with this project. Spring data jpa will manage creation and modification of tables. You may edit `bug-wars-server/src/main/resources/data.sql` to populate the tables with test data.

If you have not previously used Lombok, your IDE should prompt you to install a Lombok plugin when you open the project. Accept the install and enable live processing when it prompts you to do so.

Create the file `bug-wars-server/env.properties` and modify the below content with your postgres connection info.
```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=<your postgres database name>
DB_USER=<your postgres username>
DB_PASSWORD=<your postgres password>
DB_DDL=create-drop
INIT_MODE=ALWAYS
JWT_SECRET=<a 64 character base64 encoded secret>
JWT_EXPIRATION_MS=600000
```
For the secret you can either search for a base64 string generator or omit this line to use the project default. Using the project default is fine for local testing.

You should now be able to run the server.