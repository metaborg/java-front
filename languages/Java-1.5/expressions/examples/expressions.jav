public class Expressions {
	public int state = 0;
	
  public int test() {
    int i = a();
    int j = this.a();
    int k = new Expressions().test();
    A l = Expressions.this.b(i, j, k);
    
    state = i + j + k;
    
    return new A().a;
    return l.a.c;
    return this.state;
    return Expressions.this.state;
    
    return new A.I(1);
    A a = new A();
    return a.new <T>I(1);
  }
  
  public int a() {
    return 1;
  }
  
  public A b(int i, int j, int k) {
    return new A(i + j, k);
  }
}