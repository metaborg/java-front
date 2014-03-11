package org.metaborg.java.conformance;

import static org.metaborg.java.conformance.JavaTermProjections.*;
import static org.metaborg.java.conformance.util.TermTools.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.*;
import org.metaborg.runtime.task.ITaskEngine;
import org.metaborg.runtime.task.Task;
import org.metaborg.runtime.task.util.SingletonIterable;
import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.base.Predicate;

@SuppressWarnings({ "rawtypes", "deprecation", "restriction" })
public class Conformance {
	private final CompilationUnit jdtAST;
	@SuppressWarnings("unused")
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
			log("Compare import types");
			if(!compareNulls(jdtImport, spxImport))
				compareReferenceType((ITypeBinding) jdtImport.resolveBinding(), resolveTypeNameBindings(spxImport));
		}

		// Compare types
		final List jdtTypes = jdtCompilationUnit.types();
		final IStrategoTerm spxTypes = getTypes(spxCompilationUnit.getSubterm(2));
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
		log("Compare superclass types");
		if(!compareNulls(jdtSuperClass, spxSuperClass))
			compareReferenceType((ITypeBinding) jdtSuperClass.resolveBinding(), resolveRefType(spxSuperClass));

		// Compare implemented interface name resolution
		final List jdtSuperInterfaces = jdtType.superInterfaces();
		final IStrategoTerm spxSuperInterfaces = getClassSuperinterfaces(spxType);
		compareArity(jdtSuperInterfaces, spxSuperInterfaces);
		for(int i = 0; i < jdtSuperInterfaces.size(); ++i) {
			final Name jdtSuperInterface = (Name) jdtSuperInterfaces.get(i);
			final IStrategoTerm spxSuperInterface = getImplementsInterface(spxSuperInterfaces.getSubterm(i));
			log("Compare superinterface types");
			compareReferenceType((ITypeBinding) jdtSuperInterface.resolveBinding(),
				resolveRefType(spxSuperInterface));
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
			compareReferenceType((ITypeBinding) jdtSuperInterface.resolveBinding(),
				resolveRefType(spxSuperInterface));
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
		compareTypes((ITypeBinding) jdtType.resolveBinding(), spxType);

		// Compare expression type
		// TODO: does not work in case of multiple assignments, since they are desugared into multiple fields.
		final VariableDeclarationFragment jdtFieldFragment = (VariableDeclarationFragment) jdtField.fragments().get(0);
		final Expression jdtFieldExpr = jdtFieldFragment.getInitializer();
		final IStrategoTerm spxFieldExpr = getFieldInit(spxField);
		if(!compareNulls(jdtFieldExpr, spxFieldExpr))
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
		compareTypes(jdtReturnType, spxReturnType);

		// Compare parameter types
		final List jdtParameters = jdtMethod.parameters();
		final IStrategoTerm spxParameters = getMethodParams(spxMethod);
		compareArity(jdtParameters, spxParameters);
		for(int i = 0; i < jdtParameters.size(); ++i) {
			final SingleVariableDeclaration jdtParam = (SingleVariableDeclaration) jdtParameters.get(i);
			final IStrategoTerm spxParam = spxParameters.getSubterm(i);
			log("Compare parameter types");
			compareTypes(jdtParam.getType().resolveBinding(), getParamType(spxParam));
		}

		// Compare statements
		final Block jdtBlock = jdtMethod.getBody();
		final IStrategoTerm spxStatements = getMethodBody(spxMethod);
		if(!compareNulls(jdtBlock, spxStatements)) {
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
		if(!compareNulls(jdtBlock, spxStatements)) {
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
			compareTypes(jdtParam.getType().resolveBinding(), getParamType(spxParam));
		}
	}

	public void testExpression(Expression jdtExpression, IStrategoTerm spxExpression) {
		// Compare expression type
		log("Compare expression types");
		compareTypes(jdtExpression.resolveTypeBinding(), resolveExpressionType(spxExpression));

		// Check sub expressions
		// If ArrayAccess, compare array and index expressions
		if(jdtExpression instanceof ArrayAccess) {
			final ArrayAccess jdtArrayAccess = (ArrayAccess) jdtExpression;
			
			testExpression(jdtArrayAccess.getArray(), getArrayAccessArrayExpr(spxExpression));
			testExpression(jdtArrayAccess.getIndex(), getArrayAccessIndexExpr(spxExpression));
			
			return;
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
			compareTypes(jdtCast.getType().resolveBinding(), getCastType(spxExpression));
			testExpression(jdtCast.getExpression(), getCastExpr(spxExpression));
			
			return;
		}
		// If ClassInstanceCreation, compare type and arguments
		if(jdtExpression instanceof ClassInstanceCreation) {
			final ClassInstanceCreation jdtConsInvoke = (ClassInstanceCreation) jdtExpression;
			
			log("compare constructor invocation types");
			compareTypes(jdtConsInvoke.getName().resolveTypeBinding(), getConsInvokeType(spxExpression));

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
			compareVarName(jdtFieldAccess.resolveFieldBinding(), resolveVarNameBindings(getFieldAccessName(spxExpression)));
			
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
			compareMethodName(jdtMethodInvoke.resolveMethodBinding(), resolveMethodNameBindings(getMethodInvokeName(spxExpression)));
			
			return;
		}
		// If SimpleName, check name resolution
		if(jdtExpression instanceof SimpleName) {
			final SimpleName jdtName = (SimpleName) jdtExpression;
			return;
		}
		// If QualifiedName, check name resolution
		if(jdtExpression instanceof QualifiedName) {
			final QualifiedName jdtName = (QualifiedName) jdtExpression;
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
		// If ConstructorInvocation, check constructor name resolution
		if(jdtStatement instanceof ConstructorInvocation) {
			final ConstructorInvocation jdtConsInvoke = (ConstructorInvocation) jdtStatement;
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
		// If ReturnStatement, check return error
		if(jdtStatement instanceof ReturnStatement) {
			final ReturnStatement jdtReturnStmt = (ReturnStatement) jdtStatement;
			return;
		}
		// If SuperConstructorInvocation, check constructor name resolution
		if(jdtStatement instanceof SuperConstructorInvocation) {
			final SuperConstructorInvocation jdtSuperConsInvoke = (SuperConstructorInvocation) jdtStatement;
			return;
		}
		// If VariableDeclarationStatement, check duplicate error
		if(jdtStatement instanceof VariableDeclarationStatement) {
			final VariableDeclarationStatement jdtVarDeclStmt = (VariableDeclarationStatement) jdtStatement;
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
		return resolveTypeNameBindings(typeName);
	}
	

	private Iterable<IStrategoTerm> resolveExpressionType(IStrategoTerm expr) {
		final IStrategoTerm type = getType(expr);
		return resolveResults(type);
	}

	
	
	
	private Iterable<IStrategoTerm> resolveTypeNameBindings(IStrategoTerm typeName) {
		if(isAppl(typeName, "TypeName", 2)) {
			return resolveResults(getUse(typeName.getSubterm(1)));
		} else if(isAppl(typeName, "TypeName", 1)) {
			final IStrategoTerm use = getUse(typeName.getSubterm(0));
			return resolveResults(getUse(typeName.getSubterm(0)));
		}

		return null;
	}

	private Iterable<IStrategoTerm> resolveVarNameBindings(IStrategoTerm varName) {
		// TODO: implement
		throw new NotImplementedException();
	}

	private Iterable<IStrategoTerm> resolveMethodNameBindings(IStrategoTerm methodName) {
		// TODO: implement
		throw new NotImplementedException();
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

	private boolean compareTypes(ITypeBinding jdtType, Iterable<IStrategoTerm> spxTypes) {
		return compareTypes(jdtType, firstOrNull(spxTypes));
	}

	private boolean compareTypes(ITypeBinding jdtType, IStrategoTerm spxType) {
		if(jdtType.isPrimitive() ^ isPrimitiveType(spxType)) {
			error("Incorrect type kind: " + jdtType.getQualifiedName() + " - " + spxType);
			return false;
		}

		if(jdtType.isPrimitive()) {
			log("  " + jdtType.getQualifiedName());
			log("  " + spxType);
			boolean success = comparePrimitiveTypes(jdtType, spxType);
			if(!success) {
				error("Incorrect primitive types: " + jdtType.getQualifiedName() + " - " + spxType);
			}
			return success;
		} else {
			return compareReferenceType(jdtType, resolveRefType(spxType));
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

	private boolean compareReferenceType(ITypeBinding jdtType, Iterable<IStrategoTerm> spxTypeDefs) {
		return compareReferenceType(jdtType, firstOrNull(spxTypeDefs));
	}

	private boolean compareReferenceType(ITypeBinding jdtType, IStrategoTerm spxTypeDef) {
		if(jdtType == null && spxTypeDef == null)
			return true;
		if(jdtType == null ^ spxTypeDef == null) {
			error("Incorrect failure state: " + jdtType + " - " + spxTypeDef);
			return false;
		}

		return compareReferenceTypeURI(jdtType, spxTypeDef.getSubterm(0));
	}
	
	private boolean compareReferenceTypeURI(ITypeBinding jdtType, IStrategoTerm spxTypeURI) {
		if(jdtType == null && spxTypeURI == null)
			return true;
		if(jdtType == null ^ spxTypeURI == null) {
			error("Incorrect failure state: " + jdtType + " - " + spxTypeURI);
			return false;
		}
		
		log("  " + jdtType.getQualifiedName());
		log("  " + spxTypeURI);

		if(!compareKind(jdtType.getKind(), uriNamespace(spxTypeURI))) {
			error("Incorrect kinds: " + jdtType.getKind() + " - " + uriNamespace(spxTypeURI));
		}

		if(!compareQualifiedName(jdtType.getQualifiedName(), spxTypeURI)) {
			error("Incorrect names: " + jdtType.getQualifiedName() + " - " + spxTypeURI);
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
				return isAppl(namespace, "NablNsVariable");
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
	
	private boolean compareVarName(IVariableBinding jdtVarName, Iterable<IStrategoTerm> spxVarNameDefs) {
		return compareVarName(jdtVarName, firstOrNull(spxVarNameDefs));
	}
	
	private boolean compareVarName(IVariableBinding jdtVarName, IStrategoTerm spxVarNameDef) {
		if(jdtVarName == null && spxVarNameDef == null)
			return true;
		if(jdtVarName == null ^ spxVarNameDef == null) {
			error("Incorrect failure state: " + jdtVarName + " - " + spxVarNameDef);
			return false;
		}
		
		final IStrategoTerm spxVarNameURI = spxVarNameDef.getSubterm(0);
		
		log("  " + jdtVarName.getName());
		log("  " + spxVarNameURI);

		if(!compareKind(jdtVarName.getKind(), uriNamespace(spxVarNameURI))) {
			error("Incorrect kinds: " + jdtVarName.getKind() + " - " + uriNamespace(spxVarNameURI));
		}
		
		if(!jdtVarName.getName().equals(uriName(spxVarNameURI))){
			error("Incorrect names: " + jdtVarName.getName() + " - " + uriName(spxVarNameURI));
		}
		
		if(jdtVarName.isField()) {
			final ITypeBinding jdtClass = jdtVarName.getDeclaringClass();
			final IStrategoTerm spxClassURI = uriParentUntilNs(spxVarNameURI, appl("NablNsClass"));
			return compareReferenceTypeURI(jdtClass, spxClassURI);
		} else {
			final IMethodBinding jdtMethod = jdtVarName.getDeclaringMethod();
			final IStrategoTerm spxMethodURI = uriParentUntilNs(spxVarNameURI, appl("NablNsMethod"));
			return compareMethodNameURI(jdtMethod, spxMethodURI);
		}
	}
	
	private boolean compareMethodName(IMethodBinding jdtMethodName, Iterable<IStrategoTerm> spxMethodNameDefs) {
		return compareMethodName(jdtMethodName, firstOrNull(spxMethodNameDefs));
	}
	
	private boolean compareMethodName(IMethodBinding jdtMethodName, IStrategoTerm spxMethodNameDef) {
		if(jdtMethodName == null && spxMethodNameDef == null)
			return true;
		if(jdtMethodName == null ^ spxMethodNameDef == null) {
			error("Incorrect failure state: " + jdtMethodName + " - " + spxMethodNameDef);
			return false;
		}
		
		// TODO: implement
		throw new NotImplementedException();
	}
	
	private boolean compareMethodNameURI(IMethodBinding jdtMethodName, IStrategoTerm spxMethodNameURI) {
		if(jdtMethodName == null && spxMethodNameURI == null)
			return true;
		if(jdtMethodName == null ^ spxMethodNameURI == null) {
			error("Incorrect failure state: " + jdtMethodName + " - " + spxMethodNameURI);
			return false;
		}
		
		// TODO: implement
		throw new NotImplementedException();
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
