module languages/Java-1.5/expressions/types/array

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/expressions/trans/desugar
	
	languages/Java-1.5/types/types/primitives
	languages/Java-1.5/types/types/promotion

type functions

	create-array-type :
		([_, Dim(_)|ds], ty) -> ArrayType(inner-ty)
  	where <create-array-type> (ds, ty) => inner-ty

	create-array-type :  
  	([_], ty) -> ArrayType(ty)
	
type rules // Array creation

  NewArray(t, dim*) : array-ty
  where t : ty
    and <create-array-type> (dim*, ty) => array-ty

  NewArrayInit(t, dim*, _) : array-ty
  where t : ty
    and <create-array-type> (dim*, ty) => array-ty
  
  Dim(e) :-
  where e : ty
    and <promote-un> ty => prom-ty
    and prom-ty == Int() else error "Expected integer" on e

type rules // Array access

	ArrayAccess(e, i) : elem-ty
	where e : ArrayType(elem-ty) else error "Expected array" on e
	  and i : i-ty
	  and <promote-un> i-ty => prom-ty
	  and prom-ty == Int() else error "Expected integer" on i
