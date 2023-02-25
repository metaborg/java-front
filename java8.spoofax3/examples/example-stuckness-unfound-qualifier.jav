test;

[AS]
public static class AS {
    public int x = 4;
}

[BS]
public static class BS {
    public int x = 5;
}

[A]
public static class A extends AS {
    public int x = 1;

    public class B extends BS {
        public int x = 2;

        public int getX(int x) {
            return A.B.x;
        }
    }
}
