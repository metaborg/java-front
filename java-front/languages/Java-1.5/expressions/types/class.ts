module languages/Java-1.5/expressions/types/class

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/equality
	
type rules

	NewInstance(_, t, _, _): t
	
	InstanceOf(e, t): Boolean()
	where e : e-ty
	  and t : t-ty
	  and <ref: e-ty else "Expected reference" on e
	  and <ref: t-ty else "Expected reference" on t

	QThis(ty) : ty
	
	Field(_, f) : ty
	where definition of f : ty
