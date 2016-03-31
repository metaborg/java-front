module languages/Java-1.5/expressions/types/boolean

imports
	
	signatures/-
	runtime/task/-
	runtime/types/-
	runtime/properties/-
	runtime/relations/-
	
	languages/Java-1.5/types/types/primitives
	languages/Java-1.5/types/types/references
	
type rules
	
  Lt(x, y) + Gt(x, y) + LtEq(x, y) + GtEq(x, y) : Boolean()
  where x : x-ty
    and y : y-ty
    and <promote-bin> (x-ty, y-ty) => prom-ty
    and x-ty <is: Numerical() else error "Expected numerical" on x
    and y-ty <is: Numerical() else error "Expected numerical" on y
  
  t@Eq(x, y) + t@NotEq(x, y) : Boolean()
  where x : x-ty
    and y : y-ty
    and (
    	(x-ty <is: Numerical() and y-ty <is: Numerical() and <promote-bin> (x-ty, y-ty) => prom-ty)
    	or
    	(x-ty == Boolean() and y-ty == Boolean())
    	or
    	(x-ty <is: Reference() and y-ty <is: Reference())
    ) else error "Expected numericals, booleans or references" on t
