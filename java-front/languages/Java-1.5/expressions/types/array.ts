module languages/Java-1.5/expressions/types/array

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/equality
	languages/Java-1.5/types/types/promotion
	
type rules // Array creation

  NewArray(t, dim1*, dim2*): array-ty
  where t : ty
    and ([dim1*, dim2*], ty) <create-array-type: array-ty

  NewArrayInit(t, dim1*, _): array-ty
  where t : ty
    and (dim1*, ty) <create-array-type: array-ty

  ([Dim(_)|s], ty) <create-array-type: ArrayType(t)
  where s : t
  
  ([], ty) <create-array-type: ArrayType(ty)
  
  Dim(e) :-
  where e : ty
    and ty <promote-un: ty => prom-ty
    and prom-ty == Integer() else "Expected integer" on e

type rules // Array access

	ArrayAccess(e, i) : inner-ty
	where e : e-ty
	  and i : i-ty
	  and e-ty : ArrayType(t) else "Expected array" on e
	  and i-ty <promote-un: i-ty => prom-ty
	  and i-ty == Integer() else "Expected integer" on i
