module languages/Java-1.5/names/types/names

imports

  signatures/-

type rules

  AmbName(n)
+ AmbName(_, n)
+ PackageOrTypeName(n)
+ PackageOrTypeName(_, n)
+ TypeName(n)
+ TypeName(_, n)
+ ExprName(n)
+ ExprName(_, n)
+ MethodName(n)
+ MethodName(_, n) : ty
  where definition of n : ty
