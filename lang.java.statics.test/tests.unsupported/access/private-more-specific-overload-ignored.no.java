// 15.12.2 the type determined ... for member methods ... uses the name of the method and the argument expressions to locate methods that are both *accessible* and *applicable*
class A {}
class B extends A {}
class C {
    int m(A a) { return 1; }
    private boolean m(B b) { return true; }
}
class Test {
    void test(C c, B b) {
        boolean x = c.m(b);
    }
}
