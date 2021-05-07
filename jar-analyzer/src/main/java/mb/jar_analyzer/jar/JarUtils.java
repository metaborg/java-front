package mb.jar_analyzer.jar;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class JarUtils {

    public static final void processJarClasses(Iterable<File> files, ClassProcessor processor) throws IOException {
        for(File file : files) {
            try(JarFile jar = new JarFile(file)) {
                final Enumeration<JarEntry> entries = jar.entries();
                while(entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    if(entry.getName().endsWith(".class")) {
                        final ClassReader classReader = new ClassReader(jar.getInputStream(entry));
                        processor.accept(classReader);
                    }
                }
            }
        }
    }

    public static final Map<String, ClassNode> readJarClasses(Iterable<File> files) throws IOException {
        final Map<String, ClassNode> classes = new HashMap<>();
        processJarClasses(files, cr -> {
            final ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.SKIP_CODE & ClassReader.SKIP_DEBUG & ClassReader.SKIP_FRAMES);
            if(classes.put(classNode.name, classNode) != null) {
                throw new IllegalArgumentException("Duplicate class " + classNode.name);
            }
        });
        return classes;
    }

    public static boolean isJavaClass(String name) {
        // @formatter:off
        return name.startsWith("java/")
//                || name.startsWith("javax/")
                ;
        // @formatter:on
    }

    public static boolean isJDKClass(String name) {
        // @formatter:off
        return name.startsWith("java/")
                || name.startsWith("javax/")
                || name.startsWith("sun/")
                || name.startsWith("jdk/")
                || name.startsWith("com/sun/")
                || name.startsWith("com/apple/")
                || name.startsWith("com/oracle/")
                || name.startsWith("org/w3c/")
                || name.startsWith("org/xml/")
                || name.startsWith("org/omg/")
                || name.startsWith("org/ietf/")
                || name.startsWith("org/jcp/")
                || name.startsWith("apple/")
                ;
        // @formatter:on
    }

}