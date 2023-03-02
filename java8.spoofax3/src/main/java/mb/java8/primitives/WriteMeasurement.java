package mb.java8.primitives;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;

public class WriteMeasurement extends AbstractPrimitive {

	private String file = "/Users/aronzwaan/tmp/rr-benchmark-result.csv";
	
	public WriteMeasurement() {
		super("write_measurement", 0, 2);
	}

	@Override
	public boolean call(IContext arg0, Strategy[] arg1, IStrategoTerm[] arg2) throws InterpreterException {
		try {
			System.out.print("Test :::: " + arg2[0] + ";" + arg2[1]);
			
			String test = TermUtils.toJavaString(arg2[0]);
			double time = TermUtils.toJavaInt(arg2[1]);
			
			Path path = Paths.get(file);
			Files.write(path, (test + ";" + time + "\n").getBytes(StandardCharsets.UTF_8), new OpenOption[] { StandardOpenOption.APPEND });
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return true;
	}

}
