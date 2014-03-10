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
		final CompilationUnit jdtcu = (CompilationUnit) jdtast;
		final IStrategoTerm spxcu = spxast.getSubterm(0);

		// Compare import name resolution
		final List jdtil = jdtcu.imports();
		final IStrategoTerm spxil = spxcu.getSubterm(1);
		for(int i = 0; i < jdtil.size(); ++i) {
			final ImportDeclaration jdtid = (ImportDeclaration) jdtil.get(i);
			final IStrategoTerm spxid = getTypeImport(spxil.getSubterm(i));
			if(!compareNulls(jdtid, spxid))
				compareTypeBinding((ITypeBinding)jdtid.resolveBinding(), getTypeBindings(spxid));
		}

		// Compare types
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
		// Compare superclass name resolution
		final Name jdtsuper = jdttype.getSuperclass();
		final IStrategoTerm spxsuper = getSupertype(spxtype);
		if(!compareNulls(jdtsuper, spxsuper))
			compareTypeBinding((ITypeBinding)jdtsuper.resolveBinding(), getTypeBindings(spxsuper));

		// Compare implemented interface name resolution
		final List jdtsuperis = jdttype.superInterfaces();
		final IStrategoTerm spxsuperis = getClassSuperinterfaces(spxtype);
		for(int i = 0; i < jdtsuperis.size(); ++i) {
			final Name jdtsuperi = (Name) jdtsuperis.get(i);
			final IStrategoTerm spxsuperi = getImplementsInterface(spxsuperis.getSubterm(i));
			compareTypeBinding((ITypeBinding)jdtsuperi.resolveBinding(), getTypeBindings(spxsuperi));
		}

		// TODO: Check interface implementation error

		// Compare body declarations
		final List jdtbodydecs = jdttype.bodyDeclarations();
		final IStrategoTerm spxbodydecs = getBodyDeclarations(spxtype);
		for(int i = 0; i < jdtbodydecs.size(); ++i) {
			final BodyDeclaration jdtbodydecl = (BodyDeclaration) jdtbodydecs.get(i);
			final IStrategoTerm spxbodydecl = spxbodydecs.getSubterm(i);
			testBodyDeclaration(jdtbodydecl, spxbodydecl);
		}
	}

	public void testInterface(TypeDeclaration jdttype, IStrategoTerm spxtype) {
		// Compare subinterface name resolution
		final List jdtsuperis = jdttype.superInterfaces();
		final IStrategoTerm spxsuperis = getInterfaceSuperinterfaces(spxtype);
		for(int i = 0; i < jdtsuperis.size(); ++i) {
			final Name jdtsuperi = (Name) jdtsuperis.get(i);
			final IStrategoTerm spxsuperi = getExtendsInterface(spxsuperis.getSubterm(i));
			compareTypeBinding((ITypeBinding)jdtsuperi.resolveBinding(), getTypeBindings(spxsuperi));
		}
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
		// Compare type
		final Type jdttype = jdtfield.getType();
		final IStrategoTerm spxtype = getFieldType(spxfield);
		compareTypeBinding((ITypeBinding)jdttype.resolveBinding(), getTypeBindings(spxtype));
		
		// Check and compare the initializer expression
		
	}

	public void testMethodOrConstructor(MethodDeclaration jdtmethod, IStrategoTerm spxmethod) {
		if(jdtmethod.isConstructor())
			testConstructor(jdtmethod, spxmethod);
		else
			testMethod(jdtmethod, spxmethod);
	}
	
	public void testMethod(MethodDeclaration jdtmethod, IStrategoTerm spxmethod) {
		// Compare return type
		
		// Compare parameter types
		
		// Compare expressions
	}

	public void testConstructor(MethodDeclaration jdtconstructor, IStrategoTerm spxconstructor) {
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
			return superClass.getSubterm(0).getSubterm(0); // TODO: this only works if type is a reference type.
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
		return term.getSubterm(0).getSubterm(0);
	}
	
	private IStrategoTerm getInterfaceSuperinterfaces(IStrategoTerm term) {
		final IStrategoTerm superInterfaces = term.getSubterm(0).getSubterm(3);
		if(isList(superInterfaces))
			return superInterfaces;
		else
			return null;
	}
	
	private IStrategoTerm getExtendsInterface(IStrategoTerm term) {
		return term.getSubterm(0).getSubterm(0);
	}

	private IStrategoTerm getBodyDeclarations(IStrategoTerm term) {
		return term.getSubterm(1).getSubterm(0);
	}

	private IStrategoTerm getFieldType(IStrategoTerm term) {
		return term.getSubterm(1).getSubterm(0); // TODO: this only works if type is a reference type.
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

	private boolean compareTypeBinding(ITypeBinding jdtbinding, Iterable<IStrategoTerm> spxbindings) {
		boolean jdtFail = jdtbinding == null;
		boolean spxFail = spxbindings == null || !spxbindings.iterator().hasNext();
		if(jdtFail && spxFail)
			return true;
		if(jdtFail ^ spxFail) {
			error("Incorrect failure state: " + jdtFail + " - " + spxFail);
			return false;
		}

		final IStrategoTerm spxbinding = spxbindings.iterator().next().getSubterm(0);

		System.out.println(jdtbinding.getQualifiedName());
		System.out.println(spxbinding);

		if(!compareKind(jdtbinding.getKind(), uriNamespace(spxbinding))) {
			error("Incorrect kinds: " + jdtbinding.getKind() + " - " + uriNamespace(spxbinding));
		}
		// TODO: COMPARE FULLY QUALIFIED NAME
		if(!compareName(jdtbinding.getQualifiedName(), spxbinding)) {
			error("Incorrect names: " + jdtbinding.getQualifiedName() + " - " + spxbinding);
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

	private boolean compareName(String jdtqname, IStrategoTerm spxqname) {
		final String[] jdtparts = jdtqname.split("\\.");
		final IStrategoTerm spxparts = uriSegments(spxqname);
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
	}
}
