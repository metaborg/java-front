module languages/Java-1.5/types/types/widening

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
type rules

  a <widens: e
  where a <widens-prim: e
     or a <widens-ref: e

	a <widens-ref: e
	where a == Null()
     or (a <is: Interface() and e <is: Object())
     or (a <is: Array() and e <is: Object())
     or (a <is: Array() and e <is: Cloneable())
     or (a <is: Array() and e <is: Serializable())
     or a <widens-array: e
 
	ArrayType(a) <widens-array: ArrayType(e)
	where a <widens-ref: e

type rules

  Byte()   <widens-prim: Short()
  Byte()   <widens-prim: Int()
  Byte()   <widens-prim: Long()
  Byte()   <widens-prim: Float()
  Byte()   <widens-prim: Double()
  
  Short()  <widens-prim: Int()
  Short()  <widens-prim: Long()
  Short()  <widens-prim: Float()
  Short()  <widens-prim: Double()
  
  Char()   <widens-prim: Int()
  Char()   <widens-prim: Long()
  Char()   <widens-prim: Float()
  Char()   <widens-prim: Double()
  
  Int()    <widens-prim: Long()
  Int()    <widens-prim: Float()
  Int()    <widens-prim: Double()
   
  Long()   <widens-prim: Float()
  Long()   <widens-prim: Double()
  
  Float()  <widens-prim: Double()
