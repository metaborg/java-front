module languages/Java-1.5/types/types/assignment

imports

  signatures/-

  languages/Java-1.5/types/types/widening
  languages/Java-1.5/types/types/narrowing

relations

  e-ty <assign-conv: v-ty
  where e-ty == v-ty
     or e-ty <widens: v-ty
     or (
           (e-ty == Byte() or e-ty == Short() or e-ty == Char() or e-ty == Int())
       and (v-ty == Byte() or v-ty == Short() or v-ty == Char())
       and e-ty <narrows-prim: v-ty
     )

  e-ty <comp-assign-conv: v-ty
  where e-ty == v-ty
     or e-ty <widens-prim:  v-ty
     or e-ty <narrows-prim: v-ty
