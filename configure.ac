AC_PREREQ(2.57)
AC_INIT([java-front],[0.4.0pre],[stratego-bugs@cs.uu.nl])
AM_INIT_AUTOMAKE([1.7.2 -Wall -Wno-portability])

test "x$prefix" = xNONE && prefix=$ac_default_prefix

USE_XT_PACKAGES
USE_STRATEGO_REGULAR

AC_PROG_CC
AC_PROG_INSTALL

DETECT_SVN_REVISION
AC_SUBST(SVN_REVISION)

AC_CONFIG_FILES([
  java-front.spec
  Makefile
    
  syn/Makefile
  syn/basic/Makefile
  syn/generic/Makefile

  sig/Makefile
  sig/basic/Makefile
  sig/generic/Makefile

  pp/Makefile
  xtc/Makefile
  
  test/Makefile
  test/basic/Makefile
  test/basic/comments/Makefile
  test/basic/compilation-units/Makefile
  test/basic/expressions/Makefile
  test/basic/statements/Makefile  
  test/generic/Makefile
  test/generic/compilation-units/Makefile
  test/gnu-classpath/Makefile
  test/j2sdk/Makefile
])
AC_OUTPUT