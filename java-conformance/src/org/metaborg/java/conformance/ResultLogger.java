package org.metaborg.java.conformance;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Statement;

public class ResultLogger {
	private final String directory;
	private final String file;
	private final boolean throwOnFailure;

	static class Result {
		public String directory;
		public String file;
		public String type;
		public String kind;
		public String jdtStr;
		public String spxStr;

		public Result(String directory, String file, String type, String kind, String jdtStr, String spxStr) {
			super();
			this.directory = directory;
			this.file = file;
			this.type = type;
			this.kind = kind;
			this.jdtStr = jdtStr;
			this.spxStr = spxStr;
		}
	}

	private static final Collection<Result> results = new LinkedList<Result>();

	public int numInnerChecks = 0;
	public int numInnerSuccess = 0;
	public int numInnerFailure = 0;

	public int numChecks = 0;
	public int numSuccess = 0;
	public int numFailure = 0;

	public ResultLogger(String directory, String file, boolean throwOnFailure) {
		this.directory = directory;
		this.file = file;
		this.throwOnFailure = throwOnFailure;
	}

	public void result(boolean success, String kind, Object jdtObj, Object spxObj) {
		if(success)
			success(kind, jdtObj, spxObj);
		else
			failure(kind, jdtObj, spxObj);
	}

	public void success(String kind, Object jdtObj, Object spxObj) {
		results.add(new Result(directory, file, "PASS", kind, stringify(jdtObj), stringify(spxObj)));
		++numChecks;
		++numSuccess;

		out("PASS " + kind + ": " + stringify(jdtObj) + " - " + stringify(spxObj));
	}

	public void failure(String kind, Object jdtObj, Object spxObj) {
		results.add(new Result(directory, file, "FAIL", kind, stringify(jdtObj), stringify(spxObj)));
		++numChecks;
		++numFailure;

		err("FAIL " + kind + ": " + stringify(jdtObj) + " - " + stringify(spxObj));

		if(throwOnFailure)
			throw new RuntimeException();
	}


	public void innerResult(boolean success, String kind, Object jdtObj, Object spxObj) {
		if(success)
			innerSuccess(kind, jdtObj, spxObj);
		else
			innerFailure(kind, jdtObj, spxObj);
	}

	public void innerSuccess(String kind, Object jdtObj, Object spxObj) {
		results.add(new Result(directory, file, "INNER PASS", kind, stringify(jdtObj), stringify(spxObj)));

		++numInnerChecks;
		++numInnerSuccess;
	}

	public void innerFailure(String kind, Object jdtObj, Object spxObj) {
		results.add(new Result(directory, file, "INNER FAIL", kind, stringify(jdtObj), stringify(spxObj)));

		++numInnerChecks;
		++numInnerFailure;
	}


	public static void write(String file) throws IOException {
		final PrintWriter out = new PrintWriter(file);
		try {
			for(Result result : results) {
				out.println(result.directory + ";" + result.file + ";" + result.type + ";" + result.kind + ";"
					+ result.jdtStr + ";" + result.spxStr);
			}
		} finally {
			out.close();
		}
	}


	private Object prettify(Object obj) {
		if(obj instanceof ITypeBinding) {
			return ((ITypeBinding) obj).getQualifiedName();
		} else if(obj instanceof IBinding) {
			return ((IBinding) obj).getName();
		} else if(obj instanceof Statement) {
			return truncate(strip(((Statement) obj).toString()), 50);
		} else if(obj instanceof Expression) {
			return truncate(strip(((Expression) obj).toString()), 50);
		}

		return obj;
	}

	private String stringify(Object obj) {
		final Object newObj = prettify(obj);
		if(newObj == null)
			return "null";
		else
			return newObj.toString();
	}

	private String strip(String str) {
		return str.replace("\n", "").replace("\r", "");
	}

	private String truncate(String str, int length) {
		if(str.length() > length) {
			return str.substring(0, length) + "...";
		}
		return str;
	}


	private void out(Object obj) {
		System.err.flush();
		System.out.flush();
		System.out.println(obj);
	}

	private void err(Object obj) {
		System.err.flush();
		System.out.flush();
		System.err.println(obj);
	}
}
