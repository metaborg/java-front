package org.metaborg.java.conformance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.metaborg.java.conformance.util.TermTools;
import org.metaborg.java.conformance.util.Util;
import org.metaborg.runtime.task.ITaskEngine;
import org.metaborg.runtime.task.TaskManager;
import org.metaborg.sunshine.environment.ServiceRegistry;
import org.metaborg.sunshine.environment.SunshineMainArguments;
import org.metaborg.sunshine.services.analyzer.AnalysisResult;
import org.metaborg.sunshine.services.analyzer.AnalysisService;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderOriginTermFactory;
import org.spoofax.terms.TermFactory;

import com.beust.jcommander.JCommander;
import com.google.common.io.Files;

public class Main {
	public static GlobalResultLogger globalLogger = new GlobalResultLogger();
	
	public static void main(String[] args) {
		final String languageDirArg = args[0];
		final String javaFilesDirArg = args[1];
		final String projectDirArg = args[2];
		final String fixedIndexFileArg = args[3];

		final File javaFilesDir = new File(javaFilesDirArg);
		final File projectDir = new File(projectDirArg);
		final String projectDirPath = projectDir.getAbsolutePath();
		final File fixedIndexFile = new File(fixedIndexFileArg);

		setupSunshine(languageDirArg, projectDirArg);
		final ITermFactory termFactory = new ImploderOriginTermFactory(new TermFactory());
		TermTools.factory = termFactory;
		

		final File[] files = javaFilesDir.listFiles();
		for(File file : files) {
			final String javaFileName = file.getName();
			if(!file.isFile() || !Files.getFileExtension(javaFileName).equals("java"))
				continue;
			
			try {
				FileUtils.cleanDirectory(projectDir);
				
				final File destinationJavaFile = Paths.get(projectDirPath, javaFileName).toFile();
				Files.copy(file, destinationJavaFile);
				
				final String javFileName = javaFileName.substring(0, javaFileName.length() - 1);
				final File destinationJavFile = Paths.get(projectDirPath, javFileName).toFile();
				Files.copy(file, destinationJavFile);
				
				final File destinationIndexFile = Paths.get(projectDirPath, ".cache", "index.idx").toFile();
				Files.createParentDirs(destinationIndexFile);
				Files.copy(fixedIndexFile, destinationIndexFile);

				conformanceCheck(projectDirPath, projectDirPath, javaFileName, destinationJavaFile.getAbsolutePath(),
					destinationJavFile.getAbsolutePath(), termFactory);

				destinationJavaFile.delete();
				destinationJavFile.delete();
				
				FileUtils.cleanDirectory(projectDir);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void conformanceCheck(String projectDir, String javaSourcePath, String javaUnitName,
		String javaFile, String javFile, ITermFactory termFactory) {
		try {			
			final CompilationUnit jdtAST = jdtFromJavaFile(javaSourcePath, javaUnitName, javaFile);
			for(IProblem problem : jdtAST.getProblems()) {
				if(problem.isError() && problem.getMessage().contains("Syntax error")) {
					globalLogger.skip(projectDir, javaUnitName);
					return;
				}
			}
			
			globalLogger.debug("Analyzing: " + javaUnitName);
			final SpoofaxResult spxResult = spoofaxFromJavaFile(termFactory, projectDir, javFile);
			if(spxResult == null) {
				globalLogger.skip(projectDir, javaUnitName);
				return;
			}

			final ResultLogger logger = new ResultLogger(projectDir, javaUnitName, false);
			final Conformance conformance =
				new Conformance(jdtAST, spxResult.index, spxResult.taskEngine, spxResult.ast, logger);
			conformance.testCompilationUnit();
			
			globalLogger.result(projectDir, javaUnitName, logger.numChecks, logger.numSuccess, logger.numFailure);
			ResultLogger.write("log.csv");
			globalLogger.write("results.csv");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private static CompilationUnit jdtFromJavaFile(String sourcePath, String unitName, String file) throws IOException {
		final ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(Util.getBytes(file));
		parser.setEnvironment(new String[] {}, new String[] { sourcePath }, null, true);
		parser.setUnitName(unitName);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(false);
		parser.setBindingsRecovery(false);
		return (CompilationUnit) parser.createAST(null);
	}

	private static SpoofaxResult spoofaxFromJavaFile(ITermFactory termFactory, String projectDir, String file)
		throws IOException {
		final IOAgent agent = new IOAgent();
		IndexManager.getInstance().unloadIndex(projectDir, agent);
		TaskManager.getInstance().unloadTaskEngine(projectDir, agent);
		
		final ServiceRegistry services = ServiceRegistry.INSTANCE();
		final AnalysisService analyzer = services.getService(AnalysisService.class);
		final Collection<AnalysisResult> analyzerResult =
			analyzer.analyze(Arrays.asList(new File[] { new File(file) }));
		if(!analyzerResult.iterator().hasNext())
			return null;
		final AnalysisResult result = analyzerResult.iterator().next();

		final IStrategoTerm ast = result.ast();
		final IIndex index = IndexManager.getInstance().loadIndex(projectDir, "Java", termFactory, agent);
		final ITaskEngine taskEngine = TaskManager.getInstance().loadTaskEngine(projectDir, termFactory, agent);
		return new SpoofaxResult(ast, index, taskEngine);
	}

	private static void setupSunshine(String languageDir, String projectDir) {
		org.metaborg.sunshine.drivers.Main.jc = new JCommander();
		String[] sunshineArgs =
			new String[] { "--project", projectDir, "--auto-lang", languageDir, "--observer", "analysis-default-cmd",
				"--non-incremental" };
		SunshineMainArguments params = new SunshineMainArguments();
		final boolean argsFine = org.metaborg.sunshine.drivers.Main.parseArguments(sunshineArgs, params);
		if(!argsFine) {
			System.exit(1);
		}
		params.validate();
		org.metaborg.sunshine.drivers.Main.initEnvironment(params);
	}
}

class SpoofaxResult {
	public final IStrategoTerm ast;
	public final IIndex index;
	public final ITaskEngine taskEngine;

	public SpoofaxResult(IStrategoTerm ast, IIndex index, ITaskEngine taskEngine) {
		super();
		this.ast = ast;
		this.index = index;
		this.taskEngine = taskEngine;
	}
}
