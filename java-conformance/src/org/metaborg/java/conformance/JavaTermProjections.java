package org.metaborg.java.conformance;

import static org.metaborg.java.conformance.util.TermTools.*;

import org.spoofax.interpreter.terms.IStrategoTerm;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.google.common.base.Predicate;

public class JavaTermProjections {
	public static IStrategoTerm getTypeImport(IStrategoTerm term) {
		if(isAppl(term, "TypeImportDec")) {
			return term.getSubterm(0);
		}
		return null;
	}

	public static IStrategoTerm getTypesInPackage(IStrategoTerm term) {
		final IStrategoTerm innerPackage = collectOne(term, new Predicate<IStrategoTerm>() {
			@Override
			public boolean apply(IStrategoTerm input) {
				return isAppl(input, "PackageDec", 3);
			}
		});
		
		if(innerPackage != null)
			return innerPackage.getSubterm(2);
		
		return term.getSubterm(1);
	}


	public static IStrategoTerm getSupertype(IStrategoTerm term) {
		final IStrategoTerm superClass = term.getSubterm(5);
		if(isAppl(superClass, "SuperDec"))
			return superClass.getSubterm(0);
		else
			return null;
	}

	public static IStrategoTerm getClassSuperinterfaces(IStrategoTerm term) {
		final IStrategoTerm superInterfaces = term.getSubterm(6);
		if(isList(superInterfaces))
			return superInterfaces;
		else
			return null;
	}

	public static IStrategoTerm getImplementsInterface(IStrategoTerm term) {
		return term.getSubterm(1);
	}


	public static IStrategoTerm getInterfaceSuperinterfaces(IStrategoTerm term) {
		final IStrategoTerm superInterfaces = term.getSubterm(4);
		if(isList(superInterfaces))
			return superInterfaces;
		else
			return null;
	}

	public static IStrategoTerm getExtendsInterface(IStrategoTerm term) {
		return term.getSubterm(1);
	}


	public static IStrategoTerm getClassBodyDeclarations(IStrategoTerm term) {
		return term.getSubterm(7).getSubterm(0);
	}


	public static IStrategoTerm getFieldType(IStrategoTerm field) {
		return field.getSubterm(3);
	}

	public static IStrategoTerm getFieldInit(IStrategoTerm field) {
		final IStrategoTerm varDec = field.getSubterm(4);
		if(varDec.getSubtermCount() == 1)
			return null;
		return varDec.getSubterm(1);
	}


	public static IStrategoTerm getMethodReturnType(IStrategoTerm method) {
		return method.getSubterm(4);
	}

	public static IStrategoTerm getMethodParams(IStrategoTerm method) {
		return method.getSubterm(6).getSubterm(0);
	}

	public static IStrategoTerm getMethodBody(IStrategoTerm method) {
		// TODO: check for abstract method.
		return getBlockStatements(method.getSubterm(8));
	}


	public static IStrategoTerm getConstructorParams(IStrategoTerm constructor) {
		return constructor.getSubterm(0).getSubterm(4);
	}

	public static IStrategoTerm getConstructorBody(IStrategoTerm constructor) {
		return constructor.getSubterm(1).getSubterm(6);
	}


	public static IStrategoTerm getBlockStatements(IStrategoTerm block) {
		return block.getSubterm(0);
	}


	public static IStrategoTerm getParamType(IStrategoTerm param) {
		return param.getSubterm(1);
	}


	public static IStrategoTerm getArrayAccessArrayExpr(IStrategoTerm arrayAccess) {
		return arrayAccess.getSubterm(0);
	}

	public static IStrategoTerm getArrayAccessIndexExpr(IStrategoTerm arrayAccess) {
		return arrayAccess.getSubterm(1);
	}


	public static IStrategoTerm getAssignLeftExpr(IStrategoTerm assign) {
		return assign.getSubterm(0);
	}

	public static IStrategoTerm getAssignRightExpr(IStrategoTerm assign) {
		return assign.getSubterm(1);
	}


	public static IStrategoTerm getCastType(IStrategoTerm cast) {
		return cast.getSubterm(0);
	}

	public static IStrategoTerm getCastExpr(IStrategoTerm cast) {
		return cast.getSubterm(1);
	}


	public static IStrategoTerm getConsInvokeType(IStrategoTerm consInvoke) {
		return consInvoke.getSubterm(1);
	}

	public static IStrategoTerm getConsInvokeArgs(IStrategoTerm consInvoke) {
		return consInvoke.getSubterm(2);
	}


	public static IStrategoTerm getFieldAccessExpr(IStrategoTerm fieldAccess) {
		// TODO: implement
		throw new NotImplementedException();
	}

	public static IStrategoTerm getFieldAccessName(IStrategoTerm fieldAccess) {
		// TODO: implement
		throw new NotImplementedException();
	}


	public static IStrategoTerm getMethodInvokeName(IStrategoTerm methodInvoke) {
		return methodInvoke.getSubterm(0).getSubterm(0);
	}

	public static IStrategoTerm getMethodInvokeArgs(IStrategoTerm methodInvoke) {
		return methodInvoke.getSubterm(1).getSubterm(0);
	}


	public static int getArrayTypeDimension(IStrategoTerm arrayType) {
		int dimension = 0;
		while(arrayType != null) {
			if(isAppl(arrayType, "ArrayType", 1)) {
				++dimension;
				arrayType = getArrayTypeInnerType(arrayType);
			} else {
				break;
			}
		}
		return dimension;
	}

	public static IStrategoTerm getArrayTypeInnerType(IStrategoTerm arrayType) {
		return arrayType.getSubterm(0);
	}
}
