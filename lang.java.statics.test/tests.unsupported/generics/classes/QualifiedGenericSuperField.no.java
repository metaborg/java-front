class A<X> {
    X f;
}
class I {}
class B extends A<I> {
    J m() {
	return super.f;
    }
}
