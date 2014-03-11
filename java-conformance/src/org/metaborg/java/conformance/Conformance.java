package org.metaborg.java.conformance;

import static org.metaborg.java.conformance.util.TermTools.*;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.metaborg.runtime.task.ITaskEngine;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.base.Predicate;

@SuppressWarnings({ "rawtypes", "deprecation" })
public class Conformance {
	private final ASTNode jdtAST;
	@SuppressWarnings("unused")
	private final IIndex index;
	private final ITaskEngine taskEngine;
	private final IStrategoTerm spxAST;

	public Conformance(ASTNode jdtAST, IIndex index, ITaskEngine taskEngine, IStrategoTerm spxAST) {
		this.jdtAST = jdtAST;
		this.index = index;
		this.taskEngine = taskEngine;
		this.spxAST = spxAST;
	}



	public void testCompilationUnit() {
		final CompilationUnit jdtCompilationUnit = (CompilationUnit) jdtAST;
		final IStrategoTerm spxCompilationUnit = spxAST.getSubterm(0);

		// Compare import name resolution
		final List jdtImports = jdtCompilationUnit.imports();
		final IStrategoTerm spxImports = spxCompilationUnit.getSubterm(1);
		for(int i = 0; i < jdtImports.size(); ++i) {
			final ImportDeclaration jdtImport = (ImportDeclaration) jdtImports.get(i);
			final IStrategoTerm spxImport = getTypeImport(spxImports.getSubterm(i));
			if(!compareNulls(jdtImport, spxImport))
				compareReferenceType((ITypeBinding) jdtImport.resolveBinding(), getTypeNameBindings(spxImport));
		}

		// Compare types
		final List jdtTypes = jdtCompilationUnit.types();
		final IStrategoTerm spxTypes = getTypes(spxCompilationUnit.getSubterm(2));
		for(int i = 0; i < jdtTypes.size(); ++i) {
			final TypeDeclaration jdtType = (TypeDeclaration) jdtTypes.get(i);
			final IStrategoTerm spxType = spxTypes.getSubterm(i);
			testType(jdtType, spxType);
		}
	}

	public void testType(TypeDeclaration jdtType, IStrategoTerm spxType) {
		if(jdtType.isInterface()) {
			testInterface(jdtType, spxType);
		} else {
			testClass(jdtType, spxType);
		}
	}

	public void testClass(TypeDeclaration jdtType, IStrategoTerm spxType) {
		// Compare superclass name resolution
		final Name jdtSuperClass = jdtType.getSuperclass();
		final IStrategoTerm spxSuperClass = getSupertype(spxType);
		if(!compareNulls(jdtSuperClass, spxSuperClass))
			compareReferenceType((ITypeBinding) jdtSuperClass.resolveBinding(), getRefTypeBindings(spxSuperClass));

		// Compare implemented interface name resolution
		final List jdtSuperInterfaces = jdtType.superInterfaces();
		final IStrategoTerm spxSuperInterfaces = getClassSuperinterfaces(spxType);
		for(int i = 0; i < jdtSuperInterfaces.size(); ++i) {
			final Name jdtSuperInterface = (Name) jdtSuperInterfaces.get(i);
			final IStrategoTerm spxSuperInterface = getImplementsInterface(spxSuperInterfaces.getSubterm(i));
			compareReferenceType((ITypeBinding) jdtSuperInterface.resolveBinding(), getRefTypeBindings(spxSuperInterface));
		}

		// TODO: Check interface implementation error

		// Compare body declarations
		final List jdtBodyDecls = jdtType.bodyDeclarations();
		final IStrategoTerm spxBodyDecls = getBodyDeclarations(spxType);
		for(int i = 0; i < jdtBodyDecls.size(); ++i) {
			final BodyDeclaration jdtBodyDecl = (BodyDeclaration) jdtBodyDecls.get(i);
			final IStrategoTerm spxBodyDecl = spxBodyDecls.getSubterm(i);
			testBodyDeclaration(jdtBodyDecl, spxBodyDecl);
		}
	}

	public void testInterface(TypeDeclaration jdtType, IStrategoTerm spxType) {
		// Compare subinterface name resolution
		final List jdtSuperInterfaces = jdtType.superInterfaces();
		final IStrategoTerm spxSuperInterfaces = getInterfaceSuperinterfaces(spxType);
		for(int i = 0; i < jdtSuperInterfaces.size(); ++i) {
			final Name jdtSuperInterface = (Name) jdtSuperInterfaces.get(i);
			final IStrategoTerm spxSuperInterface = getExtendsInterface(spxSuperInterfaces.getSubterm(i));
			compareReferenceType((ITypeBinding) jdtSuperInterface.resolveBinding(), getRefTypeBindings(spxSuperInterface));
		}
	}

	public void testBodyDeclaration(BodyDeclaration jdtBodyDecl, IStrategoTerm spxBodyDecl) {
		if(jdtBodyDecl instanceof TypeDeclaration) {
			testType((TypeDeclaration) jdtBodyDecl, spxBodyDecl);
		} else if(jdtBodyDecl instanceof FieldDeclaration) {
			testField((FieldDeclaration) jdtBodyDecl, spxBodyDecl);
		} else if(jdtBodyDecl instanceof MethodDeclaration) {
			testMethodOrConstructor((MethodDeclaration) jdtBodyDecl, spxBodyDecl);
		}
	}

	public void testField(FieldDeclaration jdtField, IStrategoTerm spxField) {
		// Compare type
		final Type jdtType = jdtField.getType();
		final IStrategoTerm spxType = getFieldType(spxField);
		compareTypes((ITypeBinding) jdtType.resolveBinding(), spxType);

		// Check and compare the initializer expression

	}

	public void testMethodOrConstructor(MethodDeclaration jdtMethod, IStrategoTerm spxMethod) {
		if(jdtMethod.isConstructor())
			testConstructor(jdtMethod, spxMethod);
		else
			testMethod(jdtMethod, spxMethod);
	}

	public void testMethod(MethodDeclaration jdtMethod, IStrategoTerm spxMethod) {
		// Compare return type

		// Compare parameter types

		// Compare expressions
	}

	public void testConstructor(MethodDeclaration jdtConstructor, IStrategoTerm spxConstructor) {
		// Compare parameter types

		// Compare expressions
	}



	private IStrategoTerm getTypeImport(IStrategoTerm term) {
		if(isAppl(term, "TypeImportDec")) {
			return term.getSubterm(0);
		}
		return null;
	}

	private IStrategoTerm getTypes(IStrategoTerm term) {
		return collectOne(term, new Predicate<IStrategoTerm>() {
			@Override
			public boolean apply(IStrategoTerm input) {
				return isAppl(input, "PackageDec", 3);
			}
		}).getSubterm(2);
	}

	private IStrategoTerm getSupertype(IStrategoTerm term) {
		final IStrategoTerm superClass = term.getSubterm(0).getSubterm(3);
		if(isAppl(superClass, "SuperDec"))
			return superClass.getSubterm(0);
		else
			return null;
	}

	private IStrategoTerm getClassSuperinterfaces(IStrategoTerm term) {
		final IStrategoTerm superInterfaces = term.getSubterm(0).getSubterm(4);
		if(isList(superInterfaces))
			return superInterfaces;
		else
			return null;
	}

	private IStrategoTerm getImplementsInterface(IStrategoTerm term) {
		return term.getSubterm(0);
	}

	private IStrategoTerm getInterfaceSuperinterfaces(IStrategoTerm term) {
		final IStrategoTerm superInterfaces = term.getSubterm(0).getSubterm(3);
		if(isList(superInterfaces))
			return superInterfaces;
		else
			return null;
	}

	private IStrategoTerm getExtendsInterface(IStrategoTerm term) {
		return term.getSubterm(0);
	}

	private IStrategoTerm getBodyDeclarations(IStrategoTerm term) {
		return term.getSubterm(1).getSubterm(0);
	}

	private IStrategoTerm getFieldType(IStrategoTerm term) {
		return term.getSubterm(1);
	}



	private boolean isPrimitiveType(IStrategoTerm term) {
		// @formatter:off
		return isAppl(term, "Boolean", 0)
			|| isAppl(term, "Byte", 0)
			|| isAppl(term, "Short", 0)
			|| isAppl(term, "Char", 0)
			|| isAppl(term, "Int", 0)
			|| isAppl(term, "Long", 0)
			|| isAppl(term, "Float", 0)
			|| isAppl(term, "Double", 0)
			|| isAppl(term, "Void", 0);
		// @formatter:on
	}

	private Iterable<IStrategoTerm> getRefTypeBindings(IStrategoTerm term) {
		final IStrategoTerm typeName = term.getSubterm(0);
		return getTypeNameBindings(typeName);
	}

	private Iterable<IStrategoTerm> getTypeNameBindings(IStrategoTerm typeName) {
		if(isAppl(typeName, "TypeName", 2)) {
			return resolveResults(collectUseTask(typeName.getSubterm(1)));
		} else if(isAppl(typeName, "TypeName", 1)) {
			return resolveResults(collectUseTask(typeName));
		}

		return null;
	}



	private boolean compareNulls(Object jdtObj, Object spxObj) {
		if(jdtObj == null && spxObj == null)
			return true;
		if(jdtObj == null ^ spxObj == null) {
			error("Incorrect nulls: " + jdtObj + " - " + spxObj);
			return true;
		}
		return false;
	}

	private boolean compareTypes(ITypeBinding jdtBinding, IStrategoTerm spxType) {
		if(jdtBinding.isPrimitive() ^ isPrimitiveType(spxType)) {
			error("Incorrect type kind: " + jdtBinding.getQualifiedName() + " - " + spxType);
			return false;
		}

		if(jdtBinding.isPrimitive()) {
			System.out.println(jdtBinding.getQualifiedName());
			System.out.println(spxType);
			boolean success = comparePrimitiveTypes(jdtBinding, spxType);
			if(!success) {
				error("Incorrect primitive types: " + jdtBinding.getQualifiedName() + " - " + spxType);
			}
			return success;
		} else {
			return compareReferenceType(jdtBinding, getRefTypeBindings(spxType));
		}
	}

	private boolean comparePrimitiveTypes(ITypeBinding jdtBinding, IStrategoTerm spxPrimType) {
		switch(jdtBinding.getName()) {
			case "boolean":
				return isAppl(spxPrimType, "Boolean", 0);
			case "byte":
				return isAppl(spxPrimType, "Byte", 0);
			case "short":
				return isAppl(spxPrimType, "Short", 0);
			case "char":
				return isAppl(spxPrimType, "Char", 0);
			case "int":
				return isAppl(spxPrimType, "Int", 0);
			case "long":
				return isAppl(spxPrimType, "Long", 0);
			case "float":
				return isAppl(spxPrimType, "Float", 0);
			case "double":
				return isAppl(spxPrimType, "Double", 0);
			case "void":
				return isAppl(spxPrimType, "Void", 0);
		}
		return false;
	}

	private boolean compareReferenceType(ITypeBinding jdtBinding, Iterable<IStrategoTerm> spxBindings) {
		boolean jdtFail = jdtBinding == null;
		boolean spxFail = spxBindings == null || !spxBindings.iterator().hasNext();
		if(jdtFail && spxFail)
			return true;
		if(jdtFail ^ spxFail) {
			error("Incorrect failure state: " + jdtFail + " - " + spxFail);
			return false;
		}

		final IStrategoTerm spxbinding = spxBindings.iterator().next().getSubterm(0);

		System.out.println(jdtBinding.getQualifiedName());
		System.out.println(spxbinding);

		if(!compareKind(jdtBinding.getKind(), uriNamespace(spxbinding))) {
			error("Incorrect kinds: " + jdtBinding.getKind() + " - " + uriNamespace(spxbinding));
		}

		if(!compareName(jdtBinding.getQualifiedName(), spxbinding)) {
			error("Incorrect names: " + jdtBinding.getQualifiedName() + " - " + spxbinding);
		}

		return true;
	}

	private boolean compareKind(int kind, IStrategoTerm namespace) {
		switch(kind) {
			case IBinding.PACKAGE:
				return isAppl(namespace, "NablNsPackage") || isAppl(namespace, "NablNsDefaultPackage");
			case IBinding.TYPE:
				return isAppl(namespace, "NablNsType");
			case IBinding.VARIABLE:
				return isAppl(namespace, "NablNsVariable") || isAppl(namespace, "NablNsField");
			case IBinding.METHOD:
				return isAppl(namespace, "NablNsMethod");
		}
		return false;
	}

	private boolean compareName(String jdtQName, IStrategoTerm spxQName) {
		final String[] jdtparts = jdtQName.split("\\.");
		final IStrategoTerm spxparts = uriSegments(spxQName);
		final int spxpartsLength = spxparts.getSubtermCount() - 1;
		if(jdtparts.length != spxpartsLength)
			return false;

		// Ignore default package from URI by ignoring the last element.
		for(int i = 0; i < spxpartsLength - 1; ++i) {
			final String jdtname = jdtparts[jdtparts.length - 1 - i];
			final String spxname = segmentName(spxparts.getSubterm(i));
			if(!jdtname.equals(spxname))
				return false;
		}

		return true;
	}



	private Iterable<IStrategoTerm> resolveResults(IStrategoTerm taskID) {
		return taskEngine.getTask(taskID).results();
	}



	private void error(String message) {
		System.err.println(message);
		throw new RuntimeException(message);
	}
}
