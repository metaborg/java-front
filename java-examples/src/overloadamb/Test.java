package overloadamb;

public class Test {

	public static class A {
		public void m(B b, A a) {}		
	}
	
	public static class B extends A {
		public void m(A a, B b) {}
	}
	
	public static void main(String[] args) {
		A a = new A();
		B b = new B();
		
		
		a.m(a, a);
		a.m(a, b);
		a.m(b, a);
		a.m(a, a);
		
		b.m(a, a);
		b.m(a, b);
		b.m(b, a);
		b.m(a, a);
	}

}
