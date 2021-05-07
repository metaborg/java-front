class A {
    public int f;
}
class B extends A {
    public boolean f;
}
class Test {
    void m(B b) {
        int x = b.f;
    }
}
