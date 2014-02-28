module languages/Java-1.5/types/types/assignment

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/widening
	languages/Java-1.5/types/types/narrowing
	
type rules

	// TODO: apply narrowing primitive conversion to constant expression if spec allows it (pg. 66)
	e-ty <assign-conv: v-ty
	where e-ty <widens: v-ty
	
	// pg 397. Compount assignment E1 op= E2 is equivalent to E1 = (T)((E1) op (E2)) where T is type of E1.
	// TODO: apply narrowing primitive conversion to constant expression if spec allows it (pg. 66)
	e-ty <comp-assign-conv: v-ty
	where e-ty <widens-prim: v-ty
	   or e-ty <narrows-prim: v-ty
