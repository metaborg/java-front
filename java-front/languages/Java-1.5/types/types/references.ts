module languages/Java-1.5/types/types/references

imports
	
	include/Java
	lib/types/-

type rules

	ClassOrInterfaceType(c, tp*) : RefType(c, tp*)
	ClassType(c, tp*)            : RefType(c, tp*)
	InterfaceType(c, tp*)        : RefType(c, tp*)
	
	ArrayType(t) : ArrayType(t)

	Null() : Null()

type rules
	
	<reft: RefType(_, _)
	<array: ArrayType(_)
	<null: Null()
	
	<ref: t
	where <reft:  t
	   or <array: t
	   or <null:  t
