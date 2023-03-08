package mb.jar_analyzer.stxlib;


import static mb.nabl2.terms.build.TermBuild.B;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.metaborg.util.Ref;
import org.metaborg.util.functions.Action2;
import org.metaborg.util.functions.Predicate1;
import org.metaborg.util.tuple.Tuple2;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.SimpleTextTermWriter;
import org.spoofax.terms.io.TermWriter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;

import mb.jar_analyzer.jar.JarUtils;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.scopegraph.oopsla20.IScopeGraph;
import mb.scopegraph.oopsla20.reference.ScopeGraph;
import mb.statix.concurrent.IStatixLibrary;
import mb.statix.concurrent.StatixLibrary;
import mb.statix.scopegraph.Scope;

public class StxLibCommand implements Runnable {

    private static final ITerm PKG_REL = makeLabel("java/names/PackageNames!pkg");
    private static final ITerm TYPE_REL = makeLabel("java/names/TypeNames!type");
    private static final ITerm THIS_TYPE_REL = makeLabel("java/names/TypeNames!thisType");
    private static final ITerm EXPRESSION_REL = makeLabel("java/names/ExpressionNames!var");
    private static final ITerm METHOD_REL = makeLabel("java/names/MethodNames!mthd");
    private static final ITerm RETURN_REL = makeLabel("java/names/MethodNames!return");
    private static final ITerm ELEMENT_TYPE_REL = makeLabel("java/types/ReferenceTypes!elementType");
    private static final ITerm WITH_KIND_REL = makeLabel("java/types/Main!withKind");
    private static final ITerm WITH_TYPE_REL = makeLabel("java/types/Main!withType");
    private static final ITerm BOX_REL = makeLabel("java/types/Conversions!box");
    private static final ITerm TYPENAME_REL = makeLabel("java/JRE!typeName");

    private static final ITerm EXTENDS_EDGE = makeLabel("java/names/Main!EXTENDS");
    private static final ITerm IMPLEMENTS_EDGE = makeLabel("java/names/Main!IMPLEMENTS");
    private static final ITerm STATIC_MEMBERS_EDGE = makeLabel("java/names/Main!STATIC_MEMBERS");

    private static final ITerm ARRAY_KIND = B.newAppl("ARRAY");
    private static final ITerm CLASS_KIND = B.newAppl("CLASS");
    private static final ITerm INTERFACE_KIND = B.newAppl("INTF");

    private static final String ARGS = "ARGS";
    private static final String VARARGS = "VARARGS";

    private static final String INIT = "<init>";

    // @formatter:off
    private static final Map<String, ITerm> boxedTypes = ImmutableMap.<String, ITerm>builder()
        .put("java/lang/Boolean",   B.newAppl("BOOLEAN"))
        .put("java/lang/Character", B.newAppl("CHAR"))
        .put("java/lang/Byte",      B.newAppl("BYTE"))
        .put("java/lang/Short",     B.newAppl("SHORT"))
        .put("java/lang/Integer",   B.newAppl("INT"))
        .put("java/lang/Long",      B.newAppl("LONG"))
        .put("java/lang/Float",     B.newAppl("FLOAT"))
        .put("java/lang/Double",    B.newAppl("DOUBLE"))
        .build();
    // @formatter:on

    // @formatter:off
    private static final Set<String> jreTypes = ImmutableSet.<String>builder()
        .add("java/lang/String")
        .add("java/lang/Throwable")
        .add("java/lang/Enum")
        .add("java/lang/Iterable")
        .build();
    // @formatter:on

    ////////////////////////////////////////////////////////////////////////////

    private final List<File> files = new ArrayList<>();
    private File output = null;
    private Predicate1<String> include = className -> JarUtils.isJavaClass(className);

    public StxLibCommand(List<String> args) {
        Iterator<String> it = args.iterator();
        while(it.hasNext()) {
            String arg = it.next();
            switch(arg) {
                case "-o":
                case "--output":
                    if(!it.hasNext()) {
                        throw new IllegalArgumentException("Missing filename after -o/--output");
                    }
                    output = new File(it.next());
                    break;
                default:
                    files.add(new File(arg));
                case "--":
                    Streams.stream(it).forEach(f -> files.add(new File(f)));
                    break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    final Scope s_root = Scope.of("", "s_prj");

    private final Set<String> innerTypes = new HashSet<>();

    private final Map<String, Scope> types = new HashMap<>();
    private final Map<String, Scope> pkgs = new HashMap<>();

    final Set<Scope> allScopes = new HashSet<>();
    final IScopeGraph.Transient<Scope, ITerm, ITerm> scopeGraph = ScopeGraph.Transient.of();

    private final Map<String, ClassNode> classes = new HashMap<>();
    private final Deque<ClassNode> worklist = new ArrayDeque<>();

    @Override public void run() {
        try {
            classes.putAll(JarUtils.readJarClasses(files));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Found " + classes.size() + " classes");

        for(ClassNode classNode : classes.values()) {
            registerInnerClasses(classNode);
        }

        for(String className : classes.keySet()) {
            if(include.test(className) && !innerTypes.contains(className)) {
                getOrInitClass(className);
            }
        }

        while(!worklist.isEmpty()) {
            processClass(worklist.pop());
        }

        System.out.println("Included " + types.size() + " classes");

        for(String className : types.keySet()) {
            finalizeClass(className);
        }

        if(output != null) {
            final IStatixLibrary library = new StatixLibrary(ImmutableList.of(s_root), allScopes, scopeGraph.freeze());
            final ITerm libraryTerm = IStatixLibrary.toTerm(library);

            final StrategoTerms st = new StrategoTerms(new TermFactory());
            final TermWriter tw = new SimpleTextTermWriter();
            final IStrategoTerm librarySTerm = st.toStratego(libraryTerm);
            try {
                tw.writeToFile(librarySTerm, output);
            } catch(IOException | SecurityException e) {
                throw new RuntimeException("Writing library failed.", e);
            }
            System.out.println("Output written to " + output);
        } else {
            System.out.println("No output written.");
        }

        //        for(Map.Entry<String, ClassNode> classEntry : classes.entrySet()) {
        //            printSummary(classEntry.getValue());
        //        }
    }

    private void printSummary(ClassNode classNode) {
        System.out.println("CLS " + classNode.name + (innerTypes.contains(classNode.name) ? " (*)" : ""));
        if(classNode.signature != null) {
            System.out.println("SIG " + classNode.signature);
        } else {
            System.out.println("SUP " + classNode.superName);
            if(!classNode.interfaces.isEmpty()) {
                System.out.println("IFS " + classNode.interfaces.stream().collect(Collectors.joining(", ")));
            }
        }
        for(InnerClassNode innerClass : classNode.innerClasses) {
            if(innerClass.outerName != null && !innerClass.outerName.equals(classNode.name)) {
                continue; // imported inner class of different type
            }
            if(innerClass.innerName == null) {
                continue; // anonymous class, e.g. lambda
            }
            System.out.println("INN " + innerClass.innerName + " -> " + innerClass.name);
        }
        for(FieldNode field : classNode.fields) {
            if((field.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
                continue; // package or private field
            }
            if(field.signature != null) {
                System.out.println("FLD " + field.name + " " + field.signature);
            } else {
                System.out.println("FLD " + field.name + " " + field.desc);
            }
        }
        for(MethodNode method : classNode.methods) {
            if((method.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
                continue; // package or private method // FIXME is this safe with overload resolution?
            }
            if(method.signature != null) {
                System.out.println("MTD " + method.name + " " + method.signature);
            } else {
                System.out.println("MTD " + method.name + " " + method.desc);
            }

        }
        System.out.println();
    }

    private void registerInnerClasses(ClassNode classNode) {
        for(InnerClassNode innerClass : classNode.innerClasses) {
            if(isFakeInnerClass(innerClass, classNode)) {
                continue;
            }
            innerTypes.add(innerClass.name); // it seems this can occur multiple times, so no check if it was already done
            innerTypes.add(innerClass.name); // it seems this can occur multiple times, so no check if it was already done
        }
    }

    private Scope getOrInitClass(String className) {
        if(types.containsKey(className)) {
            return types.get(className);
        }
        if(!classes.containsKey(className)) {
            throw new IllegalStateException("Missing class " + className);
        }
        ClassNode classNode = classes.get(className);

        // create type scope for the class
        final Scope s_ty = newTypeScope();
        if(types.put(className, s_ty) != null) {
            throw new IllegalArgumentException("Class " + className + " already initialized.");
        }

        // put in worklist
        worklist.push(classNode);

        return s_ty;
    }

    private void processClass(ClassNode classNode) {
        final String className = classNode.name;
        final Scope s_ty = types.get(className);
        final Scope s_static = newTypeScope();
        scopeGraph.addEdge(s_ty, STATIC_MEMBERS_EDGE, s_static);

        final ITerm kind;
        if((classNode.access & Opcodes.ACC_INTERFACE) != 0) {
            kind = INTERFACE_KIND;
        } else {
            kind = CLASS_KIND;
        }

        addDecl(s_ty, WITH_TYPE_REL, makeREF(s_ty));
        addDecl(s_ty, WITH_KIND_REL, kind);
        addDecl(s_ty, THIS_TYPE_REL, B.newTuple(makeId("<this>"), s_ty)); // FIXME how to get the simple name of this class?

        if(jreTypes.contains(className)) {
            addDecl(s_ty, TYPENAME_REL, B.newString(className));
        }
        if(boxedTypes.containsKey(className)) {
            addDecl(s_ty, BOX_REL, boxedTypes.get(className));
        }

        final Map<String, ITerm> typeVars = new HashMap<>();

        // process signature
        if(classNode.signature != null) {
            // ClassSignature = ( visitFormalTypeParameter visitClassBound? visitInterfaceBound* )* ( visitSuperclass visitInterface* )
            new SignatureReader(classNode.signature).accept(new SignatureVisitor(Opcodes.ASM9) {

                private String tvarName;
                private boolean tvarBound;

                @Override public void visitFormalTypeParameter(String name) {
                    tvarName = name;
                    tvarBound = false;
                    typeVars.put(tvarName, objectType()._1());
                }

                @Override public SignatureVisitor visitClassBound() {
                    return SignatureType(typeVars, (type, typeScope) -> {
                        if(!tvarBound) {
                            tvarBound = true;
                            typeVars.put(tvarName, type);
                        } else {
                            System.out.println(className + "<" + tvarName + "> ignored class bound " + type);
                        }
                    });
                }

                @Override public SignatureVisitor visitInterfaceBound() {
                    return SignatureType(typeVars, (type, typeScope) -> {
                        if(!tvarBound) {
                            tvarBound = true;
                            typeVars.put(tvarName, type);
                        } else {
                            System.out.println(className + "<" + tvarName + "> ignored interface bound " + type);
                        }
                    });
                }

                @Override public SignatureVisitor visitSuperclass() {
                    return SignatureType(typeVars, (type, typeScope) -> {
                        scopeGraph.addEdge(s_ty, StxLibCommand.EXTENDS_EDGE, typeScope);
                    });
                }

                @Override public SignatureVisitor visitInterface() {
                    return SignatureType(typeVars, (type, typeScope) -> {
                        scopeGraph.addEdge(s_ty, IMPLEMENTS_EDGE, typeScope);
                    });
                }

            });
        } else {
            if(classNode.superName != null) { // java.lang.Object has no super
                final Scope s_super = getOrInitClass(classNode.superName);
                scopeGraph.addEdge(s_ty, EXTENDS_EDGE, s_super); // FIXME Also if classNode is an interface?
            }

            if(classNode.interfaces != null) {
                for(String intf : classNode.interfaces) {
                    final Scope s_intf = types.get(intf);
                    scopeGraph.addEdge(s_ty, IMPLEMENTS_EDGE, s_intf);
                }
            }
        }

        for(InnerClassNode innerClass : classNode.innerClasses) {
            if(isFakeInnerClass(innerClass, classNode)) {
                continue;
            }

            if(innerClass.innerName == null) {
                continue; // anonymous inner class, don't even trigger getOrInitClass
            }

            if((innerClass.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
                continue; // package or private inner type
            }

            final Scope s_inner = getOrInitClass(innerClass.name);
            final Scope s_def = (innerClass.access & Opcodes.ACC_STATIC) == 0 ? s_ty : s_static;
            aliasType(s_def, innerClass.innerName, s_inner);
        }

        for(FieldNode field : classNode.fields) {
            if((field.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
                continue; // package or private field
            }

            final ITerm fieldType;
            if(field.signature != null) {
                fieldType = sigType(typeVars, field.signature);
            } else {
                fieldType = descType(Type.getType(field.desc));
            }
            final Scope s_def = (field.access & Opcodes.ACC_STATIC) == 0 ? s_ty : s_static;
            declareVar(s_def, field.name, fieldType);
        }

        for(MethodNode method : classNode.methods) {
            if((method.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
                continue; // package or private method // FIXME is this safe with overload resolution?
            }

            final Map<String, ITerm> mthdTypeVars = Maps.newHashMap(typeVars);

            final Ref<ITerm> retType = new Ref<>();
            final List<ITerm> paramTypes = new ArrayList<>();
            if(method.signature != null) {
                // MethodSignature = ( visitFormalTypeParameter visitClassBound? visitInterfaceBound* )* (visitParameterType* visitReturnType visitExceptionType*)
                new SignatureReader(method.signature).accept(new SignatureVisitor(Opcodes.ASM9) {

                    private String tvarName;
                    private boolean tvarBound;

                    @Override public void visitFormalTypeParameter(String name) {
                        tvarName = name;
                        tvarBound = false;
                        mthdTypeVars.put(name, objectType()._1());
                    }

                    @Override public SignatureVisitor visitClassBound() {
                        return SignatureType(mthdTypeVars, (type, typeScope) -> {
                            if(!tvarBound) {
                                tvarBound = true;
                                mthdTypeVars.put(tvarName, type);
                            } else {
                                System.out.println(className + "#" + method.name + "<" + tvarName + ">"
                                        + " ignored interface bound " + type);
                            }
                        });
                    }

                    @Override public SignatureVisitor visitInterfaceBound() {
                        return SignatureType(mthdTypeVars, (type, typeScope) -> {
                            if(!tvarBound) {
                                tvarBound = true;
                                mthdTypeVars.put(tvarName, type);
                            } else {
                                System.out.println(className + "#" + method.name + "<" + tvarName + ">"
                                        + " ignored interface bound " + type);
                            }
                        });
                    }

                    @Override public SignatureVisitor visitParameterType() {
                        return new SigType(mthdTypeVars) {
                            @Override public void setType(ITerm type, Scope typeScope) {
                                paramTypes.add(type);
                            }
                        };
                    }

                    @Override public SignatureVisitor visitReturnType() {
                        return new ReturnType(mthdTypeVars) {
                            @Override public void setReturnType(ITerm type, Scope typeScope) {
                                retType.set(type);
                            }
                        };
                    }

                    @Override public SignatureVisitor visitExceptionType() {
                        return new ReturnType(mthdTypeVars) {
                            @Override public void setReturnType(ITerm type, Scope typeScope) {
                            }
                        };
                    }

                });
            } else {
                final Type methodType = Type.getMethodType(method.desc);
                if(method.name.equals(INIT)) {
                    retType.set(makeREF(s_ty));
                } else {
                    retType.set(returnType(methodType.getReturnType()));
                }
                for(Type argType : methodType.getArgumentTypes()) {
                    paramTypes.add(descType(argType));
                }
            }
            final Scope s_def = (method.access & Opcodes.ACC_STATIC) == 0 ? s_ty : s_static;
            final ITerm params;
            if((method.access & Opcodes.ACC_VARARGS) == 0) {
                params = B.newAppl(ARGS, B.newList(paramTypes));
            } else {
                params = B.newAppl(VARARGS, B.newList(paramTypes.subList(0, paramTypes.size() - 1)),
                        paramTypes.get(paramTypes.size() - 1));
            }
            declareMethod(s_def, method.name, params, retType.get());
        }

    }

    private void finalizeClass(String className) {
        final Scope s_ty = types.get(className);

        if(!innerTypes.contains(className) && include.test(className)) {
            final String simpleName = className.substring(className.lastIndexOf('/') + 1);
            final String pkgName = className.substring(0, className.lastIndexOf('/'));
            // create the package
            final Scope s_pkg = getOrInitPkg(pkgName);
            aliasType(s_pkg, simpleName, s_ty);
        }
    }

    private Scope getOrInitPkg(String pkgName) {
        Scope s_pkg;
        if((s_pkg = pkgs.get(pkgName)) != null) {
            return s_pkg;
        }
        s_pkg = newPkgScope();
        pkgs.put(pkgName, s_pkg);

        final String name;
        final Scope s_parent;
        final int idx = pkgName.lastIndexOf('/');
        if(idx == -1) {
            name = pkgName;
            s_parent = s_root;
        } else {
            name = pkgName.substring(pkgName.lastIndexOf('/') + 1);
            s_parent = getOrInitPkg(pkgName.substring(0, pkgName.lastIndexOf('/')));
        }
        declarePkg(s_parent, name, s_pkg);

        return s_pkg;
    }

    ////////////////////////////////////////////////////////////////////////////

    private int typeScopeCounter = 0;

    private Scope newTypeScope() {
        final Scope s_ty = Scope.of("", "s_ty-" + typeScopeCounter++);
        scopeGraph.setDatum(s_ty, s_ty);
        allScopes.add(s_ty);
        return s_ty;
    }

    private void aliasType(Scope s, String name, Scope s_ty) {
        addDecl(s, TYPE_REL, B.newTuple(makeId(name), s_ty));
    }

    ////////////////////////////////////////////////////////////////////////////

    private int pkgScopeCounter = 0;

    private Scope newPkgScope() {
        final Scope s_pkg = Scope.of("", "s_pkg-" + pkgScopeCounter++);
        scopeGraph.setDatum(s_pkg, s_pkg);
        allScopes.add(s_pkg);
        return s_pkg;
    }

    private void declarePkg(Scope s, String name, Scope s_pkg) {
        addDecl(s, PKG_REL, B.newTuple(makeId(name), s_pkg));
    }

    ////////////////////////////////////////////////////////////////////////////

    private int varScopeCounter = 0;

    private Scope newVarScope() {
        final Scope s_var = Scope.of("", "s_var-" + varScopeCounter++);
        scopeGraph.setDatum(s_var, s_var);
        allScopes.add(s_var);
        return s_var;
    }

    private void declareVar(Scope s, String name, ITerm type) {
        final Scope s_var = newVarScope();
        addDecl(s_var, WITH_TYPE_REL, type);
        addDecl(s, EXPRESSION_REL, B.newTuple(makeId(name), s_var));
    }

    ////////////////////////////////////////////////////////////////////////////

    private int mthdScopeCounter = 0;

    private Scope newMthdScope() {
        final Scope s_mthd = Scope.of("", "s_mthd-" + mthdScopeCounter++);
        scopeGraph.setDatum(s_mthd, s_mthd);
        allScopes.add(s_mthd);
        return s_mthd;
    }

    private void declareMethod(Scope s, String name, ITerm params, ITerm retType) {
        final Scope s_mthd = newMthdScope();
        addDecl(s_mthd, RETURN_REL, retType);
        final ITerm id = makeId(name);
        addDecl(s, METHOD_REL, B.newTuple(id, id, params, s_mthd));
    }

    ////////////////////////////////////////////////////////////////////////////

    private int declScopeCounter = 0;

    private void addDecl(Scope scope, ITerm label, ITerm decl) {
        final Scope s_decl = newDeclScope(decl);
        scopeGraph.addEdge(scope, label, s_decl);
    }

    private Scope newDeclScope(ITerm decl) {
        final Scope s_decl = Scope.of("", "d-" + declScopeCounter++);
        scopeGraph.setDatum(s_decl, decl);
        allScopes.add(s_decl);
        return s_decl;
    }

    private boolean isFakeInnerClass(InnerClassNode innerClass, ClassNode outerClass) {
        return innerClass.name.equals(outerClass.name)
                || (innerClass.outerName != null && !innerClass.outerName.equals(outerClass.name));
    }

    private ITerm returnType(Type desc) {
        switch(desc.getSort()) {
            case Type.VOID:
                return B.newAppl("VOID");
            default:
                return B.newAppl("TYPED", descType(desc));
        }
    }

    private abstract class SigType extends SignatureVisitor {

        // TypeSignature = visitBaseType | visitTypeVariable | visitArrayType | ( visitClassType visitTypeArgument* ( visitInnerClassType visitTypeArgument* )* visitEnd )

        private final Map<String, ITerm> typeVars;

        private SigType(Map<String, ITerm> typeVars) {
            super(Opcodes.ASM9);
            this.typeVars = typeVars;
        }

        ITerm type;
        Scope typeScope;

        public abstract void setType(ITerm type, Scope typeScope);

        @Override public void visitBaseType(char descriptor) {
            switch(descriptor) {
                case 'Z':
                    setType(B.newAppl("BOOLEAN"), null);
                    break;
                case 'B':
                    setType(B.newAppl("BYTE"), null);
                    break;
                case 'C':
                    setType(B.newAppl("CHAR"), null);
                    break;
                case 'D':
                    setType(B.newAppl("DOUBLE"), null);
                    break;
                case 'F':
                    setType(B.newAppl("FLOAT"), null);
                    break;
                case 'I':
                    setType(B.newAppl("INT"), null);
                    break;
                case 'J':
                    setType(B.newAppl("LONG"), null);
                    break;
                case 'S':
                    setType(B.newAppl("SHORT"), null);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type " + descriptor);
            }
        }

        @Override public void visitTypeVariable(String name) {
            if(!typeVars.containsKey(name)) {
                throw new IllegalArgumentException("Unknown type variable " + name);
            }
            setType(typeVars.get(name), null);
        }

        @Override public SignatureVisitor visitArrayType() {
            final SigType outer = this;
            return new SigType(typeVars) {
                @Override public void setType(ITerm elementType, Scope elementTypeScope) {
                    Tuple2<ITerm, Scope> type = makeArrayType(elementType);
                    outer.setType(type._1(), type._2());
                }
            };
        }

        @Override public void visitClassType(String name) {
            this.typeScope = getOrInitClass(name);
            this.type = makeREF(this.typeScope);
        }

        @Override public void visitInnerClassType(String name) {
            throw new UnsupportedOperationException("Inner class unsupported: " + name);
        }

        @Override public SignatureVisitor visitTypeArgument(char wildcard) {
            return new SigType(typeVars) {
                @Override public void setType(ITerm type, Scope typeScope) {
                }
            };
        }

        @Override public void visitTypeArgument() {
        }

        @Override public void visitEnd() {
            setType(type, typeScope);
        }

    }

    private abstract class ReturnType extends SigType {

        private ReturnType(Map<String, ITerm> typeVars) {
            super(typeVars);
        }

        public abstract void setReturnType(ITerm type, Scope typeScope);

        @Override public void setType(ITerm type, Scope typeScope) {
            setReturnType(B.newAppl("TYPED", type), typeScope);
        }

        @Override public void visitBaseType(char descriptor) {
            switch(descriptor) {
                case 'V':
                    setReturnType(B.newAppl("VOID"), null);
                    break;
                default:
                    super.visitBaseType(descriptor);
            }
        }

        @Override public void visitTypeVariable(String name) {
            super.visitTypeVariable(name);
        }

        @Override public SignatureVisitor visitArrayType() {
            return super.visitArrayType();
        }

        @Override public void visitClassType(String name) {
            super.visitClassType(name);
        }

        @Override public void visitInnerClassType(String name) {
            super.visitInnerClassType(name);
        }

        @Override public void visitEnd() {
            super.visitEnd();
        }

    }

    private ITerm sigType(Map<String, ITerm> typeVars, String signature) {
        Ref<ITerm> type = new Ref<>();
        new SignatureReader(signature).acceptType(SignatureType(typeVars, (ty, s_ty) -> type.set(ty)));
        return type.get();
    }

    private SignatureVisitor SignatureType(Map<String, ITerm> typeVars, Action2<ITerm, Scope> setType) {
        return new SigType(typeVars) {
            @Override public void setType(ITerm type, Scope typeScope) {
                setType.apply(type, typeScope);
            }
        };
    }


    private ITerm descType(Type desc) {
        switch(desc.getSort()) {
            case Type.ARRAY:
                return makeArrayType(descType(desc.getElementType()))._1();
            case Type.BOOLEAN:
                return B.newAppl("BOOLEAN");
            case Type.BYTE:
                return B.newAppl("BYTE");
            case Type.CHAR:
                return B.newAppl("CHAR");
            case Type.DOUBLE:
                return B.newAppl("DOUBLE");
            case Type.FLOAT:
                return B.newAppl("FLOAT");
            case Type.INT:
                return B.newAppl("INT");
            case Type.LONG:
                return B.newAppl("LONG");
            case Type.OBJECT:
                return makeREF(getOrInitClass(desc.getInternalName()));
            case Type.SHORT:
                return B.newAppl("SHORT");
            default:
                throw new IllegalArgumentException("Unknown type " + desc);
        }
    }

    private Tuple2<ITerm, Scope> makeArrayType(ITerm elementType) {
        final Scope s_ty = newTypeScope();

        addDecl(s_ty, WITH_TYPE_REL, makeREF(s_ty));
        addDecl(s_ty, WITH_KIND_REL, ARRAY_KIND);

        scopeGraph.addEdge(s_ty, EXTENDS_EDGE, objectType()._2());
        scopeGraph.addEdge(s_ty, IMPLEMENTS_EDGE, iterableType()._2());

        addDecl(s_ty, ELEMENT_TYPE_REL, elementType);

        return Tuple2.of(makeREF(s_ty), s_ty);
    }

    private static ITerm makeId(String name) {
        return B.newAppl("Id", B.newString(name));
    }

    private static ITerm makeREF(Scope scope) {
        return B.newAppl("REF", scope);
    }

    private static ITerm makeLabel(String name) {
        return B.newAppl("Label", B.newString(name));
    }

    private Tuple2<ITerm, Scope> objectType() {
        final Scope scope = getOrInitClass("java/lang/Object");
        ITerm type = makeREF(scope);
        return Tuple2.of(type, scope);
    }

    private Tuple2<ITerm, Scope> iterableType() {
        final Scope scope = getOrInitClass("java/lang/Iterable");
        ITerm type = makeREF(scope);
        return Tuple2.of(type, scope);
    }

}
