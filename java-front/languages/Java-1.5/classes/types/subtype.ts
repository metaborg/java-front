module languages/Java-1.5/classes/types/subtype

imports
	
	include/Java
	languages/Java-1.5/types/types/subtype

type rules

	// TODO: store relations
	ClassDec(ClassDecHead(_, c, _, SuperDec(pc), _), _) :-
	where store c <wr: pc
	
	// TODO: list matching
	ClassDec(ClassDecHead(_, c, _, _, ... ImplementsDec(ic) ...), _) :-
	where store c <wr: ic
