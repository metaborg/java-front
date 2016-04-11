module languages/Java-1.5/classes/types/method

imports

  signatures/-
  runtime/task/-
  runtime/types/-
  runtime/properties/-
  runtime/relations/-

  varids

type rules

  // TODO: this will call type-of on the list p*, for which there is no rule. it should map over p* instead.
  MethodParams(p*) : ty*
  where p* : ty*

  Param(_, t, _, _) : t
