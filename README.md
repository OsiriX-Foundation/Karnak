[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0) ![Maven build](https://github.com/OsiriX-Foundation/karnak/workflows/Build/badge.svg?branch=master)

[![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=ncloc)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=reliability_rating)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=sqale_rating)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=security_rating)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=alert_status)](https://sonarcloud.io/dashboard?id=karnak)

Karnak is a DICOM gateway for data de-identification and DICOM attribute normalization.

Karnak manages a continuous DICOM flow with a DICOM listener as input and a DICOM and/or DICOMWeb as output.

[Karnak documentation](https://osirix-foundation.github.io/karnak-documentation/) is available online.

# Application Features

## Gateway
- Allows to build mapping between a source and one or more destinations
- Each destination can be DICOM or DICOMWeb
- Filter the images providers by AETitle and/or hostname (ot ensure the authenticity of the source)

## de-identification
- Each destination can be configured with a specific de-identification profile
- Configuration for sending only specific SopClassUIDs
- [Build your own de-identification profile](https://github.com/OsiriX-Foundation/karnak-docker/blob/master/profileExample/README.md) or add modifications to the basic DICOM profile
- Import and export the de-identification profiles

# Build Karnak

Prerequisites:
- JDK 14
- Maven 3
- Package dcm4che20
- Package weasis-dicom-tools 
- Code formatter: [google-java-format](https://github.com/google/google-java-format)

Use the following commands to build the two dependencies (dcm4che20 and weasis-dicom-tools):

##### dcm4che20

1. `git clone https://github.com/nroduit/dcm4che20.git`
1. `git checkout image`
1. `mvn source:jar install`

##### weasis-dicom-tools

1. `git clone https://github.com/nroduit/weasis-dicom-tools.git`
1. `git checkout dcm4che6`
1. `mvn clean install`


##### Karnak

Execute the maven command `mvn clean install -P production` in the root directory of the project.

# Run Karnak

To configure and run Karnak with docker-compose, see [karnak-docker](https://github.com/OsiriX-Foundation/karnak-docker).

# Debug Karnak

## Debug in IntelliJ

 - Launch the components needed by Karnak (see below "Configure locally Mainzelliste and Postgres database with docker-compose")
 - Enable Spring and Spring Boot for the project
 - Create a Spring Boot launcher from main of StartApplication.java
    - Working Directory must be the mvc directory
    - In VM Options, add `-Djava.library.path="/tmp/dicom-opencv"`
    - In Environment variables, add the following values. 
    The following values work with our default configuration define with docker used for the development (see: "Configure locally Mainzelliste and Postgres database with docker-compose") :
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
        - `MAINZELLISTE_ID_TYPES=pid`
        - `MAINZELLISTE_API_KEY=changeThisApiKey`
        - `KARNAK_ADMIN=admin`
        - `KARNAK_PASSWORD=admin`
        - `KARNAK_LOGS_MAX_FILE_SIZE=100MB`
        - `KARNAK_LOGS_MIN_INDEX=1`
        - `KARNAK_LOGS_MAX_INDEX=10`
        - `KARNAK_CLINICAL_LOGS_MAX_FILE_SIZE=100MB`
        - `KARNAK_CLINICAL_LOGS_MIN_INDEX=1`
        - `KARNAK_CLINICAL_LOGS_MAX_INDEX=10`
   

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

This project provides two modules:
 - karnak-data: the data model for persistence of the gateway configuration 
 - karnak-mvc: the services and UI for updating the data model

# Workflow

![Workflow](doc/karnak-workflow.svg)

# Pipeline

![Workflow](doc/karnak-pipeline.svg)
