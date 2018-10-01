[![Build status](http://buildfarm.metaborg.org/job/metaborg/job/java-front/job/master/badge/icon)](http://buildfarm.metaborg.org/job/metaborg/job/java-front/job/master/)

This is a declarative specification of Java's 1.8 syntax.

# Usage

The maven artifacts for this project are supplied on our artifact server, see [the instructions on our website](http://www.metaborg.org/en/latest/source/dev/maven.html#spoofax-maven-artifacts). To use the java-front plugin in Eclipse you also need to install the plugin through [the update site hosted on our buildfarm](https://buildfarm.metaborg.org/job/metaborg/job/java-front/job/master/lastSuccessfulBuild/artifact/lang.java.eclipse.site/target/site/site.xml) (linked is the nightly build).

# Project Structure

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

# Contribute

Feel free to contribute with changes/improvements to the project.
