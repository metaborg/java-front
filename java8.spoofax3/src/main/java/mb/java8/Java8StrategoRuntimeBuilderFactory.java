package mb.java8;

import javax.inject.Inject;

import mb.log.api.LoggerFactory;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.resource.ResourceService;
import mb.resource.hierarchical.HierarchicalResource;
import mb.statix.referenceretention.stratego.InteropRegisterer;
import mb.statix.referenceretention.stratego.RRPrimitiveLibrary;
import mb.statix.referenceretention.stratego.RRStrategoContext;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.common.StrategoRuntimeBuilder;
import mb.tego.strategies.runtime.TegoRuntime;

@Java8Scope
public class Java8StrategoRuntimeBuilderFactory extends Java8BaseStrategoRuntimeBuilderFactory {

    private final TegoRuntime tegoRuntime;
    private final StrategoTerms strategoTerms;

	@Inject
    public Java8StrategoRuntimeBuilderFactory(
        LoggerFactory loggerFactory,
        ResourceService resourceService,
        HierarchicalResource definitionDir,
        RRPrimitiveLibrary referenceRetentionPrimitives,
        InteropRegisterer referenceRetentionInteropRegisterer,
        TegoRuntime tegoRuntime,
        StrategoTerms strategoTerms  
    ) {
		super(loggerFactory, resourceService, definitionDir, referenceRetentionPrimitives, referenceRetentionInteropRegisterer);
        this.tegoRuntime = tegoRuntime;
		this.strategoTerms = strategoTerms;
    }

    @Override public StrategoRuntimeBuilder create() {
        return new StrategoRuntimeBuilderRRDecorator(super.create(), tegoRuntime, strategoTerms);
    }

    private static final class StrategoRuntimeBuilderRRDecorator extends StrategoRuntimeBuilder {

        private final TegoRuntime tegoRuntime;
        private final StrategoTerms strategoTerms;

    	StrategoRuntimeBuilderRRDecorator(StrategoRuntimeBuilder other, TegoRuntime tegoRuntime, StrategoTerms strategoTerms) {
    		super(other);
    		this.tegoRuntime = tegoRuntime;
    		this.strategoTerms = strategoTerms;
    	}

    	@Override
    	public StrategoRuntime build() {
    		// Initialize Stratego Runtime with additional RR context.
    		// Combined with the fact that the test execution task (mb.java8.task.Java8TestStrategoTaskDef#getStrategoRuntime)
    		// adds the analysis context object, everything should be set to use reference retention in SPT tests directly.
    		final RRStrategoContext rrctx = new RRStrategoContext(tegoRuntime, strategoTerms, "qualify-reference");
    		final StrategoRuntime runtime = super.build().addContextObject(rrctx);
    		rrctx.strategoRuntime = runtime;    		
    		return runtime;
    	}
    }
}
