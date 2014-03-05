module languages/Java-1.5/types/types/narrowing

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/widening

type rules // Type narrowing relation for reference and primitive types

	e <narrows: a
	where e <narrows-prim: a
		 or e <narrows-ref: a
		 
type rules // Narrowing reference types

	e <narrows-ref: a
	where a <widens-ref: e // Apply widening on references the other way around.
	  
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
