module languages/Java-1.5/expressions/types/bitwise-logical

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/equality
	languages/Java-1.5/types/types/promotion
	
type rules

  t@And(x, y) + t@ExcOr(x, y) + t@Or(x, y) : ty
  where x : x-ty
    and y : y-ty
    and (
    	(
    	      x-ty <is: Numerical()
    	  and y-ty <is: Numerical()
    	  and x-ty <promote-bin: y-ty
    	  and y-ty => ty
  	  )
      or
      (
    	      x-ty == Boolean()
    	  and y-ty == Boolean()
    	  and Boolean() => ty
      )
    ) else error "Expected numbers or booleans" on t

  t@LazyAnd(x, y) + t@LazyOr(x, y) : Boolean()
  where x : x-ty
    and y : y-ty
    and x-ty == Boolean()
    and y-ty == Boolean() else "Expected booleans" on t
    
  Not(e) : ty
  where e : ty
    and ty == Boolean() else "Expected boolean" on e

	Complement(e) : prom-ty
	where e : ty
	  and ty <is: Integral() else "Expected integral" on e
	  and <promote-un> ty => prom-ty
