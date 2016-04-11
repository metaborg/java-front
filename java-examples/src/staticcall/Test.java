package staticcall;

public class Test {

	public static class A {
		public static void s(A a) {}
		public void s(B b) {}
	}
	public static class B extends A {}
	
	public static void s(A a) {}
	public void s(B b) {}
	
	public static void main(String[] args) {
		B b = new B();
		
		// Context = method m (static), class Test
		s(b);
		
		// Context = class A
		A.s(b);
	}
	
	
}
