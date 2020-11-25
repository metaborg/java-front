class A {}
class B extends A {}
class C {
    int m(A a) { return 1; }
    private boolean m(B b) { return true; }
}
class Test {
    void test(C c, B b) {
        int x = c.m(b);
    }
}
