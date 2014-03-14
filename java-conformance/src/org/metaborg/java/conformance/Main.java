package org.metaborg.java.conformance;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.metaborg.java.conformance.util.TermTools;
import org.metaborg.java.conformance.util.Util;
import org.metaborg.runtime.task.ITaskEngine;
import org.metaborg.runtime.task.TaskManager;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderOriginTermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.binary.TermReader;

public class Main {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		final String projectDir = args[0];

		final String javaSourcePath = projectDir + args[1];
		final String javaUnitName = args[2];
		final String javaFile = javaSourcePath + javaUnitName;

		final String atermFile = projectDir + args[3];

		try {
			final ASTParser parser = ASTParser.newParser(AST.JLS2);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(Util.getBytes(javaFile));
			parser.setEnvironment(new String[] {}, new String[] { javaSourcePath }, null, true);
			parser.setUnitName(javaUnitName);
			parser.setResolveBindings(true);
			parser.setStatementsRecovery(false);
			parser.setBindingsRecovery(false);
			final CompilationUnit jdtast = (CompilationUnit) parser.createAST(null);

			final ITermFactory termFactory = new ImploderOriginTermFactory(new TermFactory());
			TermTools.factory = termFactory;
			final IOAgent agent = new IOAgent();
			agent.setDefinitionDir(projectDir);
			agent.setWorkingDir(projectDir);
			final TermReader termReader = new TermReader(termFactory);
			final IStrategoTerm spxast = termReader.parseFromFile(atermFile);
			final IIndex index = IndexManager.getInstance().loadIndex(projectDir, "Java", termFactory, agent);
			final ITaskEngine taskEngine = TaskManager.getInstance().loadTaskEngine(projectDir, termFactory, agent);

			final Conformance conformance = new Conformance(jdtast, index, taskEngine, spxast);
			conformance.testCompilationUnit();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
