AC_DEFUN(USE_JAVA_FRONT, [

AC_ARG_WITH(java-front, 
  AC_HELP_STRING([--with-java-front=DIR], [use java-front at DIR  @<:@PREFIX@:>@]), 
  JAVA_FRONT="$withval", 
  JAVA_FRONT="$prefix"
)
AC_SUBST(JAVA_FRONT)

])
