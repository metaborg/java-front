class A {
  
}

class B extends A {
  
}

class C<T> implements A, B {
  void bla(C<T> t, C<Concrete> x) {
  	a(t);
  	a(x);
  }
  
  void a(C<T> s) {
  	
  }
}

