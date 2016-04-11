package arraywidening;

public class Test {

	public static class A {}
	public static class B extends A {}
	
	public static void main(String[] args) {
		A[] as = new A[0];
		B[] bs = new B[0];
		
		as = bs;
	}

}
