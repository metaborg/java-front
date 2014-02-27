module languages/Java-1.5/types/types/widening

imports
	
	include/Java
	lib/nabl/-
	lib/task/-
	lib/types/-
	lib/relations/-
	languages/Java-1.5/types/types/subtype
	
type rules // Type widening relation for reference and primitive types

  e <w: a
  where e <wp: a
     or e <wr: a

type rules // Widening primitive types

  Byte()   <wp: Byte()
  Byte()   <wp: Short()
  Byte()   <wp: Int()
  Byte()   <wp: Long()
  Byte()   <wp: Float()
  Byte()   <wp: Double()
  
  Short()  <wp: Short()
  Short()  <wp: Int()
  Short()  <wp: Long()
  Short()  <wp: Float()
  Short()  <wp: Double()
  
  Char()   <wp: Char()
  Char()   <wp: Int()
  Char()   <wp: Long()
  Char()   <wp: Float()
  Char()   <wp: Double()
  
  Int()    <wp: Int()
  Int()    <wp: Long()
  Int()    <wp: Float()
  Int()    <wp: Double()
   
  Long()   <wp: Long()
  Long()   <wp: Float()
  Long()   <wp: Double()
  
  Float()  <wp: Float()
  Float()  <wp: Double()
  
  Double() <wp: Double()
