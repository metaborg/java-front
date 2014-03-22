package org.metaborg.java.conformance;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class ResultLogger {
	private final String directory;
	private final String file;
	private final boolean throwOnFailure;

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
		String message = kind + ": " + prettify(jdtObj) + " - " + prettify(spxObj);
		out(message);
	}

	public void failure(String kind, Object jdtObj, Object spxObj) {
		String message = kind + ": " + prettify(jdtObj) + " - " + prettify(spxObj);
		err(message);

		if(throwOnFailure)
			throw new RuntimeException(message.toString());
	}


	public void innerResult(boolean success, String kind, Object jdtObj, Object spxObj) {
		if(success)
			innerSuccess(kind, jdtObj, spxObj);
		else
			innerFailure(kind, jdtObj, spxObj);
	}

	public void innerSuccess(String kind, Object jdtObj, Object spxObj) {
		String message = "I " + kind + ": " + prettify(jdtObj) + " - " + prettify(spxObj);
		out(message);
	}

	public void innerFailure(String kind, Object jdtObj, Object spxObj) {
		String message = "I " + kind + ": " + prettify(jdtObj) + " - " + prettify(spxObj);
		err(message);

		if(throwOnFailure)
			throw new RuntimeException(message.toString());
	}


	public Object prettify(Object obj) {
		if(obj instanceof ITypeBinding) {
			return ((ITypeBinding) obj).getQualifiedName();
		} else if(obj instanceof IBinding) {
			return ((IBinding) obj).getName();
		}

		return obj;
	}


	public void debug(Object obj) {
		out(obj);
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
