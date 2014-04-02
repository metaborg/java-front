Declarative specification of Java's static semantics.
Syntax is specified in SDF, AST transformations in Stratego, name binding in NaBL, and type analysis in TS.

Each directory contains specifications for a section of the Java language:

* arrays  - Syntax for array initializers.
* classes - Class, method, constructor, field and enum declarations
* expressions - All expressions
* interfaces - Interface, abstract method, constant and annotation declarations
* lexical - Lexical syntax for identifiers, comments, layout, etc.
* literals - Boolean, numerical, string, and null literals
* names - References to definitions of names
* packages - Compilation unit, package, and import declarations
* statements - All statements
* types - Primitive and reference types, widening and narrowing of these types, numerical promotions, and other kinds of conversions between types

In each directory, there are 4 possible subdirectories which contain an aspect of the static semantics:

* syntax - Syntax definitions in SDF2
* trans - Transformation definitions in Stratego
* names - Name binding definitions in NaBL
* types - Type system definitions in TS

The conformance test against the JDT is located at: [../../../java-conformance](java-conformance).
The JAR to index converter is located at: [../../../java-jar2index](jar2index).
The full specification can be found in PDF format at: [https://github.com/metaborg/java-front/releases/download/java-3-spec-1.0/fullspec.pdf](fullspec.pdf).

Note that the name of this directory, Java-1.5, only indicates that the syntax is of Java 5.
Our name and type specification does __not__ support the following features from Java 5 yet:

* Enum declarations
* Annotation declarations
* Type parameters (generics)
* Boxing/unboxing
* Foreach loop
* Static imports
