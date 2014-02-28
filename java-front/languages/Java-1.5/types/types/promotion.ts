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

	t <promote-un: t
	where (t == Byte() or t == Short() or t == Char())
	  and t <widens-prim: Int()
  