module languages/Java-1.5/expressions/types/assignment

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/assignment
	languages/Java-1.5/types/types/cast
	
type rules

	Assign(v, e) + LocalVarDec(_, v, VarDec(_, e)): ty
	where v : v-ty
	  and e : e-ty
	  and e-ty <assign-conv: v-ty => ty
	    else $[Cannot assign expression of type [e-ty] to variable of type [v-ty]]
	  
  AssignPlus(v, e) // TODO: can also be string concatenation, needs a special case
+ AssignMinus(v, e)
+ AssignMul(v, e)
+ AssignDiv(v, e)
+ AssignMod(v, e)
+ AssignLeftShift(v, e)
+ AssignRightShift(v, e)
+ AssignURightShift(v, e)
+ AssignAnd(v, e)
+ AssignExcOr(v, e)
+ AssignOr(v, e) : v-ty
  where v : v-ty
    and e : e-ty
    and e-ty <conv-comp: v-ty
      else $[Cannot assign expression of type [e-ty] to variable of type [v-ty]]