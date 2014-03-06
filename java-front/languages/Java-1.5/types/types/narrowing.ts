module languages/Java-1.5/types/types/narrowing

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/widening

type rules // Type narrowing relation for reference and primitive types

	a-ty <narrows: e-ty
	where a-ty <narrows-prim: e-ty
		 or a-ty <narrows-ref: e-ty
		 
type rules // Narrowing reference-ty types

	a-ty <narrows-ref: e-ty
	where e-ty <widens-ref: a-ty // Apply widening on references the-ty other way around.
		 or a-ty <is: Object()
		 // From any interface-ty type-ty I to any class type-ty C that is not final.
		 or(a-ty <is: Interface() and e-ty <is: Class() and not(e-ty <is: Final()))
		 // From any class type-ty C to any interface-ty type-ty I, provided that C is not final and does not implement I.
		 or(a-ty <is: Class() and e-ty <is: Interface() and not(a-ty <is: Final()) and not(a-ty <implements: e-ty)) 
		 // TODO WTF: from any interface-ty type-ty J to any inteface-ty type-ty K, provided that J is not a-ty subinterface-ty of K and there-ty is no method name-ty m such that J and K both contain a-ty method named m with the-ty same-ty signature-ty but different return types.
 
	ArrayType(a-ty) <narrows-array: ArrayType(e-ty)
	where a-ty <narrows-ref: e-ty
	  
type rules // Narrowing primitive types

	Double() <narrows-prim: Byte()
	Double() <narrows-prim: Short()
	Double() <narrows-prim: Char()
	Double() <narrows-prim: Int()
	Double() <narrows-prim: Long()
	Double() <narrows-prim: Float()
	
	Float()  <narrows-prim: Byte()
	Float()  <narrows-prim: Short()
	Float()  <narrows-prim: Char()
	Float()  <narrows-prim: Int()
	Float()  <narrows-prim: Long()
	
	Long()   <narrows-prim: Byte()
	Long()   <narrows-prim: Short()
	Long()   <narrows-prim: Char()
	Long()   <narrows-prim: Int()
	
	Int()    <narrows-prim: Byte()
	Int()    <narrows-prim: Short()
	Int()    <narrows-prim: Char()
	
	Short()  <narrows-prim: Byte()
	Short()  <narrows-prim: Char()
	
	Byte()   <narrows-prim: Char()
