public class Type<Param1 extends Type, Param2> {
  int a;
  Type<Type, Type> b;
  Type<?, ?extends Type, ?super Type> c;
}
