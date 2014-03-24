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
import org.objectweb.asm.FieldVisitor;
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

	private String currentClassName;
	private int methodId = 0;
	private int fieldId = 0;

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

	private boolean shouldVisitClass(String name) {
		if(name.contains("org/omg"))
			return false;
		if(name.contains("javax/") && !name.contains("javax/swing/JComponent")
			&& !name.contains("javax/swing/text"))
			return false;
		if(name.contains("com/sun"))
			return false;
		if(name.contains("sun/java2d"))
			return false;
		if(name.contains("sun/awt"))
			return false;
		
		return true;
	}
	
	private void fromClass(InputStream in, final IndexPartition partition, final Set<IStrategoAppl> entries)
		throws IOException {
		final ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5) {
			public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
				currentClassName = null;
				if(!shouldVisitClass(name))
					return;
				currentClassName = name;
				methodId = 0;
				fieldId = 0;
				System.out.println(name);
				for(IStrategoAppl entryTerm : factory.clazz(name, superName, interfaces, access)) {
					entries.add(entryTerm);
				}
			}

			public void visitInnerClass(String name, String outerName, String innerName, int access) {
				if(outerName == null || innerName == null)
					return;
				if(!shouldVisitClass(outerName))
					return;
				
				for(IStrategoAppl entryTerm : factory.innerClazz(outerName, innerName, access)) {
					entries.add(entryTerm);
				}
			}

			public MethodVisitor
				visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				if(currentClassName == null)
					return null;
				if((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE)
					return null;
				for(IStrategoAppl entryTerm : factory.method(name, desc, currentClassName, methodId++)) {
					entries.add(entryTerm);
				}
				return null;
			}

			public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
				if(currentClassName == null)
					return null;
				if((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE)
					return null;
				for(IStrategoAppl entryTerm : factory.field(name, desc, currentClassName, fieldId++)) {
					entries.add(entryTerm);
				}
				return null;
			}
		};

		final ClassReader reader = new ClassReader(in);
		reader.accept(visitor, 0);
	}
}
