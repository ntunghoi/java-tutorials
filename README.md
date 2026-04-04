# Project Setup

```bash
gradle init --type java-application --dsl kotlin --overwrite --java-version 25 --project-name java-tutorials --package com.ntunghoi.tutorials --no-split-project --test-framework junit-jupiter --incubating
```

# Spring Boot

```bash
./gradlew bootRun
```

# PostgreSQL

```SQL
CREATE DATABASE java_tutorials;
\c java_tutorials;
CREATE SCHEMA token_exchange;
SET search_path TO token_exchange, public;
CREATE USER postgres WITH PASSWORD 'postgres';
ALTER DATABASE java_tutorials OWNER To postgres;
ALTER SCHEMA token_exchange OWNER To postgres;
```