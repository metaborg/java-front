package lang.java.javac.strategies;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class javac_1_0 extends JavaCStrategy {

    public static final javac_1_0 instance = new javac_1_0();

    @Override public IStrategoTerm invoke(Context context, IStrategoTerm current, Strategy pp) {
        final Optional<String> out = compileTest(context, current, pp);
        if(out.isPresent()) {
            return context.getFactory().makeAppl("Some", context.getFactory().makeString(out.get()));
        } else {
            return context.getFactory().makeAppl("None");
        }
    }

}