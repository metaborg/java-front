module languages/Java-1.5/expressions/types/class

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/references
	
type rules

	NewInstance(_, t, _, _) : ty
	where t : ty
	
	InstanceOf(e, t) : Boolean()
	where e : e-ty
	  and t : t-ty
	  and e-ty <is: Reference() else error "Expected reference" on e
	  and t-ty <is: Reference() else error "Expected reference" on t

	QThis(ty) : ty
	
	Field(_, f) : ty
	where definition of f : ty
