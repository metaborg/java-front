package jar2index;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.TermAttachmentSerializer;
import org.spoofax.terms.io.binary.SAFWriter;
import org.spoofax.terms.io.binary.TermReader;

public class TermConstruction {
	private final ITermFactory factory;
	
	public TermConstruction(ITermFactory factory) {
		this.factory = factory;
	}
	
	public IStrategoTerm cloneTerm(IStrategoTerm term) {
		try {
			// Serialize attachments to annotations.
			final TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
			term = serializer.toAnnotations(term);

			// Write term to memory as byte array.
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			SAFWriter.writeTermToSAFStream(term, out);
			out.flush();

			// Read term from memory.
			TermReader reader = new TermReader(factory);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bis);
			term = reader.parseFromStream(in);

			// Close streams
			out.close();
			in.close();

			// Deserialize annotations to attachements.
			return serializer.fromAnnotations(term, false);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	public IStrategoInt i(int i) {
		return factory.makeInt(i);
	}
	
	public IStrategoString str(String str) {
		return factory.makeString(str);
	}

	public IStrategoAppl appl(String constructor, IStrategoTerm... terms) {
		return factory.makeAppl(factory.makeConstructor(constructor, terms.length), terms);
	}

	public IStrategoList list(IStrategoTerm... terms) {
		return factory.makeList(terms);
	}
	
	public IStrategoList cons(IStrategoTerm head, IStrategoList tail) {
		return factory.makeListCons(head, tail);
	}

	public IStrategoTuple tuple(IStrategoTerm... terms) {
		return factory.makeTuple(terms);
	}

	public IStrategoAppl uri(String language, IStrategoTerm... segments) {
		IStrategoTerm[] reversed = new IStrategoTerm[segments.length];
		for(int i = 0; i < reversed.length; ++i)
			// Paths are reversed in Stratego for easy appending of new names.
			reversed[i] = segments[reversed.length - i - 1];
		return appl("URI", appl("Language", str(language)), list(reversed));
	}

	public IStrategoAppl segment(String namespace, IStrategoTerm name) {
		return appl("ID", appl(namespace), name, appl("NonUnique"));
	}
	
	public IStrategoAppl segment(String namespace, String name) {
		return appl("ID", appl(namespace), str(name), appl("NonUnique"));
	}

	public IStrategoAppl segment(String namespace, String name, String unique) {
		return appl("ID", appl(namespace), str(name), appl("Unique", str(unique)));
	}
	
	public IStrategoAppl segment(String namespace, String name, IStrategoString unique) {
		return appl("ID", appl(namespace), str(name), appl("Unique", unique));
	}


	public IStrategoAppl def(IStrategoTerm uri) {
		return appl("Def", uri);
	}
	
	public IStrategoAppl use(IStrategoTerm uri) {
		return appl("Use", uri);
	}
	
	public IStrategoAppl alias(IStrategoTerm alias, IStrategoTerm of) {
		return appl("Alias", alias, of);
	}

	public IStrategoAppl prop(IStrategoTerm uri, IStrategoTerm kind, IStrategoTerm value) {
		return appl("Prop", uri, kind, value);
	}

	public IStrategoAppl rel(IStrategoTerm l, IStrategoTerm rel, IStrategoTerm r) {
		return appl("RelTuple", l, rel, r);
	}
}
