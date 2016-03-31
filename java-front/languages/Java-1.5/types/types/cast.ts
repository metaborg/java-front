module languages/Java-1.5/types/types/cast

imports

  signatures/-
  runtime/task/-
  runtime/types/-
  runtime/properties/-
  runtime/relations/-

  languages/Java-1.5/types/types/widening
  languages/Java-1.5/types/types/narrowing

relations

  e-ty <cast: t-ty
  where e-ty == t-ty
     or e-ty <widens-prim:   t-ty
     or e-ty <narrows-prim:  t-ty
     or e-ty <widens-ref:    t-ty
     or e-ty <narrows-ref:   t-ty
     or e-ty <widens-array:  t-ty
     or e-ty <narrows-array: t-ty
