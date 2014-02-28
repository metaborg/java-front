module languages/Java-1.5/types/types/promotion

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/widening
	languages/Java-1.5/types/types/equality
	
type rules

	t1 <prom: t2
	where t1 <wp: t2

	<prom: t
	where (<byte: t or <short: t or <char: t)
	and t <wp: Int()
  