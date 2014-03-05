module languages/Java-1.5/expressions/types/invoke

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-

type rules
	
	Invoke(Method(m), _) : ty
	where definition of m : ty
