package lang.java.javac.strategies;

import org.strategoxt.lang.JavaInteropRegisterer;
import org.strategoxt.lang.Strategy;

public class InteropRegisterer extends JavaInteropRegisterer {
    public InteropRegisterer() {
        super(new Strategy[] { javac_1_0.instance, javac_1_1.instance });
    }
}
