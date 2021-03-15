package lang.java.javac.strategies;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.metaborg.util.functions.Function1;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public abstract class JavaCStrategy extends Strategy {

    private static final ILogger logger = LoggerUtils.logger(JavaCStrategy.class);

    private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    private static final StandardJavaFileManager fileManager = javac.getStandardFileManager(null, null, null);

    public JavaCStrategy() {
        super();
    }

    protected Optional<String> compileTest(Context context, IStrategoTerm current, Strategy pp) {
        Path srcDir = null;
        try {
            srcDir = Files.createTempDirectory("javac");
            srcDir.toFile().mkdir();

            processTest(current, t -> TermUtils.toJavaString(pp.invoke(context, t)), srcDir);

            final Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjectsFromFiles(
                    Files.walk(srcDir).filter(p -> p.toFile().isFile()).map(Path::toFile).collect(Collectors.toList()));

            final StringWriter out = new StringWriter();
            CompilationTask task = javac.getTask(out, fileManager, null, null, null, sources);
            if(task.call()) {
                return Optional.empty();
            } else {
                return Optional.of(out.toString());
            }
        } catch(IOException e) {
            logger.error("Failed to invoke JavaC.", e);
            return null;
        } finally {
            if(srcDir != null) {
                try {
                    Files.walk(srcDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                } catch(IOException e) {
                    logger.warn("Failed to remove temp directory {}.", e, srcDir);
                }
            }
        }
    }

    private void processTest(IStrategoTerm test, Function1<IStrategoTerm, String> pp, Path srcDir) throws IOException {
        if(!(TermUtils.isAppl(test, "Test", 2))) {
            logger.error("Expected Test/2, got {}", test);
            throw new IllegalArgumentException();
        }
        processPkgOrFileList(test.getSubterm(1), pp, srcDir);
    }

    private void processPkgOrFileList(IStrategoTerm pkgOrFileList, Function1<IStrategoTerm, String> pp, Path srcDir)
            throws IOException {
        for(IStrategoTerm pkgOrFile : TermUtils.asJavaList(pkgOrFileList).get()) {
            processPkgOrFile(pkgOrFile, pp, srcDir);
        }
    }

    private void processPkgOrFile(IStrategoTerm pkgOrFile, Function1<IStrategoTerm, String> pp, Path srcDir)
            throws IOException {
        if(TermUtils.isAppl(pkgOrFile, "TestPkg", 2)) {
            final String name = TermUtils.asJavaStringAt(pkgOrFile, 0).get();
            final Path pkgDir = srcDir.resolve(name);
            pkgDir.toFile().mkdir();
            processPkgOrFileList(pkgOrFile.getSubterm(1), pp, pkgDir);
        } else if(TermUtils.isAppl(pkgOrFile, "TestFile", 2)) {
            final String name = TermUtils.asJavaStringAt(pkgOrFile, 0).get();
            final Path cuFile = srcDir.resolve(name + ".java");
            final IStrategoTerm cu = pkgOrFile.getSubterm(1);
            final String src = pp.apply(cu);
            Files.write(cuFile, Arrays.asList(src.split("[\r\n]+")));
        } else {
            logger.error("Expected TestPkg/2 or TestFile/2, got {}", pkgOrFile);
            throw new IllegalArgumentException();
        }
    }

}