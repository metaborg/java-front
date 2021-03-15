package lang.java.javac.strategies;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class javac_1_1 extends JavaCStrategy {

    public static final javac_1_1 instance = new javac_1_1();

    @Override public IStrategoTerm invoke(Context context, IStrategoTerm current, Strategy pp, IStrategoTerm expect) {
        final String regex = TermUtils.asJavaString(expect).get();
        final Optional<String> out = compileTest(context, current, pp);
        if(regex.isEmpty()) {
            if(out.isPresent()) {
                // ("Expected success, but got " + out.get());
                return null;
            }
        } else {
            if(!out.isPresent()) {
                // ("Expected failure, but got success.");
                return null;
            } else if(!out.get().matches(regex)) {
                // ("Failure does not match expectation.");
                return null;
            }
        }
        return current;
    }

}