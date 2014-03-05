module languages/Java-1.5/expressions/types/conditional

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/primitves
	languages/Java-1.5/types/types/promotion
	
type rules
	
	Cond(c, o1, o2) : o2-ty
	where c : c-ty
	  and c-ty == Boolean() else error "Expected boolean" on c
	  and o1 : o1-ty
	  and o2 : o2-ty
	  and (
		  (
		   	    o1-ty == o2-ty
		  )
		  or
		  (
		   	    o1-ty <is: Numerical()
		    and o2-ty <is: Numerical()
		    and o1-ty <promote-bin: o2-ty
		  )
	  )
	  // TODO: spec also mentions other weird promotion-like conversions.
