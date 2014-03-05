module languages/Java-1.5/statements/types/control

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
type rules

  If(e, _)
+ If(e, _, _)
+ AssertStm(e)
+ AssertStm(e, _)
+ While(e, _)
+ DoWhile(_, e)
+ For(_, e, _, _) :-
  where not(e == None())
    and e : ty
    and ty == Boolean() else error "Expected boolean" on e