module languages/Java-1.5/types/types/promotion

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/widening
	
type rules

	// TODO: check correctness
	t1 <promote-bin: t2
	where t1 <widens-prim: t2

type functions

	// TODO: check correctness
	promote-un :
		t -> Int()
		where (t == Byte() or t == Short() or t == Char())
	    and t <widens-prim: Int()
