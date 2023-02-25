test;

[A]
public class A {
    public int x = 1;

    public static class B {
        public static int x = 2;

        public int getX(int y) {
            int a = A.B.x;
            int b = p.B.x;
            return a + b;
        }
    }
}
[p] {
  [B]
  package p;
  public class B {
    public static int x;
  }
}
