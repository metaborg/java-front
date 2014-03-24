package org.metaborg.java.conformance;

import static org.metaborg.java.conformance.JavaTermProjections.*;
import static org.metaborg.java.conformance.util.TermTools.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.metaborg.runtime.task.ITaskEngine;
import org.metaborg.runtime.task.util.SingletonIterable;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.Iterables;

@SuppressWarnings({ "rawtypes", "deprecation" })
public class Conformance {
	private final CompilationUnit jdtAST;
	private final IIndex index;
	private final ITaskEngine taskEngine;
	private final IStrategoTerm spxAST;
	private final ResultLogger logger;

	public Conformance(CompilationUnit jdtAST, IIndex index, ITaskEngine taskEngine, IStrategoTerm spxAST,
		ResultLogger logger) {
		this.jdtAST = jdtAST;
		this.index = index;
		this.taskEngine = taskEngine;
		this.spxAST = spxAST;
		this.logger = logger;
	}



	public void testCompilationUnit() {
		final CompilationUnit jdtCompilationUnit = jdtAST;
		final IStrategoTerm spxCompilationUnit = spxAST.getSubterm(0);

		// Compare import name resolution
		final List jdtImports = jdtCompilationUnit.imports();
		final IStrategoList spxImportsWithJavaLang = (IStrategoList) spxCompilationUnit.getSubterm(1);
		final IStrategoTerm spxImports = spxImportsWithJavaLang.tail(); // Remove implicit java.lang.* import.
		if(compareArity(jdtImports, spxImports, "File imports")) {
			for(int i = 0; i < jdtImports.size(); ++i) {
				final ImportDeclaration jdtImport = (ImportDeclaration) jdtImports.get(i);
				final IStrategoTerm spxImport = getTypeImport(spxImports.getSubterm(i));
				if(!containsNulls(jdtImport, spxImport)) {
					final ITypeBinding jdtTypeBinding = (ITypeBinding) jdtImport.resolveBinding();
					final Iterable<IStrategoTerm> spxTypeBinding = resolveNameBindings(spxImport);
					final boolean result = compareRefTypeBindings(jdtTypeBinding, spxTypeBinding);
					logger.result(result, "Import type", jdtTypeBinding, spxTypeBinding);
				}
			}
		}

		// Compare types
		final List jdtTypes = jdtCompilationUnit.types();
		final IStrategoTerm spxTypes = getTypesInPackage(spxCompilationUnit.getSubterm(2));
		if(compareArity(jdtTypes, spxTypes, "File types")) {
			for(int i = 0; i < jdtTypes.size(); ++i) {
				final TypeDeclaration jdtType = (TypeDeclaration) jdtTypes.get(i);
				final IStrategoTerm spxType = spxTypes.getSubterm(i);
				testType(jdtType, spxType);
			}
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

		if(!(jdtSuperClass == null && inheritsFromObject(spxSuperClass))) {
			compareNulls(jdtSuperClass, spxSuperClass, "Class superclass");
			if(!containsNulls(jdtSuperClass, spxSuperClass)) {
				final ITypeBinding jdtTypeBinding = (ITypeBinding) jdtSuperClass.resolveBinding();
				final Iterable<IStrategoTerm> spxTypeBinding = resolveRefType(spxSuperClass);
				final boolean result = compareRefTypeBindings(jdtTypeBinding, spxTypeBinding);
				logger.result(result, "Class extends type", jdtTypeBinding, spxTypeBinding);
			}
		}

		// Compare implemented interface name resolution
		final List jdtSuperInterfaces = jdtType.superInterfaces();
		final IStrategoTerm spxSuperInterfaces = getClassSuperinterfaces(spxType);
		if(compareArity(jdtSuperInterfaces, spxSuperInterfaces, "Class superinterfaces")) {
			for(int i = 0; i < jdtSuperInterfaces.size(); ++i) {
				final Name jdtSuperInterface = (Name) jdtSuperInterfaces.get(i);
				final IStrategoTerm spxSuperInterface = getImplementsInterface(spxSuperInterfaces.getSubterm(i));

				final ITypeBinding jdtTypeBinding = (ITypeBinding) jdtSuperInterface.resolveBinding();
				final Iterable<IStrategoTerm> spxTypeBinding = resolveRefType(spxSuperInterface);
				final boolean result = compareRefTypeBindings(jdtTypeBinding, spxTypeBinding);
				logger.result(result, "Class implements type", jdtTypeBinding, spxTypeBinding);
			}
		}

		// Compare body declarations
		final List jdtBodyDecls = jdtType.bodyDeclarations();
		final IStrategoTerm spxBodyDecls = getClassBodyDeclarations(spxType);
		testBodyDeclarations(jdtBodyDecls, spxBodyDecls);
	}

	public void testInterface(TypeDeclaration jdtType, IStrategoTerm spxType) {
		// Compare subinterface name resolution
		final List jdtSuperInterfaces = jdtType.superInterfaces();
		final IStrategoTerm spxSuperInterfaces = getInterfaceSuperinterfaces(spxType);
		if(compareArity(jdtSuperInterfaces, spxSuperInterfaces, "Interface superinterfaces")) {
			for(int i = 0; i < jdtSuperInterfaces.size(); ++i) {
				final Name jdtSuperInterface = (Name) jdtSuperInterfaces.get(i);
				final IStrategoTerm spxSuperInterface = getExtendsInterface(spxSuperInterfaces.getSubterm(i));

				final ITypeBinding jdtTypeBinding = (ITypeBinding) jdtSuperInterface.resolveBinding();
				final Iterable<IStrategoTerm> spxTypeBinding = resolveRefType(spxSuperInterface);
				final boolean result = compareRefTypeBindings(jdtTypeBinding, spxTypeBinding);
				logger.result(result, "Interface extends type", jdtTypeBinding, spxTypeBinding);
			}
		}

		// TODO: interface body declarations
	}

	public void testBodyDeclarations(List jdtBodyDecls, IStrategoTerm spxBodyDecls) {
		if(compareArity(jdtBodyDecls, spxBodyDecls, "Class body declarations")) {
			for(int i = 0; i < jdtBodyDecls.size(); ++i) {
				final BodyDeclaration jdtBodyDecl = (BodyDeclaration) jdtBodyDecls.get(i);
				final IStrategoTerm spxBodyDecl = spxBodyDecls.getSubterm(i);
				testBodyDeclaration(jdtBodyDecl, spxBodyDecl);
			}
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
		final ITypeBinding jdtType = (ITypeBinding) jdtField.getType().resolveBinding();
		final IStrategoTerm spxType = getFieldType(spxField);
		final boolean result = compareTypeBindings(jdtType, spxType);
		logger.result(result, "Field type", jdtType, spxType);

		// Compare expression type
		// TODO: does not work in case of multiple assignments, since they are desugared into multiple fields.
		final VariableDeclarationFragment jdtFieldFragment = (VariableDeclarationFragment) jdtField.fragments().get(0);
		final Expression jdtFieldExpr = jdtFieldFragment.getInitializer();
		final IStrategoTerm spxFieldExpr = getFieldInit(spxField);
		compareNulls(jdtFieldExpr, spxFieldExpr, "Field initializer");
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
		final boolean returnTypeResult = compareTypeBindings(jdtReturnType, spxReturnType);
		logger.result(returnTypeResult, "Method return type", jdtReturnType, spxReturnType);

		// Compare parameter types
		final List jdtParameters = jdtMethod.parameters();
		final IStrategoTerm spxParameters = getMethodParams(spxMethod);
		testParameters(jdtParameters, spxParameters);

		// Compare statements
		final Block jdtBlock = jdtMethod.getBody();
		final IStrategoTerm spxStatements = getMethodBody(spxMethod);
		compareNulls(jdtBlock, spxStatements, "Method body");
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
		compareNulls(jdtBlock, spxStatements, "Constructor body");
		if(!containsNulls(jdtBlock, spxStatements)) {
			final List jdtStatements = jdtBlock.statements();
			testStatements(jdtStatements, spxStatements);
		}
	}

	public void testParameters(List jdtParams, IStrategoTerm spxParams) {
		if(compareArity(jdtParams, spxParams, "Parameters")) {
			for(int i = 0; i < jdtParams.size(); ++i) {
				final SingleVariableDeclaration jdtParam = (SingleVariableDeclaration) jdtParams.get(i);
				final IStrategoTerm spxParam = spxParams.getSubterm(i);

				final ITypeBinding jdtTypeBinding = jdtParam.getType().resolveBinding();
				final IStrategoTerm spxTypeBinding = getParamType(spxParam);
				final boolean result = compareTypeBindings(jdtTypeBinding, spxTypeBinding);
				logger.result(result, "Parameter type", jdtTypeBinding, spxTypeBinding);
			}
		}
	}

	public void testExpressions(List jdtExpressions, IStrategoTerm spxExpressions) {
		if(compareArity(jdtExpressions, spxExpressions, "Expressions")) {
			for(int i = 0; i < jdtExpressions.size(); ++i) {
				final Expression jdtExpression = (Expression) jdtExpressions.get(i);
				final IStrategoTerm spxExpression = spxExpressions.getSubterm(i);
				testExpression(jdtExpression, spxExpression);
			}
		}
	}

	public void testExpression(Expression jdtExpression, IStrategoTerm spxExpression) {
		// Compare expression type
		// Cannot compare type of anonymous class instantiations, this is done in separate check
		if(!(jdtExpression instanceof ClassInstanceCreation)) {
			final ITypeBinding jdtTypeBinding = jdtExpression.resolveTypeBinding();
			final Iterable<IStrategoTerm> spxTypeBinding = resolveExpressionType(spxExpression);
			final boolean typeResult = compareTypeBindings(jdtTypeBinding, spxTypeBinding);
			logger.result(typeResult, "Expression type", jdtTypeBinding, spxTypeBinding);
		}

		if(jdtExpression instanceof ArrayAccess) {
			final ArrayAccess jdtArrayAccess = (ArrayAccess) jdtExpression;

			testExpression(jdtArrayAccess.getArray(), getArrayAccessArrayExpr(spxExpression));
			testExpression(jdtArrayAccess.getIndex(), getArrayAccessIndexExpr(spxExpression));

			return;
		}
		if(jdtExpression instanceof ArrayInitializer) {
			final ArrayInitializer jdtArrayInit = (ArrayInitializer) jdtExpression;

			final List jdtExprs = jdtArrayInit.expressions();
			final IStrategoTerm spxExprs = getArrayInitializerExprs(spxExpression);
			testExpressions(jdtExprs, spxExprs);

			return;
		}
		if(jdtExpression instanceof Assignment) {
			final Assignment jdtAssignment = (Assignment) jdtExpression;

			testExpression(jdtAssignment.getLeftHandSide(), getAssignLeftExpr(spxExpression));
			testExpression(jdtAssignment.getRightHandSide(), getAssignRightExpr(spxExpression));

			return;
		}
		if(jdtExpression instanceof CastExpression) {
			final CastExpression jdtCast = (CastExpression) jdtExpression;

			final ITypeBinding jdtCastTypeBinding = jdtCast.getType().resolveBinding();
			final IStrategoTerm spxCastTypeBinding = getCastType(spxExpression);
			final boolean castResult = compareTypeBindings(jdtCastTypeBinding, spxCastTypeBinding);
			logger.result(castResult, "Cast type", jdtCastTypeBinding, spxCastTypeBinding);
			testExpression(jdtCast.getExpression(), getCastExpr(spxExpression));

			return;
		}
		if(jdtExpression instanceof ClassInstanceCreation) {
			final ClassInstanceCreation jdtConsInvoke = (ClassInstanceCreation) jdtExpression;

			final AnonymousClassDeclaration jdtAnonClassDec = jdtConsInvoke.getAnonymousClassDeclaration();
			final IStrategoTerm spxBodyDecls = getConsInvokeAnonClassDeclBodyDecls(spxExpression);
			compareNulls(jdtAnonClassDec, spxBodyDecls, "Anonymous class declaration");
			if(!containsNulls(jdtAnonClassDec, spxBodyDecls)) {
				final List jdtBodyDecls = jdtAnonClassDec.bodyDeclarations();
				testBodyDeclarations(jdtBodyDecls, spxBodyDecls);
			} else {
				final ITypeBinding jdtTypeBinding = jdtExpression.resolveTypeBinding();
				final Iterable<IStrategoTerm> spxTypeBinding = resolveExpressionType(spxExpression);
				final boolean typeResult = compareTypeBindings(jdtTypeBinding, spxTypeBinding);
				logger.result(typeResult, "Constructor expression type", jdtTypeBinding, spxTypeBinding);

				final ITypeBinding jdtConsTypeBinding = jdtConsInvoke.getName().resolveTypeBinding();
				final Iterable<IStrategoTerm> spxConsTypeBinding =
					resolveExpressionType(getConsInvokeType(spxExpression));
				final boolean consTypeResult = compareTypeBindings(jdtConsTypeBinding, spxConsTypeBinding);
				logger.result(consTypeResult, "Constructor invocation type", jdtConsTypeBinding, spxConsTypeBinding);
			}

			final List jdtArguments = jdtConsInvoke.arguments();
			final IStrategoTerm spxArguments = getConsInvokeArgs(spxExpression);
			testExpressions(jdtArguments, spxArguments);

			return;
		}
		if(jdtExpression instanceof FieldAccess) {
			final FieldAccess jdtFieldAccess = (FieldAccess) jdtExpression;

			testExpression(jdtFieldAccess.getExpression(), getFieldAccessExpr(spxExpression));

			final IVariableBinding jdtFieldBinding = jdtFieldAccess.resolveFieldBinding();
			final Iterable<IStrategoTerm> spxFieldinding = resolveNameBindings(getFieldAccessName(spxExpression));
			final boolean fieldNameResult = compareVarName(jdtFieldBinding, spxFieldinding);
			logger.result(fieldNameResult, "Field access name", jdtFieldBinding, spxFieldinding);

			return;
		}
		if(jdtExpression instanceof MethodInvocation) {
			final MethodInvocation jdtMethodInvoke = (MethodInvocation) jdtExpression;

			final List jdtArguments = jdtMethodInvoke.arguments();
			final IStrategoTerm spxArguments = getMethodInvokeArgs(spxExpression);
			testExpressions(jdtArguments, spxArguments);

			final IMethodBinding jdtMethodBinding = jdtMethodInvoke.resolveMethodBinding();
			final Iterable<IStrategoTerm> spxMethodBinding = resolveNameBindings(getMethodInvokeName(spxExpression));
			final boolean methodNameResult = compareMethodName(jdtMethodBinding, spxMethodBinding);
			logger.result(methodNameResult, "Method invocation name", jdtMethodBinding, spxMethodBinding);

			return;
		}
		if(jdtExpression instanceof Name) {
			final Name jdtName = (Name) jdtExpression;

			final IBinding jdtNameBinding = jdtName.resolveBinding();
			final Iterable<IStrategoTerm> spxNameBinding = resolveNameBindings(spxExpression);
			final boolean nameResult = compareBindings(jdtNameBinding, spxNameBinding);
			logger.result(nameResult, "Any name", jdtNameBinding, spxNameBinding);

			return;
		}
	}

	public void testStatements(List jdtStatements, IStrategoTerm spxStatements) {
		if(compareArity(jdtStatements, spxStatements, "Statements")) {
			for(int i = 0; i < jdtStatements.size(); ++i) {
				final Statement jdtStatement = (Statement) jdtStatements.get(i);
				final IStrategoTerm spxStatement = spxStatements.getSubterm(i);
				testStatement(jdtStatement, spxStatement);
			}
		}
	}

	public void testStatement(Statement jdtStatement, IStrategoTerm spxStatement) {
		if(jdtStatement instanceof Block) {
			final Block jdtBlock = (Block) jdtStatement;
			final List jdtStatements = jdtBlock.statements();
			final IStrategoTerm spxStatements = getBlockStatements(spxStatement);
			testStatements(jdtStatements, spxStatements);

			return;
		}
		if(jdtStatement instanceof ExpressionStatement) {
			final ExpressionStatement jdtExprStmt = (ExpressionStatement) jdtStatement;
			final Expression jdtExpression = jdtExprStmt.getExpression();
			final IStrategoTerm spxExpression = spxStatement;
			testExpression(jdtExpression, spxExpression);

			return;
		}
		if(jdtStatement instanceof IfStatement) {
			final IfStatement jdtIfStatement = (IfStatement) jdtStatement;

			final Expression jdtExpression = jdtIfStatement.getExpression();
			final IStrategoTerm spxExpression = getIfExpression(spxStatement);
			testExpression(jdtExpression, spxExpression);

			final Statement jdtThenStatement = jdtIfStatement.getThenStatement();
			final IStrategoTerm spxThenStatement = getIfThenStatement(spxStatement);
			testStatement(jdtThenStatement, spxThenStatement);

			final Statement jdtElseStatement = jdtIfStatement.getElseStatement();
			final IStrategoTerm spxElseStatement = getIfElseStatement(spxStatement);
			compareNulls(jdtElseStatement, spxElseStatement, "Else");
			if(!containsNulls(jdtElseStatement, spxElseStatement))
				testStatement(jdtThenStatement, spxThenStatement);

			return;
		}
		if(jdtStatement instanceof ForStatement) {
			final ForStatement jdtForStatement = (ForStatement) jdtStatement;

			final Expression jdtCondition = jdtForStatement.getExpression();
			final IStrategoTerm spxCondition = getForCondition(spxStatement);
			compareNulls(jdtCondition, spxCondition, "For condition");
			if(!containsNulls(jdtCondition, spxCondition))
				testExpression(jdtCondition, spxCondition);

			final List jdtUpdaters = jdtForStatement.updaters();
			final IStrategoTerm spxUpdaters = getForUpdaters(spxStatement);
			testExpressions(jdtUpdaters, spxUpdaters);

			final Statement jdtBody = jdtForStatement.getBody();
			final IStrategoTerm spxBody = getForBody(spxStatement);
			testStatement(jdtBody, spxBody);

			return;
		}
		if(jdtStatement instanceof WhileStatement) {
			final WhileStatement jdtWhileStatement = (WhileStatement) jdtStatement;

			final Expression jdtCondition = jdtWhileStatement.getExpression();
			final IStrategoTerm spxCondition = getWhileCondition(spxStatement);
			testExpression(jdtCondition, spxCondition);

			final Statement jdtBody = jdtWhileStatement.getBody();
			final IStrategoTerm spxBody = getWhileBody(spxStatement);
			testStatement(jdtBody, spxBody);

			return;
		}
		if(jdtStatement instanceof DoStatement) {
			final DoStatement jdtDoStatement = (DoStatement) jdtStatement;

			final Statement jdtBody = jdtDoStatement.getBody();
			final IStrategoTerm spxBody = getDoBody(spxStatement);
			testStatement(jdtBody, spxBody);

			final Expression jdtCondition = jdtDoStatement.getExpression();
			final IStrategoTerm spxCondition = getDoCondition(spxStatement);
			testExpression(jdtCondition, spxCondition);

			return;
		}
		if(jdtStatement instanceof TryStatement) {
			final TryStatement jdtTryStatement = (TryStatement) jdtStatement;

			final Statement jdtBody = jdtTryStatement.getBody();
			final IStrategoTerm spxBody = getTryBody(spxStatement);
			testStatement(jdtBody, spxBody);

			final List jdtCatches = jdtTryStatement.catchClauses();
			final IStrategoTerm spxCatches = getTryCatches(spxStatement);
			if(compareArity(jdtCatches, spxCatches, "Catches")) {
				for(int i = 0; i < jdtCatches.size(); ++i) {
					final CatchClause jdtCatch = (CatchClause) jdtCatches.get(i);
					final IStrategoTerm spxCatch = spxCatches.getSubterm(i);

					final Statement jdtCatchBody = jdtCatch.getBody();
					final IStrategoTerm spxCatchBody = getCatchBody(spxCatch);
					testStatement(jdtCatchBody, spxCatchBody);
				}
			}

			final Statement jdtFinally = jdtTryStatement.getFinally();
			final IStrategoTerm spxFinally = getTryFinally(spxStatement);
			compareNulls(jdtFinally, spxFinally, "Finally");
			if(!containsNulls(jdtFinally, spxFinally))
				testStatement(jdtFinally, spxFinally);

			return;
		}
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
			return resolveResults(getUseOrDef(name.getSubterm(0)));
		}
		if(isAppl(name, "PackageName", 1)) {
			return resolveResults(getUseOrDef(name.getSubterm(0)));
		}
		if(isAppl(name, "PackageName", 2)) {
			return resolveResults(getUseOrDef(name.getSubterm(1)));
		}

		if(isAppl(name, "PackageOrTypeName", 1)) {
			return resolveResults(getUseOrDef(name.getSubterm(0)));
		}
		if(isAppl(name, "PackageOrTypeName", 2)) {
			return resolveResults(getUseOrDef(name.getSubterm(1)));
		}

		if(isAppl(name, "ExprName", 1)) {
			return resolveResults(getUseOrDef(name.getSubterm(0)));
		}
		if(isAppl(name, "ExprName", 2)) {
			return resolveResults(getUseOrDef(name.getSubterm(1)));
		}

		if(isAppl(name, "AmbName", 1)) {
			return resolveResults(getUseOrDef(name.getSubterm(0)));
		}
		if(isAppl(name, "AmbName", 2)) {
			return resolveResults(getUseOrDef(name.getSubterm(1)));
		}

		if(isAppl(name, "MethodName", 1)) {
			return resolveResults(getUseOrDef(name.getSubterm(0)));
		}
		if(isAppl(name, "MethodName", 2)) {
			return resolveResults(getUseOrDef(name.getSubterm(1)));
		}

		if(isAppl(name, "TypeName", 1)) {
			return resolveResults(getUseOrDef(name.getSubterm(0)));
		}
		if(isAppl(name, "TypeName", 2)) {
			return resolveResults(getUseOrDef(name.getSubterm(1)));
		}

		return resolveResults(getUseOrDef(name));
	}



	private boolean compareArity(List jdtList, IStrategoTerm spxTerm, String context) {
		boolean jdtEmpty = jdtList == null || jdtList.size() == 0;
		boolean spxEmpty = spxTerm == null || spxTerm.getSubtermCount() == 0;

		if(jdtEmpty ^ spxEmpty) {
			logger.failure(context + ", emptyness", jdtEmpty, spxEmpty);
			return false;
		}

		if(jdtEmpty && spxEmpty) {
			logger.success(context + ", emptyness", jdtEmpty, spxEmpty);
			return true;
		}

		if(jdtList.size() != spxTerm.getSubtermCount()) {
			logger.failure(context + ", arity", jdtList.size(), spxTerm.getSubtermCount());
			return false;
		}

		logger.success(context + ", arity", jdtList.size(), spxTerm.getSubtermCount());
		return true;
	}

	private boolean compareNulls(Object jdtObj, Object spxObj, String context) {
		if(jdtObj == null ^ spxObj == null) {
			logger.failure(context + ", nullness", jdtObj, spxObj);
			return false;
		}
		logger.success(context + ", nullness", jdtObj, spxObj);
		return true;
	}

	private boolean compareBindingFailure(IBinding jdtBinding, IStrategoTerm spxBinding) {
		if(jdtBinding == null && spxBinding == null) {
			logger.innerSuccess("Binding, failure", jdtBinding, spxBinding);
			return true;
		}
		if(jdtBinding == null ^ spxBinding == null) {
			logger.innerFailure("Binding, failure", jdtBinding, spxBinding);
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
			logger.innerFailure("Type kind", jdtType.getQualifiedName(), spxType);
			return false;
		}

		if(jdtType.isPrimitive()) {
			boolean success = comparePrimTypeBindings(jdtType, spxType);
			logger.innerResult(success, "Primitive type", jdtType.getQualifiedName(), spxType);
			return success;
		} else if(jdtType.isArray()) {
			boolean success = compareArrayTypeBindings(jdtType, spxType);
			logger.innerResult(success, "Array type", jdtType.getQualifiedName(), spxType);
			return success;
		} else if(jdtType.isNullType()) {
			boolean success = isAppl(spxType, "Null", 0);
			logger.innerResult(success, "Array type", jdtType.getQualifiedName(), spxType);
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
			logger.innerFailure("Array dimension ", jdtDim, spxDim);
			return false;
		}

		final ITypeBinding jdtInnerType = jdtArrayType.getElementType();
		final IStrategoTerm spxInnerType = getArrayTypeInnerType(spxArrayType);

		if(!compareTypeBindings(jdtInnerType, spxInnerType)) {
			logger.innerFailure("Array element type", jdtInnerType.getQualifiedName(), spxInnerType);
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
		if(containsNulls(jdtType, spxTypeDef))
			return true;

		return compareRefTypeBindingsURI(jdtType, spxTypeDef.getSubterm(0));
	}

	private boolean compareRefTypeBindingsURI(ITypeBinding jdtType, IStrategoTerm spxTypeURI) {
		if(!compareBindingFailure(jdtType, spxTypeURI))
			return false;
		if(containsNulls(jdtType, spxTypeURI))
			return true;

		if(!compareKind(jdtType.getKind(), uriNamespace(spxTypeURI))) {
			logger.innerFailure("Ref type kind", jdtType.getKind(), uriNamespace(spxTypeURI));
			return false;
		}

		// Cannot compare names of anonymous classes.
		if(!jdtType.isAnonymous() && !compareQualifiedName(jdtType.getQualifiedName(), spxTypeURI)) {
			logger.innerFailure("Ref type name", jdtType.getQualifiedName(), spxTypeURI);
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

		if(!compareKind(jdtVarName.getKind(), uriNamespace(spxVarNameURI))) {
			logger.innerFailure("Var name kind", jdtVarName.getKind(), uriNamespace(spxVarNameURI));
			return false;
		}

		if(!jdtVarName.getName().equals(uriName(spxVarNameURI))) {
			logger.innerFailure("Var name", jdtVarName.getName(), uriName(spxVarNameURI));
			return false;
		}

		if(jdtVarName.isField()) {
			final ITypeBinding jdtType = jdtVarName.getDeclaringClass();
			final IStrategoTerm spxTypeURI = uriParentUntilNs(spxVarNameURI, appl("NablNsType"));
			final boolean result = compareRefTypeBindingsURI(jdtType, spxTypeURI);
			logger.innerResult(result, "Field def type", jdtType, spxTypeURI);
			return result;
		} else {
			final int jdtVarID = jdtVarName.getVariableId();
			final int spxVarID = getIndexVarID(spxVarNameURI);
			final boolean varIDResult = jdtVarID == spxVarID;
			logger.innerResult(varIDResult, "Variable ID", jdtVarID, spxVarID);
			if(!varIDResult)
				return false;

			final IMethodBinding jdtMethod = jdtVarName.getDeclaringMethod();
			IStrategoTerm spxMethodURI = uriParentUntilNs(spxVarNameURI, appl("NablNsMethod"));
			if(spxMethodURI == null)
				spxMethodURI = uriParentUntilNs(spxVarNameURI, appl("NablNsConstructor"));
			final boolean result = compareMethodNameURI(jdtMethod, spxMethodURI);
			logger.innerResult(result, "Var def type", jdtMethod, spxMethodURI);
			return result;
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

		if(!compareKind(jdtMethodName.getKind(), uriNamespace(spxMethodNameURI))) {
			logger.innerFailure("Method name kind", jdtMethodName.getKind(), uriNamespace(spxMethodNameURI));
			return false;
		}

		if(jdtMethodName.isConstructor()) {
			if(!uriName(spxMethodNameURI).equals("constructor")) {
				logger.innerFailure("Constructorness", jdtMethodName.isConstructor(),
					uriName(spxMethodNameURI).equals("constructor"));
				return false;
			}
		} else {
			if(!jdtMethodName.getName().equals(uriName(spxMethodNameURI))) {
				logger.innerFailure("Method name", jdtMethodName.getName(), uriName(spxMethodNameURI));
				return false;
			}
		}

		final ITypeBinding jdtType = jdtMethodName.getDeclaringClass();
		final IStrategoTerm spxTypeURI = uriParentUntilNs(spxMethodNameURI, appl("NablNsType"));
		final boolean result = compareRefTypeBindingsURI(jdtType, spxTypeURI);
		logger.innerResult(result, "Method def type", jdtType, spxTypeURI);
		return result;
	}


	private boolean compareKind(int kind, IStrategoTerm namespace) {
		boolean result = false;
		switch (kind) {
			case IBinding.PACKAGE:
				result = isAppl(namespace, "NablNsPackage") || isAppl(namespace, "NablNsDefaultPackage");
				break;
			case IBinding.TYPE:
				result = isAppl(namespace, "NablNsType");
				break;
			case IBinding.VARIABLE:
				result = isAppl(namespace, "NablNsVariable") || isAppl(namespace, "NablNsField");
				break;
			case IBinding.METHOD:
				result = isAppl(namespace, "NablNsMethod") || isAppl(namespace, "NablNsConstructor");
				break;
		}
		logger.innerResult(result, "Kind", kind, namespace);
		return result;
	}

	private boolean compareQualifiedName(String jdtQName, IStrategoTerm spxURI) {
		final String[] jdtparts = jdtQName.split("\\.");
		final IStrategoTerm spxparts = uriSegments(spxURI);
		final int spxpartsLength = spxparts.getSubtermCount() - 1;
		if(jdtparts.length != spxpartsLength) {
			logger.innerFailure("Qualified name, length", jdtQName, spxURI);
			return false;
		}

		// Ignore default package from URI by ignoring the last element.
		for(int i = 0; i < spxpartsLength - 1; ++i) {
			final String jdtname = jdtparts[jdtparts.length - 1 - i];
			final String spxname = segmentName(spxparts.getSubterm(i));
			if(!jdtname.equals(spxname)) {
				logger.innerFailure("Qualified name, equality", jdtQName, spxURI);
				return false;
			}
		}

		logger.success("Qualified name", jdtQName, spxURI);
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
		if(jdtObj == null && spxObj == null) {
			return true;
		}
		if(jdtObj == null ^ spxObj == null) {
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

	// private IStrategoTerm getIndexType(IStrategoTerm uri) {
	// return getIndexProperty(uri, appl("Type"));
	// }

	private int getIndexVarID(IStrategoTerm uri) {
		IStrategoTerm varID = getIndexProperty(uri, appl("NablProp_var-id"));
		if(varID == null)
			return -1;
		else
			return ((IStrategoInt) varID).intValue() - 1; // Subtract one because Spoofax begins at 1, whereas JDT
															// begins at 0.
	}

	private boolean inheritsFromObject(IStrategoTerm term) {
		if(isAppl(term, "ClassType", 2)) {
			final IStrategoTerm typeName = term.getSubterm(0);
			if(isAppl(typeName, "TypeName", 1)) {
				final IStrategoString name = (IStrategoString) typeName.getSubterm(0);
				return name.stringValue().equals("Object");
			} else if(isAppl(typeName, "TypeName", 2)) {
				final IStrategoString name = (IStrategoString) typeName.getSubterm(1);
				return name.stringValue().equals("Object");
			}
		}
		return false;
	}
}
