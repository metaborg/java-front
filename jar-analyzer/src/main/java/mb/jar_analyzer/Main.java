package mb.jar_analyzer;

import java.util.List;

import com.google.common.collect.ImmutableList;

import mb.jar_analyzer.deps.DepsCommand;
import mb.jar_analyzer.stxlib.StxLibCommand;

public final class Main {

    public static void main(String[] args) {
        final List<String> argList = ImmutableList.copyOf(args);
        if(argList.size() < 1) {
            fail("Missing command.");
        }
        String cmd = argList.get(0);
        List<String> cmdArgList = argList.subList(1, argList.size());
        switch(cmd) {
            case "deps":
                new DepsCommand(cmdArgList).run();
                break;
            case "stxlib":
                new StxLibCommand(cmdArgList).run();
                break;
            default:
                fail("Unknown command: " + cmd);
        }
    }

    static void fail(String message) {
        System.out.println("Error: " + message);
        System.out.println("Usage: COMMAND (deps|stxlib) JARS");
        System.exit(1);
    }

}