package jar2index;

import java.util.Collection;
import java.util.LinkedList;

import org.objectweb.asm.Opcodes;
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
	private static final String METHOD_NAMESPACE = "NablNsMethod";
	private static final String CONS_NAMESPACE = "NablNsConstructor";
	private static final String FIELD_NAMESPACE = "NablNsField";

	private static final String SUBCLASS_REL = "<extends:";
	private static final String IMPLEMENTS_REL = "<implements:";
	private static final String WIDENING_REL = "<widens-ref:";

	private static final String TYPE_PROP = "Type";
	private static final String IMPORT_PROP = "Import";
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


	public Iterable<IStrategoAppl> clazz(String name, String superName, String[] interfaces, int access) {
		final Collection<IStrategoAppl> entries = new LinkedList<IStrategoAppl>();
		final IStrategoTerm uri = classNameToEntries(name, entries);
		final IStrategoTerm superURI = superName != null ? classNameToURI(superName) : null;
		final IStrategoTerm[] interfaceURIs = new IStrategoTerm[interfaces.length];
		for(int i = 0; i < interfaces.length; ++i) {
			interfaceURIs[i] = classNameToURI(interfaces[i]);
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
		
		
		// Store imports
		if(superURI != null) {
			entries.add(prop(uri, appl(IMPORT_PROP, appl(FIELD_NAMESPACE)), superURI));
			entries.add(prop(uri, appl(IMPORT_PROP, appl(METHOD_NAMESPACE)), superURI));
			entries.add(prop(uri, appl(IMPORT_PROP, appl(TYPE_NAMESPACE)), superURI));
		}
		// TODO: transitive
		
		for(IStrategoTerm interfaceURI : interfaceURIs) {
			entries.add(prop(uri, appl(IMPORT_PROP, appl(FIELD_NAMESPACE)), interfaceURI));
			entries.add(prop(uri, appl(IMPORT_PROP, appl(METHOD_NAMESPACE)), interfaceURI));
			entries.add(prop(uri, appl(IMPORT_PROP, appl(TYPE_NAMESPACE)), interfaceURI));
			// TODO: transitive
		}
		

		// Store type property
		entries.add(prop(uri, appl(TYPE_PROP), refType(uri)));

		// Store kind property
		if((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE)
			entries.add(prop(uri, appl(KIND_PROP), appl("Interface")));
		else
			entries.add(prop(uri, appl(KIND_PROP), appl("Class")));

		// Store modifiers property
		entries.add(prop(uri, appl(ACCESS_PROP), appl("Public"))); // TODO: get access modifier
		if((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE)
			entries.add(prop(uri, appl(CONTEXT_PROP), appl("Static")));
		else
			entries.add(prop(uri, appl(CONTEXT_PROP), appl("Instance"))); // TODO: get static/non-static
		entries.add(prop(uri, appl(MODIFIERS_PROP), list()));

		// Store type-parameters
		entries.add(prop(uri, appl(TYPEPARAMS_PROP), appl("None")));

		return entries;
	}


	public Iterable<IStrategoAppl> innerClazz(String name, String inner, int access) {
		final Collection<IStrategoAppl> entries = new LinkedList<IStrategoAppl>();
		final IStrategoTerm uri = innerClassNameToEntries(name, inner, entries);

		// Store type property
		entries.add(prop(uri, appl(TYPE_PROP), refType(uri)));

		// Store kind property
		if((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE)
			entries.add(prop(uri, appl(KIND_PROP), appl("Interface")));
		else
			entries.add(prop(uri, appl(KIND_PROP), appl("Class")));

		// Store modifiers property
		entries.add(prop(uri, appl(ACCESS_PROP), appl("Public"))); // TODO: get access modifier
		if((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE)
			entries.add(prop(uri, appl(CONTEXT_PROP), appl("Static")));
		else
			entries.add(prop(uri, appl(CONTEXT_PROP), appl("Instance"))); // TODO: get static/non-static
		entries.add(prop(uri, appl(MODIFIERS_PROP), list()));

		// Store type-parameters
		entries.add(prop(uri, appl(TYPEPARAMS_PROP), appl("None")));

		return entries;
	}


	public Iterable<IStrategoAppl> method(String name, String descriptor, String className, int id) {
		final Collection<IStrategoAppl> entries = new LinkedList<IStrategoAppl>();
		final IStrategoTerm classURI = classNameToURI(className);
		final IStrategoTerm uri = methodNameToEntries(name, classURI, entries, id);
		if(uri == null)
			return entries;
		final IStrategoTerm types = parseMethodDescriptor(descriptor);
		final IStrategoTerm paramterTypes = types.getSubterm(0);
		final IStrategoTerm returnType = types.getSubterm(1);

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

	public Iterable<IStrategoAppl> field(String name, String descriptor, String className, int id) {
		final Collection<IStrategoAppl> entries = new LinkedList<IStrategoAppl>();
		final IStrategoTerm classURI = classNameToURI(className);
		final IStrategoTerm uri = fieldNameToEntries(name, classURI, entries, id);

		// Store type property
		final IStrategoTerm type = parseMethodDescriptorType(descriptor, 0).getSubterm(1);
		entries.add(prop(uri, appl(TYPE_PROP), type));

		// Store modifiers property
		entries.add(prop(uri, appl(ACCESS_PROP), appl("Public"))); // TODO: get access modifier
		entries.add(prop(uri, appl(CONTEXT_PROP), appl("Instance"))); // TODO: get static/non-static
		entries.add(prop(uri, appl(MODIFIERS_PROP), list()));

		return entries;
	}


	public IStrategoTerm parseMethodDescriptor(String str) {
		boolean inArguments = false;

		IStrategoList parameterTypes = list();
		IStrategoTerm type = null;

		for(int i = 0; i < str.length();) {
			final char c = str.charAt(i);

			switch (c) {
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
		switch (c) {
			case 'Z':
				return tuple(i(1), appl("Boolean"));
			case 'C':
				return tuple(i(1), appl("Char"));
			case 'B':
				return tuple(i(1), appl("Byte"));
			case 'S':
				return tuple(i(1), appl("Short"));
			case 'I':
				return tuple(i(1), appl("Int"));
			case 'F':
				return tuple(i(1), appl("Float"));
			case 'J':
				return tuple(i(1), appl("Long"));
			case 'D':
				return tuple(i(1), appl("Double"));
			case 'L': {
				final int commaIndex = str.indexOf(';', i);
				final int skip = commaIndex - i;
				return tuple(i(skip + 1), refType(classNameToURI(str.substring(i + 1, commaIndex))));
			}
			case '[': {
				final IStrategoTerm innerTypeTuple = parseMethodDescriptorType(str, i + 1);
				final int skip = ((IStrategoInt) innerTypeTuple.getSubterm(0)).intValue();
				final IStrategoTerm innerType = innerTypeTuple.getSubterm(1);
				return tuple(i(skip + 1), arrayType(innerType));
			}
			case 'V':
				return tuple(i(1), appl("Void"));
		}
		return null;
	}


	private IStrategoTerm classNameToEntries(String name, Collection<IStrategoAppl> entries) {
		final Collection<IStrategoAppl> segments = new LinkedList<IStrategoAppl>();
		segments.add(segment(DPACKAGE_NAMESPACE, appl("Default")));

		entries.add(def(uri(LANG, segments.toArray(new IStrategoAppl[0]))));

		final String[] names = name.replaceAll("\\$", "").split("/");
		for(int i = 0; i < names.length; ++i) {
			if(i == names.length - 1) {
				final Collection<IStrategoTerm> nonUniqueSegments = new LinkedList<IStrategoTerm>(segments);
				nonUniqueSegments.add(segment(TYPE_NAMESPACE, names[i]));
				final IStrategoAppl nonUniqueURI = uri(LANG, nonUniqueSegments.toArray(new IStrategoAppl[0]));

				segments.add(segment(TYPE_NAMESPACE, names[i], "0")); // TODO: gen unique string
				final IStrategoAppl uniqueURI = uri(LANG, segments.toArray(new IStrategoAppl[0]));

				entries.add(def(uniqueURI));
				entries.add(alias(nonUniqueURI, uniqueURI));
			} else {
				segments.add(segment(PACKAGE_NAMESPACE, names[i]));
				entries.add(def(uri(LANG, segments.toArray(new IStrategoAppl[0]))));
			}
		}

		return uri(LANG, segments.toArray(new IStrategoAppl[0]));
	}

	private IStrategoTerm classNameToURI(String name) {
		final String[] names = name.replaceAll("\\$", "").split("/");
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

	private IStrategoTerm innerClassNameToEntries(String name, String inner, Collection<IStrategoAppl> entries) {
		final String[] names = name.replaceAll("\\$", "").split("/");
		final IStrategoTerm[] segments = new IStrategoTerm[names.length + 1];
		segments[0] = segment(DPACKAGE_NAMESPACE, appl("Default"));
		for(int i = 1; i < names.length + 1; ++i) {
			if(i == names.length)
				segments[i] = segment(TYPE_NAMESPACE, names[i - 1], "0"); // TODO: gen unique string
			else
				segments[i] = segment(PACKAGE_NAMESPACE, names[i - 1]);
		}

		final IStrategoTerm uniqueSegment = segment(TYPE_NAMESPACE, inner, "0");
		final IStrategoTerm nonUniqueSegment = segment(TYPE_NAMESPACE, inner);

		final IStrategoTerm uniqueURI = appl("URI", appl("Language", str(LANG)), cons(uniqueSegment, reverse(segments)));
		final IStrategoTerm nonUniqueURI =
			appl("URI", appl("Language", str(LANG)), cons(nonUniqueSegment, reverse(segments)));

		entries.add(def(uniqueURI));
		entries.add(alias(nonUniqueURI, uniqueURI));

		return uniqueURI;
	}

	private IStrategoTerm methodNameToEntries(String name, IStrategoTerm classURI, Collection<IStrategoAppl> entries,
		int id) {
		if(name.equals("<clinit>"))
			return null;

		final IStrategoList segments = (IStrategoList) classURI.getSubterm(1);
		final IStrategoTerm methodSegment;
		final IStrategoTerm methodSegmentNonUnique;
		if(name.equals("<init>")) {
			methodSegment = segment(CONS_NAMESPACE, "constructor", str(Integer.toString(id)));
			methodSegmentNonUnique = segment(CONS_NAMESPACE, "constructor");
		} else {
			methodSegment = segment(METHOD_NAMESPACE, name, str(Integer.toString(id)));
			methodSegmentNonUnique = segment(METHOD_NAMESPACE, name);
		}

		final IStrategoTerm uri = appl("URI", appl("Language", str(LANG)), cons(methodSegment, segments));
		final IStrategoTerm uriNonUnique =
			appl("URI", appl("Language", str(LANG)), cons(methodSegmentNonUnique, segments));

		entries.add(def(uri));
		entries.add(alias(uriNonUnique, uri));

		return uri;
	}

	private IStrategoTerm fieldNameToEntries(String name, IStrategoTerm classURI, Collection<IStrategoAppl> entries,
		int id) {
		final IStrategoList segments = (IStrategoList) classURI.getSubterm(1);
		final IStrategoTerm fieldSegment = segment(FIELD_NAMESPACE, name, str(Integer.toString(id)));
		final IStrategoTerm fieldSegmentNonUnique = segment(FIELD_NAMESPACE, name);

		final IStrategoTerm uri = appl("URI", appl("Language", str(LANG)), cons(fieldSegment, segments));
		final IStrategoTerm uriNonUnique =
			appl("URI", appl("Language", str(LANG)), cons(fieldSegmentNonUnique, segments));

		entries.add(def(uri));
		entries.add(alias(uriNonUnique, uri));

		return uri;
	}


	private IStrategoTerm refType(IStrategoTerm uri) {
		return appl("RefType", appl("TypeName", annotatedName(uri)), appl("None"));
	}

	private IStrategoTerm annotatedName(IStrategoTerm uri) {
		final IStrategoTerm name = cloneTerm(uri.getSubterm(1).getSubterm(0).getSubterm(1));
		return factory.annotateTerm(name, list(use(def(uri))));
	}

	private IStrategoTerm arrayType(IStrategoTerm type) {
		return appl("ArrayType", type);
	}
}
