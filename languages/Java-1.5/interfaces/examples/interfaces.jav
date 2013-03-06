@Anno(1)
public interface IA {
  public static int a = 1;
  int b;
}

@Anno
public interface IB extends IA, IA {
  public abstract void method(int a);
  void deprecated(int s, int h) [] throws IB;
}

@Anno(a = 1, b = 2)
public @interface Anno {
  IA test();
  public Anno test2() default 1;
  
  int a;
}
