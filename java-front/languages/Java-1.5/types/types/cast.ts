module languages/Java-1.5/types/types/cast

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/widening
	languages/Java-1.5/types/types/narrowing
	
type rules
	
	e-ty <cast: t-ty
	where e-ty <wp: t-ty
	   or e-ty <np: t-ty
	   or e-ty <wr: t-ty
	   or e-ty <nr: t-ty
