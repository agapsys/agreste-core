# Agapsys REST Environment - AGRESTE

Welcome to the AGRESTE project.

**Attention:** If you obtained the source from a development branch be aware that  such branch can contain unstable and/or uncompilable code.

## Major features:

* Security
* JPA transactions
* JPA query builders
* REST endpoints mapping to servlet methods
* Singleton management for application components:
    * Services
    * Modules

## Compiling

The first build may take a long time as Maven downloads all the dependencies.

## Dependencies

AGRESTE project requires Java 7 compatible runtime and depends on the following external libraries:

* Google GSON (https://github.com/google/gson)
* Agapsys Web Security Framework (https://github.com/agapsys/web-security-framework)
* Agapsys Console Utilities (https://github.com/agapsys/console-utils)
* Agapsys Simple HTTP client (https://github.com/agapsys/simple-http-client)
* Agapsys Action Dispatcher (https://github.com/agapsys/action-dispatcher)
* Agapsys JPA Utils (https://github.com/agapsys/jpa-utils)
* Agapsys Web Application Toolkit (https://github.com/agapsys/web-app-toolkit)
* Apache Commons FileUpload (https://commons.apache.org/proper/commons-fileupload/)
* Hibernate JPA provider (http://hibernate.org/orm/)
* JBCrypt (https://github.com/svenkubiak/jBCrypt)

For detailed information on external dependencies please see *pom.xml*.

## Licensing

AGRESTE project is licensed under the **Apache License 2.0**. See the files called *LICENSE* and *NOTICE* for more information.

## Contact

For general information visit the main project site at https://github.com/agapsys/agreste
