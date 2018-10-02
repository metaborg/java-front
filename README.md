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

To deploy a release version of java-front, merge `master` into `release` and use the `set_release_version.sh` script to update to the release version and latest released Spoofax version. Now commit and push this version change to the `release` branch, and tag it with a version tag such as `v0.2.0`. Remember to use `git push --tags` to push the tag. The buildfarm will build and deploy the release version when it receives the new tag.

Once a release is done `master` should be updated to a new development version. **Never** merge `release` into `master`, then the version numbers won't match the helper script. For updating the development version you can use the `update_develop_version.sh` script. It needs the current version (without `-SNAPSHOT`), a new version (without `-SNAPSHOT`) and the latest Spoofax nightly version (without `-SNAPSHOT`). 
