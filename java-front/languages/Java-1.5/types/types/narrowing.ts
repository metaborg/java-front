module languages/Java-1.5/types/types/narrowing

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/widening

type rules // Type narrowing relation for reference and primitive types

	a <narrows: e
	where a <narrows-prim: e
		 or a <narrows-ref: e
		 
type rules // Narrowing reference types

	a <narrows-ref: e
	where e <widens-ref: a // Apply widening on references the other way around.
		 or a <is: Object()
		 // From any interface type I to any class type C that is not final.
		 or(a <is: Interface() and e <is: Class() and not(e <is: Final()))
		 // From any class type C to any interface type I, provided that C is not final and does not implement I.
		 or(a <is: Class() and e <is: Interface() and not(a <is: Final()) and not(a <implements: e)) 
		 // TODO WTF: from any interface type J to any inteface type K, provided that J is not a subinterface of K and there is no method name m such that J and K both contain a method named m with the same signature but different return types.
 
	ArrayType(a) <narrows-array: ArrayType(e)
	where a <narrows-ref: e
	  
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
