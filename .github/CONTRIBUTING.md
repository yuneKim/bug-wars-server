# Welcome to the Bug Wars Server contributing guide

## Table Of Contents
[Set up a local dev environment](#set-up-a-local-dev-environment)

[How to contribute](#how-to-contribute)


## Set up a local dev environment

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

## How to contribute

To create a new feature / bug fix / whatever, create a new branch based on the main branch:
```
git branch --track <new-branch> origin/main
```
If you are going to implement a feature or fix a bug, your branch name should be formatted like
```
feature--login-page
bug--user-controller-response-status
```
As you are working on your feature, continue to pull from main periodically to make sure your work doesn't conflict with anyone else's.
```
git pull origin main
```
You should regularly push your changes to your branch. Try and keep commits focused so if you need to go back to a previous commit it's easier to do.
```
git add -A
git commit -m "styled login page"
git push origin <your-branch>
```
When you're satisfied with your work and ready to merge your changes to main, head to github and [create a pull request (PR)](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request?tool=webui). Automated tests will run on your code. If they fail, you'll need to adjust your code until they pass. If there is a merge conflict, you'll need to resolve it before your branch can be merged. Finally, a group member will review your code. If they spot something funky, they may ask you to make a few changes. Once your PR is approved, your changes can be merged onto main. Thanks for your contribution!