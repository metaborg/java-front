java-front
==========

Java editor created in Spoofax. The reposiory is structed as follows:

* java-front - The Spoofax project for the editor, containing a syntax definition, transformations, name binding specification, and type system specification for Java 3.
* java-tests - Test suite in SPT to test name binding and type analysis.
* java-examples - Example files for java-front.
* java-conformance - Conformance test of java-front against the Eclipse JDT.
* java-jar2index - Converts a jar file to an index, which is used by name binding to be able to refer to definitions inside jar files.
