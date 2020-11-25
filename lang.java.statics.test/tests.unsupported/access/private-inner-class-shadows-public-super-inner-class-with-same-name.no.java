class A {
    public class C {};
}
class B extends A {
    private class C {};
}
class Test {
    B.C c;
}
