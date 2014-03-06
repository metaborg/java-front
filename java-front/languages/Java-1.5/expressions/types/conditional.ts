module languages/Java-1.5/expressions/types/conditional

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/primitves
	languages/Java-1.5/types/types/promotion

signatures

	BSC : // Byte, Short or Char.
	
type rules
	
   Byte()  <is: BSC()
   Short() <is: BSC()
   Char()  <is: BSC()
	
	c@Cond(c, x, y) : ty
	where c : c-ty
	  and c-ty == Boolean() else error "Expected boolean" on c
	  and x : x-ty
	  and y : y-ty
	  and (
		  (
		   	    x-ty == y-ty
		   	and y-ty => ty
		  )
		  or
		  (
		        x-ty <is: Numerical()
		    and y-ty <is: Numerical()
		    and (
				    	(((x-ty == Byte() and y-ty == Short()) or (x-ty == Short() and y-ty == Byte())) and Short() => ty)
				   or ((x-ty <is: BSC() and y-ty == Int()) and y <representable: x-ty and x-ty => ty) // TODO: only if y-ty is a constant, TODO: value representability.
				   or ((x-ty == Int() and y-ty <is: BSC()) and x <representable: y-ty and y-ty => ty) // TODO: only if x-ty is a constant, TODO: value representability.
				   or <promote-bin> (x-ty, y-ty) => ty
		    )
		  )
		  or ((x-ty <is: Reference() and y-ty == Null()) and x-ty => ty)
		  or ((x-ty == Null() and y-ty <is: Reference()) and y-ty => ty)
		  or (x-ty <is: Reference() and y-ty <is: Reference() and x-ty <assign-conv: y-ty and y-ty => ty)
	  ) else error "Type mismatch between expressions" on c
