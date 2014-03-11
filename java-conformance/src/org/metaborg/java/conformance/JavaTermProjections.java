package org.metaborg.java.conformance;

import static org.metaborg.java.conformance.util.TermTools.isList;

import org.spoofax.interpreter.terms.IStrategoTerm;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class JavaTermProjections {
	public static IStrategoTerm getImplementsInterface(IStrategoTerm term) {
		return term.getSubterm(0);
	}

	
	public static IStrategoTerm getInterfaceSuperinterfaces(IStrategoTerm term) {
		final IStrategoTerm superInterfaces = term.getSubterm(0).getSubterm(3);
		if(isList(superInterfaces))
			return superInterfaces;
		else
			return null;
	}

	
	public static IStrategoTerm getExtendsInterface(IStrategoTerm term) {
		return term.getSubterm(0);
	}

	
	public static IStrategoTerm getBodyDeclarations(IStrategoTerm term) {
		return term.getSubterm(1).getSubterm(0);
	}

	
	public static IStrategoTerm getFieldType(IStrategoTerm field) {
		return field.getSubterm(1);
	}

	public static IStrategoTerm getFieldInit(IStrategoTerm field) {
		final IStrategoTerm varDec = field.getSubterm(2);
		if(varDec.getSubtermCount() == 1)
			return null;
		return varDec.getSubterm(1);
	}

	
	public static IStrategoTerm getMethodReturnType(IStrategoTerm method) {
		return method.getSubterm(0).getSubterm(2);
	}

	public static IStrategoTerm getMethodParams(IStrategoTerm method) {
		return method.getSubterm(0).getSubterm(4).getSubterm(0);
	}

	public static IStrategoTerm getMethodBody(IStrategoTerm method) {
		// TODO: check for abstract method.
		return getBlockStatements(method.getSubterm(1));
	}

	
	public static IStrategoTerm getConstructorParams(IStrategoTerm constructor) {
		// TODO: implement
		throw new NotImplementedException();
	}

	public static IStrategoTerm getConstructorBody(IStrategoTerm constructor) {
		// TODO: implement
		throw new NotImplementedException();
	}

	
	public static IStrategoTerm getBlockStatements(IStrategoTerm block) {
		return block.getSubterm(0);
	}

	
	public static IStrategoTerm getParamType(IStrategoTerm param) {
		return param.getSubterm(1);
	}

	
	public static IStrategoTerm getArrayAccessArrayExpr(IStrategoTerm arrayAccess) {
		// TODO: implement
		throw new NotImplementedException();
	}

	public static IStrategoTerm getArrayAccessIndexExpr(IStrategoTerm arrayAccess) {
		// TODO: implement
		throw new NotImplementedException();
	}

	
	public static IStrategoTerm getAssignLeftExpr(IStrategoTerm assign) {
		// TODO: implement
		throw new NotImplementedException();
	}

	public static IStrategoTerm getAssignRightExpr(IStrategoTerm assign) {
		// TODO: implement
		throw new NotImplementedException();
	}
	
	
	public static IStrategoTerm getCastType(IStrategoTerm cast) {
		// TODO: implement
		throw new NotImplementedException();
	}
	
	public static IStrategoTerm getCastExpr(IStrategoTerm cast) {
		// TODO: implement
		throw new NotImplementedException();
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
	
	
	public static IStrategoTerm getMethodInvokeArgs(IStrategoTerm methodInvoke) {
		// TODO: implement
		throw new NotImplementedException();
	}
	
	public static IStrategoTerm getMethodInvokeName(IStrategoTerm methodInvoke) {
		// TODO: implement
		throw new NotImplementedException();
	}
}
