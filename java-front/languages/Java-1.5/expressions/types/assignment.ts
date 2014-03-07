module languages/Java-1.5/expressions/types/assignment

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/assignment
	languages/Java-1.5/types/types/cast
	
type rules

	t@Assign(v, e) + t@LocalVarDec(_, v, VarDec(_, e)): v-ty
	where v : v-ty
	  and e : e-ty
	  and e-ty <assign-conv: v-ty
	    else error $[Cannot assign expression of type [e-ty] to variable of type [v-ty]] on t
	    
  t@AssignPlus(v, e) : ty
  where v : v-ty
    and e : e-ty
    and ( 
	    (
	    	    v-ty <is: String()
	    	and v-ty => ty
	    ) 
	    or 
	    (
		        v-ty <is: Primitive() else error "Expected primitive type" on v
		    and e-ty <is: Primitive() else error "Expected primitive type" on e
		    and e-ty <assign-conv-comp: v-ty
		      else error $[Cannot assign expression of type [e-ty] to variable of type [v-ty]] on t
	    )
    )

  t@AssignMinus(v, e)
+ t@AssignMul(v, e)
+ t@AssignDiv(v, e)
+ t@AssignMod(v, e)
+ t@AssignLeftShift(v, e)
+ t@AssignRightShift(v, e)
+ t@AssignURightShift(v, e)
+ t@AssignAnd(v, e)
+ t@AssignExcOr(v, e)
+ t@AssignOr(v, e) : v-ty
  where v : v-ty
    and e : e-ty
    and v-ty <is: Primitive() else error "Expected primitive type" on v
    and e-ty <is: Primitive() else error "Expected primitive type" on e
    and e-ty <assign-conv-comp: v-ty
      else error $[Cannot assign expression of type [e-ty] to variable of type [v-ty]] on t
 