module languages/Java-1.5/types/types/references

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/classes/names/classes

signatures

	RefType : TypeName * TypeParams -> Type

	Reference    : TypeKind
	Class        : TypeKind
	Interface    : TypeKind
	Array        : TypeKind
	
	Final        : TypeKind
	
	Object       : TypeKind 
	Cloneable    : TypeKind
	Serializable : TypeKind
	String       : TypeKind
	
type rules // Reference types

	ClassOrInterfaceType(c, tp*) : RefType(c, tp*)
	ClassType(c, tp*)            : RefType(c, tp*)
	InterfaceType(c, tp*)        : RefType(c, tp*)
	
	ArrayType(ty) : ArrayType(ty)

relations // Reference type kinds

	RefType(_, _) <is: Reference()
	ArrayType(_)  <is: Reference()
	Null()        <is: Reference()

	RefType(TypeName(c), _)   <is: Class()
	where definition of c has kind Class()
	RefType(TypeName(_, c), _) <is: Class()
	where definition of c has kind Class()
		
	RefType(TypeName(c), _)   <is: Interface()
	where definition of c has kind Interface()
	RefType(TypeName(_, c), _) <is: Interface()
	where definition of c has kind Interface()

	ArrayType(_)  <is: Array()
	
	RefType(TypeName(c), _)    <is: Final()
	where c has modifiers Final()
	RefType(TypeName(_, c), _) <is: Final()
	where c has modifiers Final()
	
relations // Built in type kinds

	RefType(TypeName("Object"), _)          <is: Object()
	RefType(TypeName(_, "Object"), _)       <is: Object()
	RefType(TypeName("Cloneable"), _)       <is: Cloneable()
	RefType(TypeName(_, "Cloneable"), _)    <is: Cloneable()
	RefType(TypeName("Serializable"), _)    <is: Serializable()
	RefType(TypeName(_, "Serializable"), _) <is: Serializable()
	RefType(TypeName("String"), _)          <is: String()
	RefType(TypeName(_, "String"), _)       <is: String()
