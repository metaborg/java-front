package jar2index;

import java.io.File;
import java.io.IOException;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.ITermFactory;

public class Main {
	public static void main(String[] args) {
		try {
			final Interpreter interpreter = new Interpreter();
			final IOAgent agent = interpreter.getIOAgent();
			final ITermFactory factory = interpreter.getFactory();
			final BytecodeReader reader = new BytecodeReader(agent, factory);
			final File file = new File(args[0]);
			reader.fromJar(file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
