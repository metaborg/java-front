module languages/Java-1.5/types/types/widening

imports
	
	include/Java
	lib/nabl/-
	lib/task/-
	lib/types/-
	lib/relations/-
	languages/Java-1.5/types/types/subtype
	
type rules // Type widening relation for reference and primitive types

  e <widens: a
  where e <widens-prim: a
     or e <widens-ref: a

type rules // Widening primitive types

  Byte()   <widens-prim: Byte()
  Byte()   <widens-prim: Short()
  Byte()   <widens-prim: Int()
  Byte()   <widens-prim: Long()
  Byte()   <widens-prim: Float()
  Byte()   <widens-prim: Double()
  
  Short()  <widens-prim: Short()
  Short()  <widens-prim: Int()
  Short()  <widens-prim: Long()
  Short()  <widens-prim: Float()
  Short()  <widens-prim: Double()
  
  Char()   <widens-prim: Char()
  Char()   <widens-prim: Int()
  Char()   <widens-prim: Long()
  Char()   <widens-prim: Float()
  Char()   <widens-prim: Double()
  
  Int()    <widens-prim: Int()
  Int()    <widens-prim: Long()
  Int()    <widens-prim: Float()
  Int()    <widens-prim: Double()
   
  Long()   <widens-prim: Long()
  Long()   <widens-prim: Float()
  Long()   <widens-prim: Double()
  
  Float()  <widens-prim: Float()
  Float()  <widens-prim: Double()
  
  Double() <widens-prim: Double()
