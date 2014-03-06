module languages/Java-1.5/types/types/invocation

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/widening
	
type rules

	e-ty <invocation-conv: v-ty
	where e-ty == v-ty 
	   or e-ty <widens: v-ty
