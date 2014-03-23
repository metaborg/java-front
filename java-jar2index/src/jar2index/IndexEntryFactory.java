package jar2index;

import java.util.Collection;
import java.util.LinkedList;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;


public class IndexEntryFactory extends TermConstruction {
	private static final String LANG = "Java";

	private static final String TYPE_NAMESPACE = "NablNsType";
	private static final String PACKAGE_NAMESPACE = "NablNsPackage";
	private static final String DPACKAGE_NAMESPACE = "NablNsDefaultPackage";

	private static final String SUBCLASS_REL = "<extends:";
	private static final String IMPLEMENTS_REL = "<implements:";
	private static final String WIDENING_REL = "<widens-ref:";

	private static final String TYPE_PROP = "Type";
	private static final String KIND_PROP = "NablProp_kind";
	private static final String ACCESS_PROP = "NablProp_access";
	private static final String CONTEXT_PROP = "NablProp_context";
	private static final String MODIFIERS_PROP = "NablProp_modifiers";
	private static final String TYPEPARAMS_PROP = "NablProp_type-parameters";
	private static final String PARAMTYPES_PROP = "NablProp_parameter-types";

	private final ITermFactory factory;

	public IndexEntryFactory(ITermFactory factory) {
		super(factory);
		this.factory = factory;
	}


	public Iterable<IStrategoAppl> clazz(String name, String superName, String[] interfaces) {
		final Collection<IStrategoAppl> entries = new LinkedList<IStrategoAppl>();
		final IStrategoTerm uri = nameToEntries(name, entries);
		final IStrategoTerm superURI = superName != null ? nameToURI(superName) : null;
		final IStrategoTerm[] interfaceURIs = new IStrategoTerm[interfaces.length];
		for(int i = 0; i < interfaces.length; ++i) {
			interfaceURIs[i] = nameToURI(interfaces[i]);
		}

		// Store subclass (+ widening, + transitive) relation
		if(superURI != null) {
			entries.add(rel(uri, appl(SUBCLASS_REL), superURI));
			entries.add(rel(uri, appl(WIDENING_REL), superURI));
			// TODO: transitive
		}

		// Store implements (+ widening, + transitive) relations
		for(IStrategoTerm interfaceURI : interfaceURIs) {
			entries.add(rel(uri, appl(IMPLEMENTS_REL), interfaceURI));
			entries.add(rel(uri, appl(WIDENING_REL), interfaceURI));
		}
		// TODO: transitive

		// Store type property
		entries.add(prop(uri, appl(TYPE_PROP), refType(uri)));

		// Store kind property
		entries.add(prop(uri, appl(KIND_PROP), appl("Class")));

		// Store modifiers property
		entries.add(prop(uri, appl(ACCESS_PROP), appl("Public"))); // TODO: get access modifier
		entries.add(prop(uri, appl(CONTEXT_PROP), appl("Instance"))); // TODO: get static/non-static
		entries.add(prop(uri, appl(MODIFIERS_PROP), list()));

		// Store type-parameters
		entries.add(prop(uri, appl(TYPEPARAMS_PROP), appl("None")));

		return entries;
	}


	public Iterable<IStrategoAppl> method(String name, String descriptor) {
		final Collection<IStrategoAppl> entries = new LinkedList<IStrategoAppl>();
		final IStrategoTerm uri = nameToURI(name);
		final IStrategoTerm types = parseMethodDescriptor(descriptor);
		final IStrategoTerm paramterTypes = types.getSubterm(0);
		final IStrategoTerm returnType = types.getSubterm(1);

		// Store def
		entries.add(def(uri));

		// Store type property
		entries.add(prop(uri, appl(TYPE_PROP), returnType));

		// Store parameter types property
		entries.add(prop(uri, appl(PARAMTYPES_PROP), paramterTypes));

		// Store modifiers property
		entries.add(prop(uri, appl(ACCESS_PROP), appl("Public"))); // TODO: get access modifier
		entries.add(prop(uri, appl(CONTEXT_PROP), appl("Instance"))); // TODO: get static/non-static
		entries.add(prop(uri, appl(MODIFIERS_PROP), list()));
		
		// Store type-parameters
		entries.add(prop(uri, appl(TYPEPARAMS_PROP), appl("None")));

		return entries;
	}


	public IStrategoTerm parseMethodDescriptor(String str) {
		boolean inArguments = false;

		IStrategoList parameterTypes = list();
		IStrategoTerm type = null;

		for(int i = 0; i < str.length();) {
			final char c = str.charAt(i);

			switch(c) {
				case '(':
					inArguments = true;
					++i;
					break;
				case ')':
					inArguments = false;
					++i;
					break;
				default:
					if(inArguments) {
						IStrategoTerm paramTypeTuple;
						while(i < str.length() && (paramTypeTuple = parseMethodDescriptorType(str, i)) != null) {
							final int skip = ((IStrategoInt) paramTypeTuple.getSubterm(0)).intValue();
							final IStrategoTerm paramType = paramTypeTuple.getSubterm(1);
							i += skip;
							parameterTypes = factory.makeListCons(paramType, parameterTypes);
						}
					} else {
						final IStrategoTerm typeTuple = parseMethodDescriptorType(str, i);
						i = str.length();
						type = typeTuple.getSubterm(1);
					}
			}
		}

		return tuple(parameterTypes, type);
	}

	private IStrategoTerm parseMethodDescriptorType(String str, int i) {
		final char c = str.charAt(i);
		switch(c) {
			case 'Z':
				return tuple(i(1), appl("BoolType"));
			case 'C':
				return tuple(i(1), appl("CharType"));
			case 'B':
				return tuple(i(1), appl("ByteType"));
			case 'S':
				return tuple(i(1), appl("ShortType"));
			case 'I':
				return tuple(i(1), appl("IntType"));
			case 'F':
				return tuple(i(1), appl("FloatType"));
			case 'J':
				return tuple(i(1), appl("LongType"));
			case 'D':
				return tuple(i(1), appl("DoubleType"));
			case 'L': {
				final int commaIndex = str.indexOf(';', i);
				final int skip = commaIndex - i;
				return tuple(i(skip + 1), refType(nameToURI(str.substring(i + 1, commaIndex))));
			}
			case '[': {
				final IStrategoTerm innerTypeTuple = parseMethodDescriptorType(str, i + 1);
				final int skip = ((IStrategoInt) innerTypeTuple.getSubterm(0)).intValue();
				final IStrategoTerm innerType = innerTypeTuple.getSubterm(1);
				return tuple(i(skip + 1), arrayType(innerType));
			}
			case 'V':
				return tuple(i(1), appl("VoidType"));
		}
		return null;
	}


	private IStrategoTerm nameToEntries(String name, Collection<IStrategoAppl> entries) {
		final Collection<IStrategoAppl> segments = new LinkedList<IStrategoAppl>();
		segments.add(segment(DPACKAGE_NAMESPACE, appl("Default")));
		
		entries.add(def(uri(LANG, segments.toArray(new IStrategoAppl[0]))));
		
		final String[] names = name.split("/");
		for(int i = 0; i < names.length; ++i) {
			if(i == names.length - 1) {
				final Collection<IStrategoTerm> nonUniqueSegments = new LinkedList<IStrategoTerm>(segments);
				nonUniqueSegments.add(segment(TYPE_NAMESPACE, names[i]));
				final IStrategoAppl nonUniqueURI = uri(LANG, nonUniqueSegments.toArray(new IStrategoAppl[0]));
				
				segments.add(segment(TYPE_NAMESPACE, names[i], "0")); // TODO: gen unique string
				final IStrategoAppl uniqueURI = uri(LANG, segments.toArray(new IStrategoAppl[0]));
				
				entries.add(def(uniqueURI));
				entries.add(alias(nonUniqueURI, uniqueURI));
			}
			else {
				segments.add(segment(PACKAGE_NAMESPACE, names[i]));
				entries.add(def(uri(LANG, segments.toArray(new IStrategoAppl[0]))));
			}
		}

		return uri(LANG, segments.toArray(new IStrategoAppl[0]));
	}
	
	private IStrategoTerm nameToURI(String name) {
		final String[] names = name.split("/");
		final IStrategoTerm[] segments = new IStrategoTerm[names.length + 1];
		segments[0] = segment(DPACKAGE_NAMESPACE, appl("Default"));
		for(int i = 1; i < names.length + 1; ++i) {
			if(i == names.length)
				segments[i] = segment(TYPE_NAMESPACE, names[i - 1], "0"); // TODO: gen unique string
			else
				segments[i] = segment(PACKAGE_NAMESPACE, names[i - 1]);
		}

		return uri(LANG, segments);
	}


	private IStrategoTerm refType(IStrategoTerm uri) {
		return appl("RefType", appl("TypeName", annotatedName(uri)), appl("None"));
	}

	private IStrategoTerm annotatedName(IStrategoTerm uri) {
		final IStrategoTerm name = cloneTerm(uri.getSubterm(1).getSubterm(0).getSubterm(1));
		return factory.annotateTerm(name, list(def(uri)));
	}

	private IStrategoTerm arrayType(IStrategoTerm type) {
		return appl("ArrayType", type);
	}
}
