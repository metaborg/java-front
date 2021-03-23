package mb.jar_analyzer.jar;

import org.objectweb.asm.ClassReader;

@FunctionalInterface
public interface ClassProcessor {

    void accept(ClassReader classReader);

}