# Progee-API

---

<a href="https://img.shields.io/badge/Version-1.0.1-informational"><img alt="Version" src="https://img.shields.io/badge/Version-1.0.1-informational"></a> <a href="https://img.shields.io/badge/Java-100%25-red"><img alt="Java use" src="https://img.shields.io/badge/Java-100%25-red"></a> <a href="https://img.shields.io/badge/Test coverage-86%25-yellow"><img alt="Test coverage" src="https://img.shields.io/badge/Test coverage-86%25-yellow"></a> <a href="https://img.shields.io/badge/Used-Spring%20Boot-success"><img alt="Spring Boot use" src="https://img.shields.io/badge/Used-Spring%20Boot-success"></a> <a href="https://img.shields.io/badge/Used-PostgreSQL-success"><img alt="PostgreSQL use" src="https://img.shields.io/badge/Used-PostgreSQL-success"></a> <a href="https://img.shields.io/badge/State-stable-success"><img alt="Project state" src="https://img.shields.io/badge/State-stable-success"></a> <a href="https://img.shields.io/badge/Deploy-deployed (heroku)-success"><img alt="Deploy state" src="https://img.shields.io/badge/Deploy-deployed (heroku)-success"></a>


## Table of Contents

- [Description](#description)
- [API docs](#api-docs)
- [Database relations map](#database-relations-map)
- [Frontend (Android client)](#frontend-android-client)
- [Tech Stack](#tech-stack)
- [Usage](#usage)
- [Contribution](#contribution)
- [Licenses](#licenses)
- [Contact Information](#contact-information)

## Description

Hi! This is **Progee-API v1**. 
Progee-API is a REST API for providing information about programming languages, and frameworks, their popularity, and respect for them among the community of developers (with reviews and scores). 
Currently working features: **auth, languages, frameworks, reviews, dynamic roles with permissions**.

## API docs

Documentation for the Progee API is available at Postman Documenter: [See docs](https://documenter.getpostman.com/view/14256972/UzBgwAPQ)

Or you can download Postman collection: [Download](https://github.com/KhusainovFarrukh/Progee-API/blob/master/Progee-API%20(v1).postman_collection.json)

Progee API is currently deployed and working. Base url is: https://progee-api-v1.herokuapp.com (See API docs above to understand where to use base-url)

## Database relations map

Progee API includes 5 main entities: Language (for programming language), Framework (for framework of programming language), AppUser (for user of application), Role (for signed user's role) and Review (for users' review about programming language).
And here is relations between tables of PostgreSQL database:

![progee-db-relations](https://user-images.githubusercontent.com/68108113/195850414-389e10cc-640a-45f9-b425-899d07bd9497.png)

## Frontend (Android client)

Currently, there is Android client app for testing Progee-API main features. Progee-Android is in alpha state now. See [GitHub repository of Progee-Android](https://github.com/khusainovfarrukh/progee-android) for additional information

## Tech stack

This REST API is developed using Java programming language and Spring Boot starter of Spring framework. The full tech stack:

  - [Spring Boot](https://spring.io/projects/spring-boot)
  - [Spring Security](https://spring.io/projects/spring-security) for authentication and authorization
  - [JWT](https://auth0.com/docs/secure/tokens/json-web-tokens) for tokens and share security information between two parties — a client and a server
  - [AWS S3](https://aws.amazon.com/s3/) for storing images and other files
  - [JUnit 5](https://junit.org/junit5/) for unit and integration tests
  - [TestContainer](https://www.testcontainers.org/) for using Docker images in integration tests
  - [Localstack](https://github.com/localstack/localstack) for mocking AWS services in integration tests
  - [Spring Data JPA](https://spring.io/projects/spring-data-jpa) for data access layers
  - [PostgreSQL](https://www.postgresql.org/) for Database
  - [FlywayDB](https://flywaydb.org/) for Database migrations
  - [Lombok](https://projectlombok.org/) for reducing boilerplate code
  - and etc.

## Usage

**Attention!** This project uses environment variables for some configuration properties. Define your environment variables in application.yml file. Otherwise default values will be used.

  - JWT_SECRET
  - DEFAULT_ENCRYPTED_PASSWORD
  - AWS_ACCESS_KEY
  - AWS_SECRET_KEY

**Hint**: Get AWS access and secret keys from your [Amazon Web Services console](https://docs.aws.amazon.com/powershell/latest/userguide/pstools-appendix-sign-up.html)

## Contribution

If you would like to contribute to this project reach out to me. Contact Information can be found below or by clicking on the 'Contact-Information' link provided in the Table of Contents.

## Licenses

Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   
## Contact Information

- Email Address: farrukhbekkhusainov@gmail.com
- [Telegram profile](https://t.me/farruxxusainov)
- [GitHub profile](https://github.com/KhusaiovFarrukh)
