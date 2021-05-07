package mb.jar_analyzer;

import java.io.PrintStream;
import java.util.Optional;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import mb.jar_analyzer.jar.ClassProcessor;

public class SummaryPrinter extends ClassVisitor implements ClassProcessor {

    private final PrintStream out;
    private String currentName;

    public SummaryPrinter(PrintStream out) {
        super(Opcodes.ASM9);
        this.out = out;
    }

    @Override public void accept(ClassReader classReader) {
        classReader.accept(this, 0);
    }

    @Override public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaceNames) {
        currentName = name;
        out.println(name);
        out.println("S " + superName);
        for(String intf : interfaceNames) {
            out.println("I " + intf);
        }
    }

    @Override public void visitOuterClass(String owner, String name, String desc) {
        out.println("O " + owner);
    }

    @Override public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if(outerName == null || outerName.equals(currentName)) {
            out.println("C " + innerName + " " + name + " (in " + outerName + ")");
        }
    }

    @Override public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        out.println("F " + name);
        final Type type = Type.getType(desc);
        getReferenceType(type).ifPresent(t -> {
            out.println("  T " + t);
        });
        return new FieldVisitor(this.api) {

        };
    }

    @Override public MethodVisitor visitMethod(int access, String name, String desc, String signature,
            String[] exceptions) {
        out.println("M " + name);
        final Type type = Type.getMethodType(desc);
        getReferenceType(type.getReturnType()).ifPresent(t -> {
            out.println("  R " + t);
        });
        for(Type param : type.getArgumentTypes()) {
            getReferenceType(param).ifPresent(t -> {
                out.println("  P " + t);
            });
        }
        if(exceptions != null) {
            for(String ex : exceptions) {
                final Type exType = Type.getType(ex);
                getReferenceType(exType).ifPresent(t -> {
                    out.println("  X " + t);
                });
            }
        }
        return new MethodVisitor(this.api) {

            @Override public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                out.println("  F " + owner);
            }

            @Override public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
                    int index) {
                final Type type = Type.getType(desc);
                getReferenceType(type).ifPresent(t -> {
                    out.println("  L " + t);
                });
            }

            @Override public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                out.println("  M " + owner + "#" + name);
            }

            @Override public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                out.println("  M " + owner + "#" + name);
            }

        };
    }

    @Override public void visitEnd() {
        out.println();
        currentName = null;
    }

    private static Optional<String> getReferenceType(Type type) {
        switch(type.getSort()) {
            case Type.ARRAY:
                return getReferenceType(type.getElementType());
            case Type.OBJECT:
                return Optional.of(type.getInternalName());
            default:
                return Optional.empty();
        }
    }

    private static boolean isReferenceType(Type type) {
        switch(type.getSort()) {
            case Type.ARRAY:
            case Type.OBJECT:
                return true;
            default:
                return false;
        }
    }

}