# Agapsys REST Environment - AGRESTE

Welcome to the AGRESTE project.

**Attention:** If you obtained the source from a development branch be aware that  such branch can contain unstable and/or uncompilable code.

## Major features:

* Security (provided by [Agapsys RCF](https://github.com/agapsys/rcf))
* Transactional requests (automatic JPA transaction management)
* JPA query builders (provided by [Agapsys JPA Utils](https://github.com/agapsys/jpa-utils))
* REST endpoints mapping to servlet methods (provided by [Agapsys RCF](https://github.com/agapsys/rcf))
* Singleton management for application **Services** and **Modules** (provided by [Agapsys Web Application Toolkit](https://github.com/agapsys/web-app-toolkit))

## Compiling

The first build may take a long time as Maven downloads all the dependencies.

## Dependencies

AGRESTE project requires Java 7 compatible runtime and depends on the following external libraries:

* Agapsys REST Controller Framework (https://github.com/agapsys/rcf)
* Agapsys JPA Utils (https://github.com/agapsys/jpa-utils)
* Agapsys Web Application Toolkit (https://github.com/agapsys/web-app-toolkit)
* Apache Commons FileUpload (https://commons.apache.org/proper/commons-fileupload/)
* Hibernate JPA provider (http://hibernate.org/orm/)

For detailed information on external dependencies please see *pom.xml*.

## Licensing

AGRESTE project is licensed under the **Apache License 2.0**. See the files called *LICENSE* and *NOTICE* for more information.

## Contact

For general information visit the main project site at https://github.com/agapsys/agreste
