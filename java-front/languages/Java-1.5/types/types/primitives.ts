module languages/Java-1.5/types/types/primitives

imports
	
	include/Java
	lib/types/-
	
type rules

	Boolean() : Boolean()
	Byte()    : Byte()
	Short()   : Short()
	Int()     : Int()
	Long()    : Long()
	Char()    : Char()
	Float()   : Float()
	Double()  : Double()

type rules

  <bool:   Boolean()
  <byte:   Byte()
  <short:  Short()
  <char:   Char()
  <int:    Int()
  <long:   Long()
  <double: Double()
  
  <integral: t
  where <byte:  t
     or <short: t
     or <int:   t
     or <long:  t
     
	<decimal: t
	where <float:  t
	   or <double: t
	   
	<numerical: t
	where <integral: t
	   or <decimal:  t
	   
	<prim: t
  where <bool:  t
    or <byte:   t
    or <short:  t
    or <char:   t
    or <int:    t
    or <long:   t
    or <double: t
