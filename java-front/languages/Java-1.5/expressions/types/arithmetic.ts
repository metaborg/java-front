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
	  and x-ty <wp: y-ty => ty
	  and <num: x-ty else "Expected numerical" on x
	  and <num: y-ty else "Expected numerical" on y
	  
  LeftShift(x, y)
+ RightShift(x, y)
+ URightShift(x, y) : ty
  where x : x-ty
    and y : y-ty
    and <prom: y => ty
    and <num: x-ty else "Expected numerical" on x
    and <integral: ty else "Expected integral" on y

  Plus(e)
+ Minus(e) : ty
  where e : ty
    and <prom: ty => prom-ty
    and <num: prom-ty else "Expected numerical" on e
    
  PreIncr(e)
+ PostIncr(e)
+ PreDecr(e)
+ PostDecr(e) : ty
  where e : ty
    and Int() <prom: ty => prom-ty
    and <num: prom-ty else "Expected numerical" on e
