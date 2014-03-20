module languages/Java-1.5/classes/types/subtype

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/classes/trans/desugar
	languages/Java-1.5/types/types/widening

relations

	define transitive <widens-ref:
	define transitive <extends:
	define transitive <implements:

type rules

	ClassDec(_, _, _, c, _, SuperDec(sc), _, _) :-
	where store c <widens-ref: sc
	  and store c <extends: sc
	
	ImplementsDec(c, it) :-
	where store c <widens-ref: it
	  and store c <implements: it
