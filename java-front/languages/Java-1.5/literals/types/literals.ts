module languages/Java-1.5/literals/types/literals

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/references
	
type rules

  Bool(_)   : Boolean()
  Char(_)   : Char()
  Single(_) : Char()
  Deci(_)   : Int()
  Hexa(_)   : Int()
  Octa(_)   : Int()
  Double(_) : Double()
  Float(_)  : Float()
  Null()    : Null()
  
  // TODO: add def (or use) annotations to the name, and type annotation to the term
  // String(_) : RefType(TypeName("String"), None())
  // Class(_)  : RefType(TypeName("Class"), None())
