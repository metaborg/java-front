// 15.12.2 the type determined ... for member methods ... uses the name of the method and the argument expressions to locate methods that are both *accessible* and *applicable*
class A {}
class B extends A {}
class C1 {
    void m(A a) {} // removing this method results in private access error below
}
class C2 extends C1 {
    private void m(B b) {}
}
class Test {
    void test(C2 c2, B b) {
        c2.m(b);
    }
}
