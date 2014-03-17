package org.metaborg.java.conformance;

import static org.metaborg.java.conformance.JavaTermProjections.*;
import static org.metaborg.java.conformance.util.TermTools.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.metaborg.runtime.task.ITaskEngine;
import org.metaborg.runtime.task.Task;
import org.metaborg.runtime.task.util.SingletonIterable;
import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@SuppressWarnings({ "rawtypes", "deprecation" })
public class Conformance {
	private final CompilationUnit jdtAST;
	private final IIndex index;
	private final ITaskEngine taskEngine;
	private final IStrategoTerm spxAST;

	public Conformance(CompilationUnit jdtAST, IIndex index, ITaskEngine taskEngine, IStrategoTerm spxAST) {
		this.jdtAST = jdtAST;
		this.index = index;
		this.taskEngine = taskEngine;
		this.spxAST = spxAST;
	}



	public void testCompilationUnit() {
		final CompilationUnit jdtCompilationUnit = jdtAST;
		final IStrategoTerm spxCompilationUnit = spxAST.getSubterm(0);

		// Compare import name resolution
		final List jdtImports = jdtCompilationUnit.imports();
		final IStrategoTerm spxImports = spxCompilationUnit.getSubterm(1);
		compareArity(jdtImports, spxImports);
		for(int i = 0; i < jdtImports.size(); ++i) {
			final ImportDeclaration jdtImport = (ImportDeclaration) jdtImports.get(i);
			final IStrategoTerm spxImport = getTypeImport(spxImports.getSubterm(i));
			if(!containsNulls(jdtImport, spxImport)) {
				log("Compare import types");
				compareRefTypeBindings((ITypeBinding) jdtImport.resolveBinding(), resolveNameBindings(spxImport));
			}
		}

		// Compare types
		final List jdtTypes = jdtCompilationUnit.types();
		final IStrategoTerm spxTypes = getTypesInPackage(spxCompilationUnit.getSubterm(2));
		compareArity(jdtTypes, spxTypes);
		for(int i = 0; i < jdtTypes.size(); ++i) {
			final TypeDeclaration jdtType = (TypeDeclaration) jdtTypes.get(i);
			final IStrategoTerm spxType = spxTypes.getSubterm(i);
			testType(jdtType, spxType);
		}

		// Compare (error) messages
		testMessages();
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
		if(!containsNulls(jdtSuperClass, spxSuperClass)) {
			log("Compare superclass types");
			compareRefTypeBindings((ITypeBinding) jdtSuperClass.resolveBinding(), resolveRefType(spxSuperClass));
		}

		// Compare implemented interface name resolution
		final List jdtSuperInterfaces = jdtType.superInterfaces();
		final IStrategoTerm spxSuperInterfaces = getClassSuperinterfaces(spxType);
		compareArity(jdtSuperInterfaces, spxSuperInterfaces);
		for(int i = 0; i < jdtSuperInterfaces.size(); ++i) {
			final Name jdtSuperInterface = (Name) jdtSuperInterfaces.get(i);
			final IStrategoTerm spxSuperInterface = getImplementsInterface(spxSuperInterfaces.getSubterm(i));
			log("Compare superinterface types");
			compareRefTypeBindings((ITypeBinding) jdtSuperInterface.resolveBinding(), resolveRefType(spxSuperInterface));
		}

		// Compare body declarations
		final List jdtBodyDecls = jdtType.bodyDeclarations();
		final IStrategoTerm spxBodyDecls = getBodyDeclarations(spxType);
		compareArity(jdtBodyDecls, spxBodyDecls);
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
		compareArity(jdtSuperInterfaces, spxSuperInterfaces);
		for(int i = 0; i < jdtSuperInterfaces.size(); ++i) {
			final Name jdtSuperInterface = (Name) jdtSuperInterfaces.get(i);
			final IStrategoTerm spxSuperInterface = getExtendsInterface(spxSuperInterfaces.getSubterm(i));
			log("Compare superinterface types");
			compareRefTypeBindings((ITypeBinding) jdtSuperInterface.resolveBinding(), resolveRefType(spxSuperInterface));
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
		// Compare declared type
		final Type jdtType = jdtField.getType();
		final IStrategoTerm spxType = getFieldType(spxField);
		log("Compare field types");
		compareTypeBindings((ITypeBinding) jdtType.resolveBinding(), spxType);

		// Compare expression type
		// TODO: does not work in case of multiple assignments, since they are desugared into multiple fields.
		final VariableDeclarationFragment jdtFieldFragment = (VariableDeclarationFragment) jdtField.fragments().get(0);
		final Expression jdtFieldExpr = jdtFieldFragment.getInitializer();
		final IStrategoTerm spxFieldExpr = getFieldInit(spxField);
		if(!containsNulls(jdtFieldExpr, spxFieldExpr))
			testExpression(jdtFieldExpr, spxFieldExpr);
	}

	public void testMethodOrConstructor(MethodDeclaration jdtMethod, IStrategoTerm spxMethod) {
		if(jdtMethod.isConstructor())
			testConstructor(jdtMethod, spxMethod);
		else
			testMethod(jdtMethod, spxMethod);
	}

	public void testMethod(MethodDeclaration jdtMethod, IStrategoTerm spxMethod) {
		// Compare return type
		final ITypeBinding jdtReturnType = jdtMethod.getReturnType().resolveBinding();
		final IStrategoTerm spxReturnType = getMethodReturnType(spxMethod);
		log("Compare method return types");
		compareTypeBindings(jdtReturnType, spxReturnType);

		// Compare parameter types
		final List jdtParameters = jdtMethod.parameters();
		final IStrategoTerm spxParameters = getMethodParams(spxMethod);
		compareArity(jdtParameters, spxParameters);
		for(int i = 0; i < jdtParameters.size(); ++i) {
			final SingleVariableDeclaration jdtParam = (SingleVariableDeclaration) jdtParameters.get(i);
			final IStrategoTerm spxParam = spxParameters.getSubterm(i);
			log("Compare parameter types");
			compareTypeBindings(jdtParam.getType().resolveBinding(), getParamType(spxParam));
		}

		// Compare statements
		final Block jdtBlock = jdtMethod.getBody();
		final IStrategoTerm spxStatements = getMethodBody(spxMethod);
		if(!containsNulls(jdtBlock, spxStatements)) {
			final List jdtStatements = jdtBlock.statements();
			testStatements(jdtStatements, spxStatements);
		}
	}

	public void testConstructor(MethodDeclaration jdtConstructor, IStrategoTerm spxConstructor) {
		// Compare parameter types
		final List jdtParams = jdtConstructor.parameters();
		final IStrategoTerm spxParams = getConstructorParams(spxConstructor);
		testParameters(jdtParams, spxParams);

		// Compare statements
		final Block jdtBlock = jdtConstructor.getBody();
		final IStrategoTerm spxStatements = getConstructorBody(spxConstructor);
		if(!containsNulls(jdtBlock, spxStatements)) {
			final List jdtStatements = jdtBlock.statements();
			testStatements(jdtStatements, spxStatements);
		}
	}

	public void testParameters(List jdtParams, IStrategoTerm spxParams) {
		compareArity(jdtParams, spxParams);
		for(int i = 0; i < jdtParams.size(); ++i) {
			final SingleVariableDeclaration jdtParam = (SingleVariableDeclaration) jdtParams.get(i);
			final IStrategoTerm spxParam = spxParams.getSubterm(i);
			log("Compare parameter types");
			compareTypeBindings(jdtParam.getType().resolveBinding(), getParamType(spxParam));
		}
	}

	public void testExpression(Expression jdtExpression, IStrategoTerm spxExpression) {
		// Compare expression type
		log("Compare expression types");
		compareTypeBindings(jdtExpression.resolveTypeBinding(), resolveExpressionType(spxExpression));

		// Check sub expressions
		// If ArrayAccess, compare array and index expressions
		if(jdtExpression instanceof ArrayAccess) {
			final ArrayAccess jdtArrayAccess = (ArrayAccess) jdtExpression;

			testExpression(jdtArrayAccess.getArray(), getArrayAccessArrayExpr(spxExpression));
			testExpression(jdtArrayAccess.getIndex(), getArrayAccessIndexExpr(spxExpression));

			return;
		}
		// If ArrayInitializer, compare initializer expressions
		if(jdtExpression instanceof ArrayInitializer) {
			final ArrayInitializer jdtArrayInit = (ArrayInitializer) jdtExpression;

			// TODO: implement
			throw new NotImplementedException();
		}
		// If Assignment, compare left and right expressions
		if(jdtExpression instanceof Assignment) {
			final Assignment jdtAssignment = (Assignment) jdtExpression;

			testExpression(jdtAssignment.getLeftHandSide(), getAssignLeftExpr(spxExpression));
			testExpression(jdtAssignment.getRightHandSide(), getAssignRightExpr(spxExpression));

			return;
		}
		// If CastExpression, compare type and expression
		if(jdtExpression instanceof CastExpression) {
			final CastExpression jdtCast = (CastExpression) jdtExpression;

			log("compare cast types");
			compareTypeBindings(jdtCast.getType().resolveBinding(), getCastType(spxExpression));
			testExpression(jdtCast.getExpression(), getCastExpr(spxExpression));

			return;
		}
		// If ClassInstanceCreation, compare type and arguments
		if(jdtExpression instanceof ClassInstanceCreation) {
			final ClassInstanceCreation jdtConsInvoke = (ClassInstanceCreation) jdtExpression;

			log("compare constructor invocation types");
			compareTypeBindings(jdtConsInvoke.getName().resolveTypeBinding(), getConsInvokeType(spxExpression));

			final List jdtArguments = jdtConsInvoke.arguments();
			final IStrategoTerm spxArguments = getConsInvokeArgs(spxExpression);
			compareArity(jdtArguments, spxArguments);
			for(int i = 0; i < jdtArguments.size(); ++i) {
				testExpression((Expression) jdtArguments.get(i), spxArguments.getSubterm(i));
			}

			// TODO: constructor invocation can be qualified.
			// TODO: compare class body declaration in case of an anonymous class.

			return;
		}
		// If FieldAccess, compare expression and field name resolution
		if(jdtExpression instanceof FieldAccess) {
			final FieldAccess jdtFieldAccess = (FieldAccess) jdtExpression;

			testExpression(jdtFieldAccess.getExpression(), getFieldAccessExpr(spxExpression));

			log("compare field access variable names");
			compareVarName(jdtFieldAccess.resolveFieldBinding(), resolveNameBindings(getFieldAccessName(spxExpression)));

			return;
		}
		// If MethodInvocation, compare arguments and method name resolution
		if(jdtExpression instanceof MethodInvocation) {
			final MethodInvocation jdtMethodInvoke = (MethodInvocation) jdtExpression;

			final List jdtArguments = jdtMethodInvoke.arguments();
			final IStrategoTerm spxArguments = getMethodInvokeArgs(spxExpression);
			compareArity(jdtArguments, spxArguments);
			for(int i = 0; i < jdtArguments.size(); ++i) {
				testExpression((Expression) jdtArguments.get(i), spxArguments.getSubterm(i));
			}

			log("compare method invocation names");
			compareMethodName(jdtMethodInvoke.resolveMethodBinding(),
				resolveNameBindings(getMethodInvokeName(spxExpression)));

			return;
		}
		// If SimpleName, check name resolution
		if(jdtExpression instanceof Name) {
			final Name jdtName = (Name) jdtExpression;

			log("compare names");
			compareBindings(jdtName.resolveBinding(), resolveNameBindings(spxExpression));

			return;
		}
	}

	public void testStatements(List jdtStatements, IStrategoTerm spxStatements) {
		compareArity(jdtStatements, spxStatements);
		for(int i = 0; i < jdtStatements.size(); ++i) {
			final Statement jdtStatement = (Statement) jdtStatements.get(i);
			final IStrategoTerm spxStatement = spxStatements.getSubterm(i);
			testStatement(jdtStatement, spxStatement);
		}
	}

	public void testStatement(Statement jdtStatement, IStrategoTerm spxStatement) {
		// Statement specific checks
		// If Block, iterate over statements
		if(jdtStatement instanceof Block) {
			final Block jdtBlock = (Block) jdtStatement;
			final List jdtStatements = jdtBlock.statements();
			final IStrategoTerm spxStatements = getBlockStatements(spxStatement);
			testStatements(jdtStatements, spxStatements);
			return;
		}
		// If ExpressionStatement, check expression
		if(jdtStatement instanceof ExpressionStatement) {
			final ExpressionStatement jdtExprStmt = (ExpressionStatement) jdtStatement;
			final Expression jdtExpression = jdtExprStmt.getExpression();
			final IStrategoTerm spxExpression = spxStatement;
			testExpression(jdtExpression, spxExpression);
			return;
		}
	}

	public void testMessages() {
		final IProblem[] jdtProblems = jdtAST.getProblems();
		final Collection<IStrategoTerm> spxMessages = new LinkedList<IStrategoTerm>();
		for(Task task : taskEngine.getTasks()) {
			if(task.message() != null)
				spxMessages.add(task.message());
		}

		// TODO: check type errors
		// TODO: assignment errors
		// TODO: array errors
		// TODO: cast errors
		// TODO: class interface implementation errors

		// TODO: check name errors
		// TODO: duplicate definition errors
	}



	private IStrategoTerm getTypeImport(IStrategoTerm term) {
		if(isAppl(term, "TypeImportDec")) {
			return term.getSubterm(0);
		}
		return null;
	}

	private IStrategoTerm getTypesInPackage(IStrategoTerm term) {
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

	private Iterable<IStrategoTerm> resolveRefType(IStrategoTerm refType) {
		final IStrategoTerm typeName = refType.getSubterm(0);
		return resolveNameBindings(typeName);
	}


	private Iterable<IStrategoTerm> resolveExpressionType(IStrategoTerm expr) {
		final IStrategoTerm type = getType(expr);
		return resolveResults(type);
	}



	private Iterable<IStrategoTerm> resolveNameBindings(IStrategoTerm name) {
		if(isAppl(name, "DefaultPackageName", 1)) {
			return resolveResults(getUse(name.getSubterm(0)));
		}
		if(isAppl(name, "PackageName", 1)) {
			return resolveResults(getUse(name.getSubterm(0)));
		}
		if(isAppl(name, "PackageName", 2)) {
			return resolveResults(getUse(name.getSubterm(1)));
		}

		if(isAppl(name, "PackageOrTypeName", 1)) {
			return resolveResults(getUse(name.getSubterm(0)));
		}
		if(isAppl(name, "PackageOrTypeName", 2)) {
			return resolveResults(getUse(name.getSubterm(1)));
		}

		if(isAppl(name, "ExprName", 1)) {
			return resolveResults(getUse(name.getSubterm(0)));
		}
		if(isAppl(name, "ExprName", 2)) {
			return resolveResults(getUse(name.getSubterm(1)));
		}

		if(isAppl(name, "AmbName", 1)) {
			return resolveResults(getUse(name.getSubterm(0)));
		}
		if(isAppl(name, "AmbName", 2)) {
			return resolveResults(getUse(name.getSubterm(1)));
		}

		if(isAppl(name, "MethodName", 1)) {
			return resolveResults(getUse(name.getSubterm(0)));
		}
		if(isAppl(name, "MethodName", 2)) {
			return resolveResults(getUse(name.getSubterm(1)));
		}

		if(isAppl(name, "TypeName", 1)) {
			return resolveResults(getUse(name.getSubterm(0)));
		}
		if(isAppl(name, "TypeName", 2)) {
			return resolveResults(getUse(name.getSubterm(1)));
		}

		return null;
	}



	private boolean compareArity(List jdtList, IStrategoTerm spxTerm) {
		boolean jdtEmpty = jdtList == null || jdtList.size() == 0;
		boolean spxEmpty = spxTerm == null || spxTerm.getSubtermCount() == 0;

		if(jdtEmpty ^ spxEmpty) {
			error("Incorrect emptyness: " + jdtEmpty + " - " + spxEmpty);
			return false;
		}

		if(jdtEmpty && spxEmpty)
			return true;

		if(jdtList.size() != spxTerm.getSubtermCount()) {
			error("Incorrect arity: " + jdtList.size() + " - " + spxTerm.getSubtermCount());
			return false;
		}
		return true;
	}

	private boolean compareBindingFailure(ITypeBinding jdtBinding, IStrategoTerm spxBinding) {
		if(jdtBinding == null && spxBinding == null) {
			log("  null");
			log("  null");
			return true;
		}
		if(jdtBinding == null ^ spxBinding == null) {
			error("Incorrect failure state: " + jdtBinding.getQualifiedName() + " - " + spxBinding);
			return false;
		}
		return true;
	}

	private boolean compareBindingFailure(IBinding jdtBinding, IStrategoTerm spxBinding) {
		if(jdtBinding == null && spxBinding == null) {
			log("  null");
			log("  null");
			return true;
		}
		if(jdtBinding == null ^ spxBinding == null) {
			error("Incorrect failure state: " + jdtBinding.getName() + " - " + spxBinding);
			return false;
		}
		return true;
	}



	private boolean compareBindings(IBinding jdtName, Iterable<IStrategoTerm> spxNames) {
		return compareBindings(jdtName, firstOrNull(spxNames));
	}

	private boolean compareBindings(IBinding jdtName, IStrategoTerm spxName) {
		if(!compareBindingFailure(jdtName, spxName))
			return false;
		if(containsNulls(jdtName, spxName))
			return true;

		switch (jdtName.getKind()) {
			case IBinding.PACKAGE:
				// TODO: implement
				throw new NotImplementedException();
			case IBinding.TYPE:
				return compareTypeBindings((ITypeBinding) jdtName, spxName);
			case IBinding.VARIABLE:
				return compareVarName((IVariableBinding) jdtName, spxName);
			case IBinding.METHOD:
				return compareMethodName((IMethodBinding) jdtName, spxName);
		}

		throw new RuntimeException("Unhandled JDT binding kind");
	}


	private boolean compareTypeBindings(ITypeBinding jdtType, Iterable<IStrategoTerm> spxTypes) {
		return compareTypeBindings(jdtType, firstOrNull(spxTypes));
	}

	private boolean compareTypeBindings(ITypeBinding jdtType, IStrategoTerm spxType) {
		if(!compareBindingFailure(jdtType, spxType))
			return false;
		if(containsNulls(jdtType, spxType))
			return true;

		if(jdtType.isPrimitive() ^ isPrimitiveType(spxType)) {
			error("Incorrect type kind: " + jdtType.getQualifiedName() + " - " + spxType);
			return false;
		}

		if(jdtType.isPrimitive()) {
			log("  " + jdtType.getQualifiedName());
			log("  " + spxType);
			boolean success = comparePrimTypeBindings(jdtType, spxType);
			if(!success) {
				error("Incorrect primitive types: " + jdtType.getQualifiedName() + " - " + spxType);
			}
			return success;
		} else if(jdtType.isArray()) {
			boolean success = compareArrayTypeBindings(jdtType, spxType);
			if(!success) {
				error("Incorrect array types: " + jdtType.getQualifiedName() + " - " + spxType);
			}
			return success;
		} else {
			return compareRefTypeBindings(jdtType, resolveRefType(spxType));
		}
	}

	private boolean comparePrimTypeBindings(ITypeBinding jdtBinding, IStrategoTerm spxPrimType) {
		switch (jdtBinding.getName()) {
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

	private boolean compareArrayTypeBindings(ITypeBinding jdtArrayType, IStrategoTerm spxArrayType) {
		final int jdtDim = jdtArrayType.getDimensions();
		final int spxDim = getArrayTypeDimension(spxArrayType);
		if(jdtDim != spxDim) {
			error("Incorrect array dimensions: " + jdtDim + " - " + spxDim);
			return false;
		}

		final ITypeBinding jdtInnerType = jdtArrayType.getElementType();
		final IStrategoTerm spxInnerType = getArrayTypeInnerType(spxArrayType);

		if(!compareTypeBindings(jdtInnerType, spxInnerType)) {
			error("Incorrect array element types: " + jdtInnerType.getQualifiedName() + " - " + spxInnerType);
			return false;
		}

		return true;
	}

	private boolean compareRefTypeBindings(ITypeBinding jdtType, Iterable<IStrategoTerm> spxTypeDefs) {
		return compareRefTypeBindings(jdtType, firstOrNull(spxTypeDefs));
	}

	private boolean compareRefTypeBindings(ITypeBinding jdtType, IStrategoTerm spxTypeDef) {
		if(!compareBindingFailure(jdtType, spxTypeDef))
			return false;

		return compareRefTypeBindingsURI(jdtType, spxTypeDef.getSubterm(0));
	}

	private boolean compareRefTypeBindingsURI(ITypeBinding jdtType, IStrategoTerm spxTypeURI) {
		if(!compareBindingFailure(jdtType, spxTypeURI))
			return false;
		if(containsNulls(jdtType, spxTypeURI))
			return true;

		log("  " + jdtType.getQualifiedName());
		log("  " + spxTypeURI);

		if(!compareKind(jdtType.getKind(), uriNamespace(spxTypeURI))) {
			error("Incorrect kinds: " + jdtType.getKind() + " - " + uriNamespace(spxTypeURI));
			return false;
		}

		if(!compareQualifiedName(jdtType.getQualifiedName(), spxTypeURI)) {
			error("Incorrect names: " + jdtType.getQualifiedName() + " - " + spxTypeURI);
			return false;
		}

		return true;
	}


	private boolean compareVarName(IVariableBinding jdtVarName, Iterable<IStrategoTerm> spxVarNameDefs) {
		return compareVarName(jdtVarName, firstOrNull(spxVarNameDefs));
	}

	private boolean compareVarName(IVariableBinding jdtVarName, IStrategoTerm spxVarNameDef) {
		if(!compareBindingFailure(jdtVarName, spxVarNameDef))
			return false;
		if(containsNulls(jdtVarName, spxVarNameDef))
			return true;


		return compareVarNameURI(jdtVarName, spxVarNameDef.getSubterm(0));
	}

	private boolean compareVarNameURI(IVariableBinding jdtVarName, IStrategoTerm spxVarNameURI) {
		if(!compareBindingFailure(jdtVarName, spxVarNameURI))
			return false;
		if(containsNulls(jdtVarName, spxVarNameURI))
			return true;

		log("  " + jdtVarName.getName());
		log("  " + spxVarNameURI);

		if(!compareKind(jdtVarName.getKind(), uriNamespace(spxVarNameURI))) {
			error("Incorrect kinds: " + jdtVarName.getKind() + " - " + uriNamespace(spxVarNameURI));
			return false;
		}

		if(!jdtVarName.getName().equals(uriName(spxVarNameURI))) {
			error("Incorrect names: " + jdtVarName.getName() + " - " + uriName(spxVarNameURI));
			return false;
		}

		if(jdtVarName.isField()) {
			final ITypeBinding jdtType = jdtVarName.getDeclaringClass();
			final IStrategoTerm spxTypeURI = uriParentUntilNs(spxVarNameURI, appl("NablNsType"));
			log("compare declaring type of field");
			return compareRefTypeBindingsURI(jdtType, spxTypeURI);
		} else {
			final int jdtVarID = jdtVarName.getVariableId();
			final int spxVarID = getIndexVarID(spxVarNameURI);
			if(jdtVarID != spxVarID) {
				error("Incorrect variable IDs: " + jdtVarID + " - " + spxVarID);
				return false;
			}

			final IMethodBinding jdtMethod = jdtVarName.getDeclaringMethod();
			final IStrategoTerm spxMethodURI = uriParentUntilNs(spxVarNameURI, appl("NablNsMethod"));
			log("compare declaring method of variable");
			return compareMethodNameURI(jdtMethod, spxMethodURI);
		}
	}

	private boolean compareMethodName(IMethodBinding jdtMethodName, Iterable<IStrategoTerm> spxMethodNameDefs) {
		return compareMethodName(jdtMethodName, firstOrNull(spxMethodNameDefs));
	}

	private boolean compareMethodName(IMethodBinding jdtMethodName, IStrategoTerm spxMethodNameDef) {
		if(!compareBindingFailure(jdtMethodName, spxMethodNameDef))
			return false;
		if(containsNulls(jdtMethodName, spxMethodNameDef))
			return true;

		return compareMethodNameURI(jdtMethodName, spxMethodNameDef.getSubterm(0));
	}

	private boolean compareMethodNameURI(IMethodBinding jdtMethodName, IStrategoTerm spxMethodNameURI) {
		if(!compareBindingFailure(jdtMethodName, spxMethodNameURI))
			return false;
		if(containsNulls(jdtMethodName, spxMethodNameURI))
			return true;

		log("  " + jdtMethodName.getName());
		log("  " + spxMethodNameURI);

		if(!compareKind(jdtMethodName.getKind(), uriNamespace(spxMethodNameURI))) {
			error("Incorrect kinds: " + jdtMethodName.getKind() + " - " + uriNamespace(spxMethodNameURI));
			return false;
		}
		if(!jdtMethodName.getName().equals(uriName(spxMethodNameURI))) {
			error("Incorrect names: " + jdtMethodName.getName() + " - " + uriName(spxMethodNameURI));
			return false;
		}

		final ITypeBinding jdtType = jdtMethodName.getDeclaringClass();
		final IStrategoTerm spxTypeURI = uriParentUntilNs(spxMethodNameURI, appl("NablNsType"));
		log("compare declaring type of method");
		return compareRefTypeBindingsURI(jdtType, spxTypeURI);
	}


	private boolean compareKind(int kind, IStrategoTerm namespace) {
		switch (kind) {
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

	private boolean compareQualifiedName(String jdtQName, IStrategoTerm spxURI) {
		final String[] jdtparts = jdtQName.split("\\.");
		final IStrategoTerm spxparts = uriSegments(spxURI);
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



	private Iterable<IStrategoTerm> resolveResults(IStrategoTerm result) {
		if(result == null)
			return null;
		if(isResult(result))
			return taskEngine.getTask(result.getSubterm(0)).results();
		return new SingletonIterable<IStrategoTerm>(result);
	}

	private IStrategoTerm firstOrNull(Iterable<IStrategoTerm> iterable) {
		if(iterable == null || !iterable.iterator().hasNext())
			return null;
		return iterable.iterator().next();
	}

	private boolean containsNulls(Object jdtObj, Object spxObj) {
		if(jdtObj == null && spxObj == null)
			return true;
		if(jdtObj == null ^ spxObj == null) {
			error("Incorrect nulls: " + jdtObj + " - " + spxObj);
			return true;
		}
		return false;
	}



	private Iterable<IStrategoTerm> getIndexProperties(IStrategoTerm uri, IStrategoTerm kind) {
		final Iterable<IStrategoTerm> entries =
			IndexEntry.toTerms(factory, index.get(appl("Prop", uri, kind, tuple())));
		final Collection<IStrategoTerm> values = new LinkedList<IStrategoTerm>();
		for(IStrategoTerm entry : entries) {
			final IStrategoTerm value = entry.getSubterm(2);
			if(isResult(value)) {
				Iterables.addAll(values, resolveResults(value));
			} else {
				values.add(value);
			}
		}
		return values;
	}

	private IStrategoTerm getIndexProperty(IStrategoTerm uri, IStrategoTerm kind) {
		Iterable<IStrategoTerm> entries = getIndexProperties(uri, kind);
		if(!entries.iterator().hasNext())
			return null;
		return entries.iterator().next();
	}

	private IStrategoTerm getIndexType(IStrategoTerm uri) {
		return getIndexProperty(uri, appl("Type"));
	}

	private int getIndexVarID(IStrategoTerm uri) {
		IStrategoTerm varID = getIndexProperty(uri, appl("NablProp_var-id"));
		if(varID == null)
			return -1;
		else
			return ((IStrategoInt) varID).intValue() - 1; // Subtract one because Spoofax begins at 1, whereas JDT
															// begins at 0.
	}



	private void log(Object message) {
		System.err.flush();
		System.out.flush();
		System.out.println(message.toString());
	}

	private void error(Object message) {
		System.out.flush();
		System.err.flush();
		System.err.println(message.toString());
		// throw new RuntimeException(message.toString());
	}
}
