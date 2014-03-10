package org.metaborg.java.conformance;

import static org.metaborg.java.conformance.util.TermTools.*;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.metaborg.runtime.task.ITaskEngine;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.base.Predicate;

@SuppressWarnings({ "rawtypes", "deprecation" })
public class Conformance {
	private final ASTNode jdtast;
	@SuppressWarnings("unused")
	private final IIndex index;
	private final ITaskEngine taskEngine;
	private final IStrategoTerm spxast;

	public Conformance(ASTNode jdtast, IIndex index, ITaskEngine taskEngine, IStrategoTerm spxast) {
		this.jdtast = jdtast;
		this.index = index;
		this.taskEngine = taskEngine;
		this.spxast = spxast;
	}



	public void testCompilationUnit() {
		// Check import name resolution
		final CompilationUnit jdtcu = (CompilationUnit) jdtast;
		final IStrategoTerm spxcu = spxast.getSubterm(0);

		final List jdtil = jdtcu.imports();
		final IStrategoTerm spxil = spxcu.getSubterm(1);
		for(int i = 0; i < jdtil.size(); ++i) {
			final ImportDeclaration jdtid = (ImportDeclaration) jdtil.get(i);
			final IStrategoTerm spxid = getTypeImport(spxil.getSubterm(i));

			if(!compareNulls(jdtid, spxid))
				compareBinding(jdtid.resolveBinding(), getTypeBindings(spxid));
		}

		// For each type
		final List jdttys = jdtcu.types();
		final IStrategoTerm spxtys = getTypes(spxcu.getSubterm(2));
		for(int i = 0; i < jdttys.size(); ++i) {
			final TypeDeclaration jdtty = (TypeDeclaration) jdttys.get(i);
			final IStrategoTerm spxty = spxtys.getSubterm(i);

			testType(jdtty, spxty);
		}
	}

	public void testType(TypeDeclaration jdttype, IStrategoTerm spxtype) {
		if(jdttype.isInterface()) {
			testInterface(jdttype, spxtype);
		} else {
			testClass(jdttype, spxtype);
		}
	}

	public void testClass(TypeDeclaration jdttype, IStrategoTerm spxtype) {
		// Check superclass name resolution
		final Name jdtsuper = jdttype.getSuperclass();
		final IStrategoTerm spxsuper = getSupertype(spxtype);

		if(!compareNulls(jdtsuper, spxsuper))
			compareBinding(jdtsuper.resolveBinding(), getTypeBindings(spxsuper));

		// Check implemented interface name resolution
		final List jdtsuperis = jdttype.superInterfaces();
		final IStrategoTerm spxsuperis = getSuperinterfaces(spxtype);
		for(int j = 0; j < jdtsuperis.size(); ++j) {
			final Name jdtsuperi = (Name) jdtsuperis.get(j);
			final IStrategoTerm spxsuperi = getImplementsDec(spxsuperis.getSubterm(j));

			compareBinding(jdtsuperi.resolveBinding(), getTypeBindings(spxsuperi));
		}

		// TODO: Check implementation error

		// For each body declaration
		final List jdtbodydecs = jdttype.bodyDeclarations();
		final IStrategoTerm spxbodydecs = getBodyDeclarations(spxtype);

		// Check type of expression
		// For each method
		// For each statement + expression
		// Type and name resolution check
	}

	public void testInterface(TypeDeclaration jdttype, IStrategoTerm spxtype) {
		// Check subinterface name resolution
	}

	public void testBodyDeclaration(BodyDeclaration jdtbodydecl, IStrategoTerm spxbodydecl) {
		if(jdtbodydecl instanceof TypeDeclaration) {
			testType((TypeDeclaration)jdtbodydecl, spxbodydecl);
		} else if(jdtbodydecl instanceof FieldDeclaration) {
			testField((FieldDeclaration)jdtbodydecl, spxbodydecl);
		} else if(jdtbodydecl instanceof MethodDeclaration) {
			testMethodOrConstructor((MethodDeclaration)jdtbodydecl, spxbodydecl);
		}
	}

	public void testField(FieldDeclaration jdtfield, IStrategoTerm spxfield) {

	}

	public void testMethodOrConstructor(MethodDeclaration jdtmethod, IStrategoTerm spxmethod) {
		if(jdtmethod.isConstructor())
			testConstructor(jdtmethod, spxmethod);
		else
			testMethod(jdtmethod, spxmethod);
	}
	
	public void testMethod(MethodDeclaration jdtmethod, IStrategoTerm spxmethod) {

	}

	public void testConstructor(MethodDeclaration jdtconstructor, IStrategoTerm spxconstructor) {

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
			return superClass.getSubterm(0).getSubterm(0);
		else
			return null;
	}

	private IStrategoTerm getSuperinterfaces(IStrategoTerm term) {
		final IStrategoTerm superInterfaces = term.getSubterm(0).getSubterm(4);
		if(isList(superInterfaces))
			return superInterfaces;
		else
			return null;
	}

	private IStrategoTerm getImplementsDec(IStrategoTerm term) {
		return term.getSubterm(0).getSubterm(0);
	}

	private IStrategoTerm getBodyDeclarations(IStrategoTerm term) {
		return term.getSubterm(0).getSubterm(0);
	}



	private Iterable<IStrategoTerm> getTypeBindings(IStrategoTerm term) {
		if(isAppl(term, "TypeName", 2)) {
			return resolveResults(collectUseTask(term.getSubterm(1)));
		} else if(isAppl(term, "TypeName", 1)) {
			return resolveResults(collectUseTask(term));
		}

		return null;
	}



	private boolean compareNulls(Object jdtobj, Object spxobj) {
		if(jdtobj == null && spxobj == null)
			return true;
		if(jdtobj == null ^ spxobj == null) {
			error("Incorrect nulls: " + jdtobj + " - " + spxobj);
			return true;
		}
		return false;
	}

	private boolean compareBinding(IBinding jdtb, Iterable<IStrategoTerm> spxbs) {
		boolean jdtFail = jdtb == null;
		boolean spxFail = spxbs == null || !spxbs.iterator().hasNext();
		if(jdtFail && spxFail)
			return true;
		if(jdtFail ^ spxFail) {
			error("Incorrect failure state: " + jdtFail + " - " + spxFail);
			return false;
		}

		final IStrategoTerm spxb = spxbs.iterator().next().getSubterm(0);

		System.out.println(jdtb.getKey());
		System.out.println(spxb);

		if(!compareKind(jdtb.getKind(), uriNamespace(spxb))) {
			error("Incorrect kinds: " + jdtb.getKind() + " - " + uriNamespace(spxb));
		}
		if(!compareName(jdtb.getName(), uriName(spxb))) {
			error("Incorrect names: " + jdtb.getName() + " - " + uriName(spxb));
		}

		return true;
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

	private boolean compareName(String jdtName, String spxName) {
		return jdtName.equals(spxName);
	}


	private Iterable<IStrategoTerm> resolveResults(IStrategoTerm taskID) {
		return taskEngine.getTask(taskID).results();
	}



	private void error(String message) {
		System.err.println(message);
	}
}
