module languages/Java-1.5/expressions/types/arithmetic

imports

  signatures/-
  runtime/task/-
  runtime/types/-
  runtime/properties/-
  runtime/relations/-

  languages/Java-1.5/types/types/primitives
  languages/Java-1.5/types/types/promotion

type rules

  Plus(x, y) : ty
  where x : x-ty
    and y : y-ty
    and (
      (
            x-ty <is: String() and x-ty => ty
         or y-ty <is: String() and y-ty => ty
      )
      or
      (
            x-ty <is: Numerical() else error "Expected numerical" on x
        and y-ty <is: Numerical() else error "Expected numerical" on y
        and <promote-bin> (x-ty, y-ty) => ty
      )
    )

  Minus(x, y)
+ Mul(x, y)
+ Div(x, y)
+ Mod(x, y) : ty
  where x : x-ty
    and y : y-ty
    and x-ty <is: Numerical() else error "Expected numerical" on x
    and y-ty <is: Numerical() else error "Expected numerical" on y
    and <promote-bin> (x-ty, y-ty) => ty

  LeftShift(x, y)
+ RightShift(x, y)
+ URightShift(x, y) : x-prom-ty
  where x : x-ty
    and y : y-ty
    and <promote-un> x-ty => x-prom-ty
    and <promote-un> y-ty => y-prom-ty
    and x-prom-ty <is: Integral() else error "Expected numerical" on x
    and x-prom-ty <is: Integral() else error "Expected integral" on y

  Plus(e)
+ Minus(e) : ty
  where e : ty
    and <promote-un> ty => prom-ty
    and prom-ty <is: Numerical() else error "Expected numerical" on e

  PreIncr(e)
+ PostIncr(e)
+ PreDecr(e)
+ PostDecr(e) : e-ty
  where e : e-ty
    and e-ty <is: Numerical() else error "Expected numerical" on e
