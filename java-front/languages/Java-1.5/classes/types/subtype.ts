module languages/Java-1.5/classes/types/subtype

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/widening

type rules

	ClassDec(ClassDecHead(_, c, _, SuperDec(pc), _), _) :-
	where store c <widens-ref: pc
	  and store c <extends: pc
	
	// TODO: list matching
	ClassDec(ClassDecHead(_, c, _, _, ... ImplementsDec(ic) ...), _) :-
	where store c <widens-ref: ic
	  and store c <implements: ic
