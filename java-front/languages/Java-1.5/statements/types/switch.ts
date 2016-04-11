module languages/Java-1.5/statements/types/switch

imports

  signatures/-
  runtime/task/-
  runtime/types/-
  runtime/nabl/-
  languages/Java-1.5/types/types/assignment

type rules

  // TODO: check that types of case expressions can be converted to type of the switch expression.
  // TODO: check for duplicate values in cases.
  Switch(e, body) :-
  where e : e-ty
    and (e == Byte() or e == Char() or e == Short() or e == Int())
      else error "Expected by, char, short or integer" on e

  Case(e) :-
  where e : e-ty