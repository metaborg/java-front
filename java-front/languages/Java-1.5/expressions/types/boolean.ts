module languages/Java-1.5/expressions/types/boolean

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/equality
	
type rules
	
  Lt(x, y)
+ Gt(x, y)
+ LtEq(x, y)
+ GtEq(x, y) : Boolean()
  where x : x-ty
    and y : y-ty
    and <num: x-ty else "Expected numerical"
    and <num: y-ty else "Expected numerical"
  
  // TODO: check for null type  
  Eq(x, y)
+ NotEq(x, y) : Boolean()
  where x : x-ty
    and y : y-ty
    and (
    	(<num: x-ty and <num: y-ty)
    	or
    	(<bool: x-ty and <bool: y-ty)
    	or
    	(<ref: x-ty and <ref: y-ty)
    ) else "Expected numericals, booleans or references"
