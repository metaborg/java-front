package mb.jar_analyzer.deps;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import mb.jar_analyzer.TypeUtil;
import mb.jar_analyzer.jar.ClassProcessor;

public class DependencyCollector extends ClassVisitor implements ClassProcessor {

    private final Predicate<String> filter;
    private final boolean includeCode;

    private final Set<String> visited;
    private final Map<String, String> nested;
    private SetMultimap<String, String> dependencies;

    private String currentName;

    public DependencyCollector(Predicate<String> filter, boolean includeCode) {
        super(Opcodes.ASM9);
        this.filter = filter;
        this.includeCode = includeCode;
        this.visited = new HashSet<>();
        this.nested = new HashMap<>();
        this.dependencies = HashMultimap.create();
    }

    public Set<String> getVisited() {
        return Collections.unmodifiableSet(Sets.difference(visited, nested.keySet()));
    }

    public SetMultimap<String, String> getDependencies() {
        return Multimaps.unmodifiableSetMultimap(dependencies);
    }

    @Override public void accept(ClassReader classReader) {
        classReader.accept(this, 0);
    }

    @Override public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaceNames) {
        currentName = name;
        if(!visited.add(name)) {
            throw new IllegalArgumentException("Class " + name + " already visited.");
        }
        if(superName != null) {
            addDependency(superName);
        }
        if(interfaceNames != null) {
            for(String interfaceName : interfaceNames) {
                addDependency(interfaceName);
            }
        }
    }

    @Override public void visitOuterClass(String ownerName, String name, String desc) {
        // ownerName
    }

    @Override public void visitInnerClass(String name, String outerName, String innerName, int access) {
        final String enclosingName = outerName != null ? outerName : currentName;
        if(!name.equals(enclosingName)) {
            if(enclosingName.equals(currentName)) {
                // merge outgoing dependencies
                for(String v : dependencies.removeAll(name)) {
                    if(!v.equals(currentName)) {
                        dependencies.put(currentName, v);
                    }
                }
                // substitute incoming dependencies
                if(dependencies.containsValue(name)) {
                    for(String k : ImmutableSet.copyOf(dependencies.keySet())) {
                        if(dependencies.remove(k, name)) {
                            if(!k.equals(currentName)) {
                                dependencies.put(k, currentName);
                            }
                        }
                    }
                }
                // register nesting
                nested.put(name, currentName);
            } else {
                addDependency(enclosingName);
            }
        }
    }

    @Override public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        addDependency(desc);
        return new FieldVisitor(this.api) {

        };
    }

    @Override public MethodVisitor visitMethod(int access, String name, String desc, String signature,
            String[] exceptions) {
        final Type type = Type.getMethodType(desc);
        addDependency(type.getReturnType().getInternalName());
        for(Type paramType : type.getArgumentTypes()) {
            addDependency(paramType.getInternalName());
        }
        if(exceptions != null) {
            for(String exception : exceptions) {
                addDependency(exception);
            }
        }
        return !includeCode ? null : new MethodVisitor(this.api) {

            @Override public void visitFieldInsn(int opcode, String ownerName, String name, String desc) {
                addDependency(ownerName);
                addDependency(desc);
            }

            @Override public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
                    int index) {
                addDependency(desc);
            }

            @Override public void visitMethodInsn(int opcode, String ownerName, String name, String desc) {
                addDependency(ownerName);
                Type type = Type.getMethodType(desc);
                addDependency(type.getReturnType().getInternalName());
                for(Type param : type.getArgumentTypes()) {
                    addDependency(param.getInternalName());
                }
            }

            @Override public void visitMethodInsn(int opcode, String ownerName, String name, String desc, boolean itf) {
                addDependency(ownerName);
                Type type = Type.getMethodType(desc);
                addDependency(type.getReturnType().getInternalName());
                for(Type param : type.getArgumentTypes()) {
                    addDependency(param.getInternalName());
                }
            }

            @Override public void visitMultiANewArrayInsn(String desc, int dims) {
                addDependency(desc);
            }

            @Override public void visitTypeInsn(int opcode, String type) {
                addDependency(type);
            }

            @Override public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                addDependency(type);
            }

            @Override public void visitInvokeDynamicInsn(String name, String desc, Handle bootstrapMethodHandle,
                    Object... bootstrapMethodArguments) {
                Type type = Type.getMethodType(desc);
                addDependency(type.getReturnType().getInternalName());
                for(Type param : type.getArgumentTypes()) {
                    addDependency(param.getInternalName());
                }
            }

            @Override public void visitLdcInsn(Object value) {
                if(value instanceof Type) {
                    addDependency(((Type) value).getInternalName());
                }
            }

        };
    }

    @Override public void visitEnd() {
        currentName = null;
    }

    private void addDependency(String typeOrName) {
        if(typeOrName == null) {
            return;
        }
        String name = null;
        try {
            name = TypeUtil.getTypeName(Type.getType(typeOrName)).orElse(null);
        } catch(IllegalArgumentException ex) {
            name = typeOrName;
        }
        if(name == null) {
            return;
        }
        final String fromName = nested.getOrDefault(currentName, currentName);
        final String toName = nested.getOrDefault(name, name);
        if(!fromName.equals(toName) && filter.test(name)) {
            dependencies.put(fromName, toName);
        }
    }

}