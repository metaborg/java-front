module languages/Java-1.5/expressions/types/bitwise-logical

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/equality
	languages/Java-1.5/types/types/promotion
	
type rules

  And(x, y)
+ ExcOr(x, y)
+ Or(x, y) : ty
  where x : x-ty
    and y : y-ty
    and (
    	(
    	      <num: x-ty
    	  and <num: y-ty
    	  and x-ty <prom: y-ty => ty
  	  )
      or
      (
    	      <bool: x-ty
    	  and <bool: y-ty
    	  and Boolean() => ty
      )
    ) else "Expected numbers or booleans"

  LazyAnd(x, y)
+ LazyOr(x, y) : Boolean()
  where x : x-ty
    and y : y-ty
    and <bool: x
    and <bool: y else "Expected booleans"
    
  Not(e) : ty
  where e : ty
    and <bool: ty else "Expected boolean"

	Complement(e) : prom-ty
	where e : ty
	  and <integral: ty else "Expected integral"
	  and <prom: ty => prom-ty
