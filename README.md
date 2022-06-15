# Progee-API

---

<a href="https://img.shields.io/badge/Version-0.0.1-informational"><img alt="Version" src="https://img.shields.io/badge/Version-0.0.1-informational"></a> <a href="https://img.shields.io/badge/Java-100%25-red"><img alt="Java use" src="https://img.shields.io/badge/Java-100%25-red"></a> <a href="https://img.shields.io/badge/Used-Spring%20Boot-success"><img alt="Spring Boot use" src="https://img.shields.io/badge/Used-Spring%20Boot-success"></a>

## Table of Contents

- [Description](#description)
- [API docs](#api-docs)
- [Database relations map](#database-relations-map)
- [Frontend (Android client)](#frontend-(android-client))
- [Tech Stack](#tech-stack)
- [Usage](#usage)
- [Contribution](#contribution)
- [Licenses](#licenses)
- [Contact Information](#contactin-formation)

## Description

Hi! This is **Progee-API v1**. 
Progee-API is a REST API for providing information about programming languages, and frameworks, their popularity, and respect for them among the community of developers (with reviews and scores). 
Currently working features: **languages, frameworks, reviews**.

## API docs

Documentation for the Progee API is available at Postman Documenter: [See docs](https://documenter.getpostman.com/view/14256972/UzBgwAPQ)

## Database realtions map

Progee API includes 4 main entities: Language (for programming language), Framework (for framework of programming language), AppUser (for user of application) and Review (users' review about programming language).
And here is relations between tables of MySQL database:

![progee-db-relations](https://user-images.githubusercontent.com/68108113/173758913-519f58af-5285-4658-953c-0a658963c909.png)

## Frontend (Android client)

_Soon._

## Tech stack

This REST API is developed using Java programming language and Spring Boot starter of Spring framework. The full tech stack:

  - **Spring Boot**
  - **MySQL** for Database
  - **Spring Data JPA** for data access layers
  - **Spring Security** for authentication and authorization
  - **JWT** for tokens and share security information between two parties — a client and a server
  - **Jakarta Validation API** for for expressing and validating application constraints
  - **Lombok** for reducing boilerplate code
  - and etc.

## Usage

**Attention!** This project for some configuration properties (username and password of MySQL, secret key of HMAC256 algorithm used to encode/decode JWT token) uses IntelliJ IDEA environment variables. Define your environtment variables or paste your values directly to places where environment variables are used:

- spring.datasource.username=${MYSQL_USERNAME} at application.properties: line 6
- spring.datasource.password=${MYSQL_PASSWORD} at application.properties: line 7
- System.getenv("MY_JWT_SECRET") at JWTUtils: line 34

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
- [Github profile](https://github.com/KhusaiovFarrukh)
