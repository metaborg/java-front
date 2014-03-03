module languages/Java-1.5/types/types/assignment

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/widening
	languages/Java-1.5/types/types/narrowing
	
type rules

	e-ty <assign-conv: v-ty
	where e-ty == v-ty
	   or e-ty <widens-prim: v-ty
	   or e-ty <widens-ref: v-ty
	   or (e-ty <box: v-ty and e-ty <widens-ref: v-ty)
	   or (e-ty <unbox: v-ty and e-ty <widens-prim: v-ty)
	/* TODO
	If, after the conversions listed above have been applied, the resulting type is a raw type (4.8), unchecked conversion (5.1.9) may then be applied. 
	It is a com- pile time error if the chain of conversions contains two parameterized types that are not not in the subtype relation.
	
	In addition, if the expression is a constant expression (15.28) of type byte, short, char or int :
	* A narrowing primitive conversion may be used if the type of the variable is byte, short, or char, and the value of the constant expression is represent- able in the type of the variable.
	* A narrowing primitive conversion followed by a boxing conversion may be used if the type of the variable is :
	  * Byte and the value of the constant expression is representable in the type byte.
	  * Short and the value of the constant expression is representable in the type short.
	  * Character and the value of the constant expression is representable in the type char.
	*/
	
	
	// pg 397. Compount assignment E1 op= E2 is equivalent to E1 = (T)((E1) op (E2)) where T is type of E1.
	// TODO: apply narrowing primitive conversion to constant expression if spec allows it (pg. 66)
	e-ty <comp-assign-conv: v-ty
	where e-ty <widens-prim: v-ty
	   or e-ty <narrows-prim: v-ty
