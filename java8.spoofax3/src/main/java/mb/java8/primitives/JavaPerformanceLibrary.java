package mb.java8.primitives;

import javax.inject.Inject;

import org.spoofax.interpreter.library.AbstractStrategoOperatorRegistry;

public class JavaPerformanceLibrary extends AbstractStrategoOperatorRegistry {
	
	@Inject public JavaPerformanceLibrary() {
		add(new WriteMeasurement());
	}
	

	@Override
	public String getOperatorRegistryName() {
		return JavaPerformanceLibrary.class.getSimpleName();
	}
	
}
