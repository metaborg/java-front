package org.metaborg.java.conformance;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;

public class GlobalResultLogger {
	static final class Result {
		public String directory;
		public String file;
		public String type;
		public int numChecks;
		public int numSuccess;
		public int numFail;

		public Result(String directory, String file, String type, int numChecks, int numSuccess, int numFail) {
			this.directory = directory;
			this.file = file;
			this.type = type;
			this.numChecks = numChecks;
			this.numSuccess = numSuccess;
			this.numFail = numFail;
		}
	}

	private final Collection<Result> results = new LinkedList<Result>();

	public void result(String directory, String file, int numChecks, int numSuccess, int numFails) {
		if(numFails != 0) {
			debug("FAIL: " + file);
			results.add(new Result(directory, file, "FAIL", numChecks, numSuccess, numFails));
		} else {
			debug("PASS: " + file);
			results.add(new Result(directory, file, "PASS", numChecks, numSuccess, numFails));
		}
	}

	public void skip(String directory, String file) {
		debug("SKIP: " + file);
		results.add(new Result(directory, file, "SKIP", 0, 0, 0));
	}

	public void write(String file) throws IOException {
		final PrintWriter out = new PrintWriter(file);
		try {
			for(Result result : results) {
				out.println(result.directory + ";" + result.file + ";" + result.type + ";" + result.numChecks + ";"
					+ result.numSuccess + ";" + result.numFail);
			}
		} finally {
			out.close();
		}
	}

	public void debug(Object obj) {
		System.err.flush();
		System.out.flush();
		System.out.println(obj);
	}
}
