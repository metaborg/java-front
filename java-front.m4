#     							-*- Autoconf -*-
# serial 2
#
# Author: Martin Bravenboer <martin@cs.uu.nl>
#

# USE_JAVA_FRONT
# --------------
AC_DEFUN([XT_USE_JAVA_FRONT], [
  XT_CHECK_PACKAGE([JAVA_FRONT],[java-front])

  AC_MSG_CHECKING([for parse-java at $JAVA_FRONT/bin/parse-java$EXEEXT])
  test -x "$JAVA_FRONT/bin/parse-java$EXEEXT"
  if test $? -eq 0; then
    AC_MSG_RESULT([yes])
  else
    AC_MSG_RESULT([no])
    AC_MSG_ERROR([cannot find parse-java. Is java-front installed correctly?])
  fi
])


# USE_J2SDK
# --------------
AC_DEFUN([XT_USE_J2SDK], [
  AC_REQUIRE([AC_PROG_CC])

  AC_ARG_WITH([j2sdk],
    AS_HELP_STRING([--j2sdk=DIR],
                   [use J2SDK at DIR @<:@prefix@:>@]),
    [J2SDK=$withval],
    [J2SDK=$prefix]
  )

  JAVAC=$J2SDK/bin/javac$EXEEXT
  JAR=$J2SDK/bin/jar
  JAVA=$J2SDK/bin/java

  AC_SUBST([J2SDK])
  AC_SUBST([JAVA])
  AC_SUBST([JAVAC])
  AC_SUBST([JAR])

  AC_MSG_CHECKING([for javac at $JAVAC])
  test -x "$JAVAC"
  if test $? -eq 0; then
    AC_MSG_RESULT([yes])
  else
    AC_MSG_RESULT([no])
    AC_MSG_ERROR([cannot find javac. Use --with-j2sdk.])
  fi

  AC_MSG_CHECKING([for jar at $JAR])
  test -x "$JAR"
  if test $? -eq 0; then
    AC_MSG_RESULT([yes])
  else
    AC_MSG_RESULT([no])
    AC_MSG_ERROR([cannot find jar. Use --with-j2sdk.])
  fi

  AC_MSG_CHECKING([for java at $JAVA])
  test -x "$JAVA"
  if test $? -eq 0; then
    AC_MSG_RESULT([yes])
  else
    AC_MSG_RESULT([no])
    AC_MSG_ERROR([cannot find java. Use --with-j2sdk.])
  fi
])
