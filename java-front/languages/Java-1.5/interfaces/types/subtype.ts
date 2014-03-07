module languages/Java-1.5/interfaces/types/subtype

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/widening

relations

	define transitive extends

type rules
	
	// TODO: list matching
	InterfaceDec(InterfaceDecHead(_, i, _, ... ExtendsInterfaces(ei) ...), _) :-
	where store i <widens-ref: ei
    and store i <extends: ei
