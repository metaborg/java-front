package jar2index;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.index.IndexPartition;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.ITermFactory;

public class BytecodeReader {
	private final IOAgent agent;
	private final ITermFactory termFactory;
	private final IIndex index;
	private final IndexEntryFactory factory;

	public BytecodeReader(IOAgent agent, ITermFactory termFactory, String projectPath) {
		this.agent = agent;
		this.termFactory = termFactory;
		this.index = IndexManager.getInstance().loadIndex(projectPath, "Java", termFactory, agent);
		this.factory = new IndexEntryFactory(termFactory);
	}

	public void fromJar(File file) throws IOException {
		final IndexPartition partition = IndexPartition.fromTerm(agent, termFactory.makeString(file.getAbsolutePath()));
		index.startCollection(partition);

		final Set<IStrategoAppl> entries = new HashSet<IStrategoAppl>();

		final ZipFile zipFile = new ZipFile(file);
		final ZipInputStream zipIn = new ZipInputStream(new FileInputStream(file));

		try {
			ZipEntry entry = null;
			while((entry = zipIn.getNextEntry()) != null) {
				if(entry.isDirectory() || !entry.getName().endsWith(".class"))
					continue;

				final InputStream in = zipFile.getInputStream(entry);
				try {
					fromClass(in, partition, entries);
				} finally {
					in.close();
				}
			}
		} finally {
			zipIn.close();
			zipFile.close();
		}

		final org.spoofax.interpreter.library.index.IndexEntryFactory indexEntryFactory = index.getFactory();
		for(IStrategoAppl entryTerm : entries) {
			final IndexEntry entry = indexEntryFactory.createEntry(entryTerm, partition);
			index.add(entry);
		}

		index.stopCollection();
		IndexManager.getInstance().storeCurrent(termFactory);
	}

	private void fromClass(InputStream in, final IndexPartition partition, final Set<IStrategoAppl> entries)
		throws IOException {
		final ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5) {
			public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
				System.out.println(name);
				for(IStrategoAppl entryTerm : factory.clazz(name, superName, interfaces)) {
					System.out.println(entryTerm);
					entries.add(entryTerm);
				}
			}

			public MethodVisitor
				visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				for(IStrategoAppl entryTerm : factory.method(name, desc)) {
					entries.add(entryTerm);
				}

				return null;
			}

		};

		final ClassReader reader = new ClassReader(in);
		reader.accept(visitor, 0);
	}
}
