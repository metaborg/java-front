package jar2index;

import java.io.File;
import java.io.IOException;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderOriginTermFactory;
import org.spoofax.terms.TermFactory;

public class Main {
	public static void main(String[] args) {
		try {
			final Interpreter interpreter = new Interpreter();
			final IOAgent agent = interpreter.getIOAgent();
			final ITermFactory factory = new ImploderOriginTermFactory(new TermFactory());
			final BytecodeReader reader = new BytecodeReader(agent, factory, args[0]);
			final File file = new File(args[1]);
			reader.fromJar(file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
