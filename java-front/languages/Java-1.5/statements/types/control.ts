module languages/Java-1.5/statements/types/control

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/equality
	
type rules

  If(e, _)
+ If(e, _, _)
+ AssertStm(e)
+ AssertStm(e, _)
+ While(e, _)
+ DoWhile(_, e)
+ For(_, e, _, _) :-
  where e != None()
    and e : ty
    and ty == Boolean() else "Expected boolean" on e