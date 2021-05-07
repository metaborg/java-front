class A {}
class C1 {
    private void m(A a) {}
}
class Test {
    void test(C1 c1, A a) {
        c1.m(a);
    }
}
