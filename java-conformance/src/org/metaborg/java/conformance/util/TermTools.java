package org.metaborg.java.conformance.util;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermVisitor;

import com.google.common.base.Predicate;

public class TermTools {
	public static ITermFactory factory;
	
	public static boolean isList(IStrategoTerm term) {
		return term.getTermType() == IStrategoTerm.LIST;
	}

	public static boolean isAppl(IStrategoTerm term, String name) {
		if(term.getTermType() == IStrategoTerm.APPL) {
			final IStrategoAppl appl = (IStrategoAppl) term;
			if(appl.getConstructor().getName().equals(name))
				return true;
		}
		return false;
	}

	public static boolean isAppl(IStrategoTerm term, String name, int arity) {
		if(term.getTermType() == IStrategoTerm.APPL) {
			final IStrategoAppl appl = (IStrategoAppl) term;
			final IStrategoConstructor cons = appl.getConstructor();
			if(cons.getName().equals(name) && cons.getArity() == arity)
				return true;
		}
		return false;
	}
	
	public static boolean isTuple(IStrategoTerm term, int arity) {
		return term.getTermType() == IStrategoTerm.TUPLE && term.getSubtermCount() == arity;
	}
	
	public static boolean isResult(IStrategoTerm term) {
		return isAppl(term, "Result", 1);
	}

	
	
	public static IStrategoString str(String str) {
		return factory.makeString(str);
	}
	
	public static IStrategoInt i(int i) {
		return factory.makeInt(i);
	}

	public static IStrategoAppl appl(String constructor, IStrategoTerm... terms) {
		return factory.makeAppl(factory.makeConstructor(constructor, terms.length), terms);
	}

	public static IStrategoList list(IStrategoTerm... terms) {
		return factory.makeList(terms);
	}

	public static IStrategoTuple tuple(IStrategoTerm... terms) {
		return factory.makeTuple(terms);
	}
	
	
	
	public static IStrategoTerm hd(IStrategoList list) {
		return list.head();
	}
	
	public static IStrategoList tl(IStrategoList list) {
		return list.tail();
	}
	
	
	
	public static IStrategoTerm uriLanguage(IStrategoTerm uri) {
		return uri.getSubterm(0);
	}
	
	public static IStrategoList uriSegments(IStrategoTerm uri) {
		return (IStrategoList)uri.getSubterm(1);
	}

	public static IStrategoTerm uriHeadSegment(IStrategoTerm uri) {
		final IStrategoList segments = uriSegments(uri);
		if(segments.getSubtermCount() == 0)
			return null;
		return segments.getSubterm(0);
	}

	public static IStrategoAppl segmentNamespace(IStrategoTerm segment) {
		return (IStrategoAppl) segment.getSubterm(0);
	}

	public static String segmentName(IStrategoTerm segment) {
		return ((IStrategoString) segment.getSubterm(1)).stringValue();
	}

	public static IStrategoTerm uriNamespace(IStrategoTerm uri) {
		final IStrategoTerm headSegment =uriHeadSegment(uri);
		if(headSegment == null)
			return null;
		return headSegment.getSubterm(0);
	}

	public static String uriName(IStrategoTerm uri) {
		return ((IStrategoString) uriHeadSegment(uri).getSubterm(1)).stringValue();
	}
	
	public static IStrategoTerm uri(IStrategoTerm language, IStrategoTerm segments) {
		return appl("URI", language, segments);
	}
	
	public static IStrategoTerm segmentParent(IStrategoList segments) {
		return tl(segments);
	}
	
	public static IStrategoTerm uriParent(IStrategoTerm uri) {
		final IStrategoList segments = uriSegments(uri);
		if(segments.size() == 0)
			return null;
		return uri(uriLanguage(uri), segmentParent(uriSegments(uri)));
	}
	
	public static IStrategoTerm uriParentUntilNs(IStrategoTerm uri, IStrategoTerm ns) {
		while(uri != null) {
			final IStrategoTerm uriNs = uriNamespace(uri); 
			if(uriNs != null && uriNs.equals(ns))
				return uri;
			uri = uriParent(uri);
		}
		return null;
	}


	
	public static IStrategoTerm getUse(IStrategoTerm term) {
		for(IStrategoTerm anno : term.getAnnotations()) {
			if(isAppl(anno, "Use", 1))
				return anno.getSubterm(0);
		}
		return null;
	}
	
	public static IStrategoTerm getType(IStrategoTerm term) {
		for(IStrategoTerm anno : term.getAnnotations()) {
			if(isTuple(anno, 2) && isAppl(anno.getSubterm(0), "Type", 0))
				return anno.getSubterm(1);
		}
		return null;
	}
	
	
	
	public static IStrategoTerm collectOne(IStrategoTerm term, Predicate<IStrategoTerm> pred) {
		final CollectOneVisitor visitor = new CollectOneVisitor(pred);
		visitor.visit(term);
		return visitor.result();
	}
}

class CollectOneVisitor extends TermVisitor {
	private final Predicate<IStrategoTerm> pred;

	private IStrategoTerm result;

	public CollectOneVisitor(Predicate<IStrategoTerm> pred) {
		this.pred = pred;
	}

	@Override
	public void preVisit(IStrategoTerm term) {
		for(IStrategoTerm anno : term.getAnnotations()) {
			if(pred.apply(anno)) {
				result = anno;
				break;
			}
		}

		if(pred.apply(term)) {
			result = term;
		}
	}

	@Override
	public boolean isDone(IStrategoTerm term) {
		return result != null;
	}

	public IStrategoTerm result() {
		return result;
	}
}
