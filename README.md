[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)  ![Maven build](https://github.com/OsiriX-Foundation/karnak/workflows/Build/badge.svg?branch=master)
[![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=ncloc)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=reliability_rating)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=sqale_rating)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=security_rating)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=alert_status)](https://sonarcloud.io/dashboard?id=karnak)

Karnak is a DICOM gateway for data de-identification and DICOM attribute normalization.

Karnak manages a continuous DICOM flow with a DICOM listener as input and a DICOM and/or DICOMWeb as
output.

For more information, see the
online [Karnak user guide](https://osirix-foundation.github.io/karnak-documentation/)

# Application Features

## Gateway

- Allows to build mapping between a source and one or more destinations
- Each destination can be DICOM or DICOMWeb
- Filter the images providers by AETitle and/or hostname (ot ensure the authenticity of the source)

## de-identification

- Each destination can be configured with a specific de-identification profile
- Configuration for sending only specific SopClassUIDs
- [Build your own de-identification profile](https://osirix-foundation.github.io/karnak-documentation/docs/deidentification/profiles)
  or add modifications to the basic DICOM profile
- Import and export the de-identification profiles

# Build Karnak

Prerequisites:

- JDK 14
- Maven 3
- Code formatter: [google-java-format](https://github.com/google/google-java-format)

##### Karnak
1. Execute the maven command `mvn clean install` in the external-provider-interface directory.
2. Execute the maven command `mvn clean install -P production` in the root directory of the project.

# Run Karnak

To configure and run Karnak with docker-compose,
see [karnak-docker](https://github.com/OsiriX-Foundation/karnak-docker).

# Debug Karnak

## Debug in IntelliJ

- Launch the components needed by Karnak (see below "Configure locally Mainzelliste and Postgres
  database with docker-compose")
- Enable Spring and Spring Boot for the project
- Create a Spring Boot launcher from main of StartApplication.java
    - Working Directory must be the mvc directory
    - In VM Options, add `-Djava.library.path="/tmp/dicom-opencv"`
    - In Environment variables, add the following values. The following values work with our default
      configuration define with docker used for the development (see: "Configure locally
      Mainzelliste and Postgres database with docker-compose") :
        - Mandatory:
            - `ENVIRONMENT=DEV`
        - Optional:
            - `DB_PASSWORD=5!KAnN@%98%d`
            - `DB_PORT=5433`
            - `DB_USER=karnak`
            - `DB_NAME=karnak`
            - `DB_HOST=localhost`
            - `MAINZELLISTE_HOSTNAME=localhost`
            - `MAINZELLISTE_HTTP_PORT=8083`
            - `MAINZELLISTE_API_KEY=changeThisApiKey`
            - `KARNAK_ADMIN=admin`
            - `KARNAK_PASSWORD=admin`
            - `KARNAK_LOGS_MAX_FILE_SIZE=100MB`
            - `KARNAK_LOGS_MIN_INDEX=1`
            - `KARNAK_LOGS_MAX_INDEX=10`
            - `KARNAK_CLINICAL_LOGS_MAX_FILE_SIZE=100MB`
            - `KARNAK_CLINICAL_LOGS_MIN_INDEX=1`
            - `KARNAK_CLINICAL_LOGS_MAX_INDEX=10`
            - `IDP=undefined`
            - `OIDC_CLIENT_ID=undefined`
            - `OIDC_CLIENT_SECRET=undefined`
            - `OIDC_ISSUER_URI=undefined`

    Note: the tmp folder must be adapted according to your system and the dicom-opencv must the last folder.

<!--
## Debug in Eclipse - obsolete

 - Configure locally mainzelliste and Postgres database (see below)
 - From Eclipse Marketplace: install the latest Spring Tools
 - Create a Spring Boot App launcher from main of SartApplication.java
    - Copy the KARNAK environment variables in docker/.env and paste into the Environment tab of the launcher    
    - In the Arguments tab of the launcher, add in VM arguments: `-Djava.library.path="/tmp/dicom-opencv"`    
    Note: the tmp folder must be adapted according to your system and the dicom-opencv must the last folder.
-->

## Configure locally Mainzelliste and Postgres database with docker-compose

Minimum docker-compose version: **1.22**

- Go in the `docker` folder located in the root project folder.
- To configure third-party components used by karnak, please refer to these links:
    - [docker hub postgres](https://hub.docker.com/_/postgres)
    - [docker hub mainzelliste](https://hub.docker.com/r/osirixfoundation/karnak-mainzelliste)
- Adapt the values if necessary (copy `.env.example` into `.env` and modify it)
- Execute command:
    - start: `docker-compose up -d`
    - show the logs: `docker-compose logs -f`
    - stop: `docker-compose down`

# Docker

Minimum docker version: **19.03**

## Build with Dockerfile

Go on the root folder and launch the following command:

* Full independent build: `docker build -t local/karnak:latest -f Dockerfile .`
* Build from compile package:
    * `mvn clean install -P production`
    * `docker build -t local/karnak:latest -f mvc/src/main/docker/Dockerfile .`

## Run image from Docker Hub

See [karnak-docker](https://github.com/OsiriX-Foundation/karnak-docker)

## Docker environment variables

See [environment variables](https://github.com/OsiriX-Foundation/karnak-docker#environment-variables)

# Architecture

This project is divided in two parts:

- backend: spring data (entities, repositories, converters, validators), enums, 
        spring configurations, spring security, cache, spring services, models...
- frontend : Vaadin components:  logic services, graphic components, views

# Workflow

![Workflow](doc/karnak-workflow.svg)

# Pipeline

![Workflow](doc/karnak-pipeline.svg)
