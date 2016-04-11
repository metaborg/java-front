package privateoverride;

public class Test {
	
	public static class C0 {
		public void m(float i) {}
	}
	
	public static class C1 extends C0 {
		private void m(int i) {}
	}
	
	public static class C2 extends C1{
		public void m() {}
	}
	
	public static void main(String[] args) {
		C2 c2 = new C2();
		c2.m(1);
	}

}
