package structuraltyping;

public class Test {
	public static interface I1 {
		public abstract void m();
	}

	public static interface I2 {
		public void m();
	}
	
	public static class Impl1 implements I1, I2 {
		public void m() {
			System.out.println("Impl1.m");
		}
	}

	public static class Impl2 implements I2, I1 {
		public void m() {
			System.out.println("Impl2.m");
		}
	}

	public static void main(String[] args) {
		I1 i1 = new Impl1();
		I2 i2 = new Impl2();
		
		i1.m();
		i2.m();
		
		I2 i2a = (I2)i1;
		i2a.m();
		
		I1 i1a = (I1)i2;
		i1a.m();
	}
}