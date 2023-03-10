package mb.java8.primitives;

import java.io.IOException;
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

	private static final int MILLI_SECONDS_PER_SECOND = 1000;
	private static final Path RESULT_FILE = Paths.get("./rr-benchmark-result.csv");

	public WriteMeasurement() {
		super("write_measurement", 0, 2);
	}

	@Override
	public boolean call(IContext current, Strategy[] strategies, IStrategoTerm[] terms) throws InterpreterException {
		if(terms.length != 2) {
			// Should be redundant due to third constructor argument
			throw new InterpreterException("write_measurement_0_2: Expected 2 term arguments, got " + terms.length);
		}

		final IStrategoTerm testNameTerm = terms[0];
		final IStrategoTerm timeTerm = terms[1];

		if(!TermUtils.isString(testNameTerm)) {
			throw new InterpreterException("write_measurement_0_2: Expected String as first argument, got " + TermUtils.termTypeToString(testNameTerm.getType()));
		}
		if(!TermUtils.isReal(timeTerm)) {
			throw new InterpreterException("write_measurement_0_2: Expected Real as second argument, got " + TermUtils.termTypeToString(timeTerm.getType()));
		}

		final String testName = TermUtils.toJavaString(testNameTerm);
		double time = TermUtils.toJavaReal(timeTerm) * MILLI_SECONDS_PER_SECOND;

		try {
			final byte[] resultLine = formatLine(testName, time).getBytes(StandardCharsets.UTF_8);
			Files.write(RESULT_FILE, resultLine, new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.APPEND });
		} catch (IOException e) {
			throw new InterpreterException("write_measurement_0_2: Exception writing result for test " + testName, e);
		}
		return true;
	}

	private String formatLine(String testName, double time) {
		return String.format("%s; %.0f%n", testName, time);
	}

}
