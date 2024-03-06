test;

[hello]
class A {
    static int f = 1;
    static int g = 2;
}

class B {
    static int f = 3;
    static int g = A.f;
}
