class A {}
class C1 {
    void m(A a) {}
}
class C2 extends C1 {
    private void m(A a) {}
}
class Test {
    void test(C2 c2, A a) {
        c2.m(a);
    }
}
