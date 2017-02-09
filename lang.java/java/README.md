This is a declarative specification of Java's syntax.
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

In each directory, there are 2 possible subdirectories which contain an aspect of the language:

* syntax - Syntax definitions in SDF3
* trans - Transformation (usually desugaring) definitions in Stratego
