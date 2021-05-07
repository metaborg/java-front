class A<X> {
    X f;
}
class I {}
class B extends A<I> {
    I m() {
	return f;
    }
}
