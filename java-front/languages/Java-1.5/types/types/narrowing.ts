module languages/Java-1.5/types/types/narrowing

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/relations/-
	languages/Java-1.5/types/types/subtype

type rules // Type narrowing relation for reference and primitive types

	e <n: a
	where e <np: a
		 or e <nr: a
		 
type rules // Narrowing reference types

	e <nr: a
	where a <wr: e // Apply widening on references the other way around.
	  
type rules // Narrowing primitive types

	Double() <np: Byte()
	Double() <np: Short()
	Double() <np: Char()
	Double() <np: Int()
	Double() <np: Long()
	Double() <np: Float()
	
	Float()  <np: Byte()
	Float()  <np: Short()
	Float()  <np: Char()
	Float()  <np: Int()
	Float()  <np: Long()
	
	Long()   <np: Byte()
	Long()   <np: Short()
	Long()   <np: Char()
	Long()   <np: Int()
	
	Int()    <np: Byte()
	Int()    <np: Short()
	Int()    <np: Char()
	
	Short()  <np: Byte()
	Short()  <np: Char()
	
	Byte()   <np: Char()
