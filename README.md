[![Build status](http://buildfarm.metaborg.org/job/metaborg/job/java-front/job/master/badge/icon)](http://buildfarm.metaborg.org/job/metaborg/job/java-front/job/master/)

# Java Front

A declarative specification of Java 8's syntax in Spoofax.

## Usage

The maven artifacts for this project are supplied on our artifact server, see [the instructions on our website](http://www.metaborg.org/en/latest/source/dev/maven.html#spoofax-maven-artifacts).
To use the java-front plugin in Eclipse you also need to install the plugin through [the update site hosted on our buildfarm](https://buildfarm.metaborg.org/job/metaborg/job/java-front/job/release/lastSuccessfulBuild/artifact/lang.java.eclipse.site/target/site/site.xml).

## Project Structure

Each directory contains specifications for a section of the Java language:

* arrays  - Syntax for array initializers
* classes - Class, method, constructor, field and enum declarations
* expressions - All expressions
* interfaces - Interface, abstract method, constant and annotation declarations
* lexical - Lexical syntax for identifiers, comments, layout, etc.
* literals - Boolean, numerical, string, and null literals
* names - References to definitions of names
* packages - Compilation unit, package, and import declarations
* statements - All statements
* types - Primitive and reference types

The project also contains tests with java files from the Spoofax codebase, and succeeding tests from the Java language specification.

## Deployment

Development (`-SNAPSHOT`) versions on the `master` branch are automatically deployed by the buildfarm.

To deploy a release version of java-front, set the versions in all `pom.xml` and `metaborg.yaml` files to the next release version (such as `0.2.0`), commit and push this version change to the `release` branch, and tag it with a version tag such as `v0.2.0`. The buildfarm will then build and deploy the release version.

The `update_version.sh` can help with this, although the maven versions plugin used is a bit slow/finicky. 

