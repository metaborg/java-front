module languages/Java-1.5/types/types/primitives

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-

signatures

	Integral  : TypeKind
	Decimal   : TypeKind
	Numerical : TypeKind
	Primitive : TypeKind
	
type rules // Primitive types

	Boolean() : Boolean()
	Byte()    : Byte()
	Short()   : Short()
	Int()     : Int()
	Long()    : Long()
	Char()    : Char()
	Float()   : Float()
	Double()  : Double()

relations // Primitive type kinds

  Byte()  <is: Integral()
  Short() <is: Integral()
  Int()   <is: Integral()
  Long()  <is: Integral()
     
	Float()  <is: Decimal()
	Double() <is: Decimal()
	   
	t <is: Numerical()
	where t <is: Integral()
	   or t <is: Decimal()
	   
	Byte()   <is: Primitive()
	Short()  <is: Primitive()
	Char()   <is: Primitive()
	Int()    <is: Primitive()
	Long()   <is: Primitive()
	Float()  <is: Primitive()
	Double() <is: Primitive()
