module languages/Java-1.5/types/types/box

imports
	
	include/Java
	lib/task/-
	lib/types/-
	
type rules

  // TODO: the resulting types don't have any name binding information
  Boolean() <box: RefType("Boolean", None())
  Byte()    <box: RefType("Byte", None())
  Char()    <box: RefType("Character", None())
  Short()   <box: RefType("Short", None())
  Int()     <box: RefType("Integer", None())
  Long()    <box: RefType("Long", None())
  Float()   <box: RefType("Float", None())
  Double()  <box: RefType("Double", None())

  RefType("Boolean", None())   <unbox: Boolean()
  RefType("Byte", None())      <unbox: Byte()
  RefType("Character", None()) <unbox: Char()
  RefType("Short", None())     <unbox: Short()
  RefType("Integer", None())   <unbox: Int()
  RefType("Long", None())      <unbox: Long()
  RefType("Float", None())     <unbox: Float()
  RefType("Double", None())    <unbox: Double()
