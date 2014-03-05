module languages/Java-1.5/types/types/references

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-

signatures

	RefType : TypeName * TypeParams

	Reference :
	
type rules // Reference types

	ClassOrInterfaceType(c, tp*) : RefType(c, tp*)
	ClassType(c, tp*)            : RefType(c, tp*)
	InterfaceType(c, tp*)        : RefType(c, tp*)
	
	ArrayType(t) : ArrayType(t)

type rules // Reference type kinds

	RefType(_, _) <is: Reference()
	ArrayType(_)  <is: Reference()
	Null()        <is: Reference()
