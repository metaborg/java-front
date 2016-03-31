module languages/Java-1.5/types/types/invocation

imports
	
	signatures/-
	runtime/task/-
	runtime/types/-
	runtime/properties/-
	runtime/relations/-
	
	languages/Java-1.5/types/types/widening
	
relations

	e-ty <invocation-conv: v-ty
	where e-ty == v-ty 
	   or e-ty <widens: v-ty
