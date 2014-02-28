module languages/Java-1.5/types/types/primitives

imports
	
	include/Java
	lib/types/-
	
type rules // Primitive types

	Boolean() : Boolean()
	Byte()    : Byte()
	Short()   : Short()
	Int()     : Int()
	Long()    : Long()
	Char()    : Char()
	Float()   : Float()
	Double()  : Double()

type rules // Primitive type kinds

  t <is: Integral()
  where t == Byte()
     or t == Short()
     or t == Int()
     or t == Long()
     
	t <is: Decimal()
	where t == Float()
	   or t == Double()
	   
	t <is: Numerical()
	where t <is: Integral()
	   or t <is: Decimal()
	   
	t <is: Primitive()
  where t == Byte()
     or t == Short()
     or t == Char()
     or t == Int()
     or t == Long()
	   or t == Float()
	   or t == Double()
