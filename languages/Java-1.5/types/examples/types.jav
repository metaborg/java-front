public class Type<Param1 extends Type, Param2> {
  int a;
  Type<A.A, Type> b;
  Type<?, ?extends Type, ?super Type> c;
}
