package lang.java.javac.strategies;

import java.util.Optional;
import java.util.regex.Pattern;

import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class javac_1_1 extends JavaCStrategy {

    private static final ILogger logger = LoggerUtils.logger(javac_1_1.class);

    public static final javac_1_1 instance = new javac_1_1();

    @Override public IStrategoTerm invoke(Context context, IStrategoTerm current, Strategy pp, IStrategoTerm expect) {
        final String regex = TermUtils.asJavaString(expect).get();
        final Optional<String> out = compileTest(context, current, pp);
        if(regex.isEmpty()) {
            if(out.isPresent()) {
                final String msg = "Expected success, but got " + out.get();
                return context.getFactory().makeString(msg);
            }
        } else {
            if(!out.isPresent()) {
                final String msg = "Expected failure, but got success.";
                return context.getFactory().makeString(msg);
            } else if(!Pattern.compile(regex, Pattern.DOTALL).matcher(out.get()).matches()) {
                final String msg = "Expected message '" + regex + "', but got " + out.get();
                return context.getFactory().makeString(msg);
            }
        }
        return context.getFactory().makeString("");
    }

}