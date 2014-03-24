module languages/Java-1.5/expressions/types/invoke

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-

type rules
	
	Invoke(Method(m), _) : ty
	where definition of m : ty
	
	Invoke(Method(e, _, m), _) : ty
	where definition of m : ty
	
	Invoke(SuperMethod(_, m), _) : ty
	where definition of m : ty
	
	Invoke(QSuperMethod(t, _, m), _) : ty
	where definition of m : ty

	// TODO: this will call type-of on the list p*, for which there is no rule. it should map over p* instead.
	MethodArgs(a*) : ty*
	where a* : ty*
