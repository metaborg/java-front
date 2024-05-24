package mb.jar_analyzer.deps;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mb.jar_analyzer.jar.JarUtils;
import org.metaborg.util.collection.SetMultimap;
import org.metaborg.util.iterators.IteratorIterableOnce;

public class DepsCommand implements Runnable {

    private final List<File> files = new ArrayList<>();

    public DepsCommand(List<String> args) {
        Iterator<String> it = args.iterator();
        while(it.hasNext()) {
            String arg = it.next();
            switch(arg) {
                default:
                    files.add(new File(arg));
                case "--":
                    for(String f: new IteratorIterableOnce<>(it)) {
                        files.add(new File(f));
                    }
                    break;
            }
        }
    }

    @Override public void run() {
        final DependencyCollector depsCollector = new DependencyCollector(n -> !JarUtils.isJDKClass(n), false);
        try {
            JarUtils.processJarClasses(files, depsCollector);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        SetMultimap<String, String> dependencies = depsCollector.getDependencies();
        final java.util.Set<String> missing = new HashSet<>(depsCollector.getVisited());
        PrintStream out = System.out;
        out.println("digraph dependencies {");
        out.println("  rankdir=LR;");
        out.println("  node [shape=box];");
        final TopoSorter.TopoSortedComponents<String> sortResult =
                TopoSorter.toposort(dependencies.keySet(), dependencies::get, true);
        int subgraphCount = 0;
        for(Set<String> component : sortResult) {
            missing.removeAll(component);
            if(component.size() > 1) {
                out.println("  subgraph cluster_" + (subgraphCount++) + " {");
                out.println("    color=blue;");
            }
            for(String node : component) {
                out.println("    \"" + node + "\";");
            }
            if(component.size() > 1) {
                out.println("  }");
            }
        }
        dependencies.entries().forEach(entry -> {
            out.println("  \"" + entry.getKey() + "\" -> \"" + entry.getValue() + "\";");
        });
        out.println("  // " + missing.size() + " unreferenced types");
        for(String m : missing) {
            out.println("//\"" + m + "\";");
        }
        out.println("}");
    }

}