class A<X> {
    X f;
}
class I {}
class J {}
class B extends A<I> {
    J m() {
	return this.f;
    }
}
