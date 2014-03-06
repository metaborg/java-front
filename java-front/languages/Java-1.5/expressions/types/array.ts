module languages/Java-1.5/expressions/types/array

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/primitives
	languages/Java-1.5/types/types/promotion

type functions

	create-array-type :
		([Dim(_)|ds], ty) -> ArrayType(inner-ty)
  	where <create-array-type> (ds, ty) => inner-ty

	create-array-type :  
  	([], ty) -> ArrayType(ty)
	
type rules // Array creation

  NewArray(t, dim1*, dim2*) : array-ty
  where t : ty
    and <create-array-type> ([dim1*, dim2*], ty) => array-ty // TODO: dim1* and dim2* will not be concatenated

  NewArrayInit(t, dim1*, _) : array-ty
  where t : ty
    and <create-array-type> (dim1*, ty) => array-ty
  
  Dim(e) :-
  where e : ty
    and <promote-un> ty => prom-ty
    and prom-ty == Int() else error "Expected integer" on e

type rules // Array access

	ArrayAccess(e, i) : inner-ty
	where e : e-ty
	  and i : i-ty
	  and e-ty : ArrayType(inner-ty) else error "Expected array" on e
	  and <promote-un> i-ty => prom-ty
	  and prom-ty == Int() else error "Expected integer" on i
