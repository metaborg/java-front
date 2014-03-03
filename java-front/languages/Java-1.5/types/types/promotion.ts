module languages/Java-1.5/types/types/promotion

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/widening
	languages/Java-1.5/types/types/equality
	
type rules

	t1 <promote-bin: t2
	where t1 <widens-prim: t2
	// TODO: see binary numeric promotion on pg. 110
  
  ty <promote-un: ty'
  where (
  	      (ty == RefType("Byte", None()) or ty == RefType("Short", None()) or ty == RefType("Character", None()) or ty == RefType("Integer", None()))
      and ty <unbox: u-ty
      and (u-ty == Int() or u-ty <widens-prim: Int())
      and ty' => Int()
    )
    or
    (
    	    (ty == RefType("Long", None()) or ty == RefType("Float", None()) or ty == RefType("Double", None()))
    	and ty <unbox: ty'
    )
    or
    (
    	    (t == Byte() or t == Short() or t == Char())
    	and t <widens-prim: Int()
    	and ty' => Int()
    )
    or ty' => ty // TODO: should check if ty is numeric?
