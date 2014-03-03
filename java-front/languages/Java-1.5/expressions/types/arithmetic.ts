module languages/Java-1.5/expressions/types/arithmetic

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/promotion
	languages/Java-1.5/types/types/equality
	
type rules

	Plus(x, y)  // TODO: can also be string concatenation, needs a special case
+ Minus(x, y)
+ Mul(x, y)
+ Div(x, y)
+ Mod(x, y) : ty
	where x : x-ty
	  and y : y-ty
	  and x-ty <widens-prim: y-ty => ty
	  and x-ty <is: Numerical() else error "Expected numerical" on x
	  and y-ty <is: Numerical() else error "Expected numerical" on y
	  
  LeftShift(x, y)
+ RightShift(x, y)
+ URightShift(x, y) : ty
  where x : x-ty
    and y : y-ty
    and y <promote-un: y => ty
    and x-ty <is: Numerical() else error "Expected numerical" on x
    and y-ty <is: Integral() else error "Expected integral" on y

  Plus(e)
+ Minus(e) : ty
  where e : ty
    and ty <promote-un: ty => prom-ty
    and prom-ty <is: Numerical() else error "Expected numerical" on e
    
  PreIncr(e)
+ PostIncr(e)
+ PreDecr(e)
+ PostDecr(e) : ty
  where e : ty
    and Int() <promote-bin: ty => prom-ty
    and prom-ty <is: Numerical() else error "Expected numerical" on e
