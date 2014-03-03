module languages/Java-1.5/expressions/types/boolean

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/equality
	
type rules
	
  Lt(x, y) + Gt(x, y) + LtEq(x, y) + GtEq(x, y) : Boolean()
  where x : x-ty
    and y : y-ty
    and x-ty <is: Numerical() else error "Expected numerical"
    and y-ty <is: Numerical() else error "Expected numerical"
  
  // TODO: check for null type  
  Eq(x, y) + NotEq(x, y) : Boolean()
  where x : x-ty
    and y : y-ty
    and (
    	(x-ty <is: Numerical() and y-ty <is: Numerical())
    	or
    	(x-ty == Boolean() and y-ty == Boolean())
    	or
    	(x-ty <is: Reference() and y-ty <is: Reference())
    ) else error "Expected numericals, booleans or references"
