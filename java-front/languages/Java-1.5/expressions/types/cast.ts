module languages/Java-1.5/expressions/types/cast

imports
	
	include/Java
	lib/task/-
	lib/types/-
	languages/Java-1.5/types/types/cast
	
type rules
	
	CastPrim(t, e) + CastRef(t, e) : t-ty
  where t : t-ty
    and e : e-ty
    and e-ty <cast: t-ty else $[Cannot cast expression of type [e-ty] to [t-ty].]
