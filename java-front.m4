AC_DEFUN([USE_JAVA_FRONT], [

AC_ARG_WITH([java-front], 
  AC_HELP_STRING([--with-java-front=DIR], [use java-front at DIR  @<:@PREFIX@:>@]), 
  [JAVA_FRONT=$withval], 
  [JAVA_FRONT=$prefix]
)
AC_SUBST(JAVA_FRONT)

AC_MSG_CHECKING([for Java-15.tbl at $JAVA_FRONT/share/sdf/java-front/Java-15.tbl])
test -e "$JAVA_FRONT/share/sdf/java-front/Java-15.tbl"
if test $? -eq 0; then
  AC_MSG_RESULT([yes])
else
  AC_MSG_RESULT([no])
  AC_MSG_ERROR([cannot find Java-15.tbl. Use --with-java-front.])
fi

AC_MSG_CHECKING([for parse-java at $JAVA_FRONT/bin/parse-java$EXEEXT])
test -x "$JAVA_FRONT/bin/parse-java$EXEEXT"
if test $? -eq 0; then
  AC_MSG_RESULT([yes])
else
  AC_MSG_RESULT([no])
  AC_MSG_ERROR([cannot find parse-java. Use --with-java-front.])
fi
])
