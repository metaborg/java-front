module languages/Java-1.5/types/types/cast

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/widening
	languages/Java-1.5/types/types/narrowing
	
type rules
	
	e-ty <cast: t-ty
	where e-ty == t-ty
	   or e-ty <widens-prim: t-ty
	   or e-ty <narrows-prim: t-ty
	   or e-ty <widens-ref: t-ty   // TODO: optionally followed by an unchecked conversion (5.1.9)
	   or e-ty <narrows-ref: t-ty  // TODO: optionally followed by an unchecked conversion (5.1.9)
     or e-ty <box: t-ty
     or e-ty <unbox: t-ty