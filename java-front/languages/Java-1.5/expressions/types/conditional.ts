module languages/Java-1.5/expressions/types/conditional

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/equality
	languages/Java-1.5/types/types/promotion
	
type rules
	
	Cond(x, o1, o2) : ty
	where x : x-ty
	  and <bool: x else "Expected boolean" on x
	  and o1 : o1-ty
	  and o2 : o2-ty
	  and (
	  	(
	  		    o1-ty == o2-ty
	  		and o1-ty => ty
	  	)
	  	or
	  	(
	  		    <num: o1-ty
	  		and <num: o2-ty
	  		and o1-ty <prom: o2-ty => ty  // TODO: spec also mentions other weird promotion-like conversions.
	  	)
  	)
