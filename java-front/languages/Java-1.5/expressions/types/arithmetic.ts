module languages/Java-1.5/expressions/types/arithmetic

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/primitives
	languages/Java-1.5/types/types/promotion
	
type rules

	Plus(x, y)  // TODO: can also be string concatenation, needs a special case
+ Minus(x, y)
+ Mul(x, y)
+ Div(x, y)
+ Mod(x, y) : prom-ty
	where x : x-ty
	  and y : y-ty
	  and <promote-bin> (x-ty, y-ty) => prom-ty
	  and x-ty <is: Numerical() else error "Expected numerical" on x
	  and y-ty <is: Numerical() else error "Expected numerical" on y
 
  LeftShift(x, y)
+ RightShift(x, y)
+ URightShift(x, y) : x-prom-ty
  where x : x-ty
    and y : y-ty
    and <promote-un> x-ty => x-prom-ty
    and <promote-un> y-ty => y-prom-ty
    and x-prom-ty <is: Integral() else error "Expected numerical" on x
    and x-prom-ty <is: Integral() else error "Expected integral" on y

  Plus(e)
+ Minus(e) : ty
  where e : ty
    and <promote-un> ty => prom-ty
    and prom-ty <is: Numerical() else error "Expected numerical" on e
    
  // TODO: check correctness
  PreIncr(e)
+ PostIncr(e)
+ PreDecr(e)
+ PostDecr(e) : ty
  where e : prom-ty
    and <promote-bin> (ty, Int()) => prom-ty else error "Expected numerical" on e
