module languages/Java-1.5/expressions/types/bitwise-logical

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/equality
	languages/Java-1.5/types/types/promotion
	
type rules

  And(x, y) + ExcOr(x, y) + Or(x, y) : ty
  where x : x-ty
    and y : y-ty
    and (
    	(
    	      x-ty <is: Numerical()
    	  and y-ty <is: Numerical()
    	  and x-ty <promote-bin: y-ty => ty
  	  )
      or
      (
    	      x-ty == Boolean()
    	  and y-ty == Boolean()
    	  and Boolean() => ty
      )
    ) else "Expected numbers or booleans"

  LazyAnd(x, y) + LazyOr(x, y) : Boolean()
  where x : x-ty
    and y : y-ty
    and x-ty == Boolean()
    and y-ty == Boolean() else "Expected booleans"
    
  Not(e) : ty
  where e : ty
    and ty == Boolean() else "Expected boolean"

	Complement(e) : prom-ty
	where e : ty
	  and ty <is: Integral() else "Expected integral"
	  and ty <promote-un: ty => prom-ty
