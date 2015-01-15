module languages/Java-1.5/classes/types/method

imports

	include/Java
	lib/runtime-libraries/org.spoofax.meta.runtime.libraries/task/-
	lib/runtime-libraries/org.spoofax.meta.runtime.libraries/types/-
	lib/runtime-libraries/org.spoofax.meta.runtime.libraries/properties/-
	lib/runtime-libraries/org.spoofax.meta.runtime.libraries/relations/-
	
	varids
	
type rules

	// TODO: this will call type-of on the list p*, for which there is no rule. it should map over p* instead.
	MethodParams(p*) : ty*
	where p* : ty*
	
	Param(_, t, _, _) : t
