AC_PREREQ(2.57)
AC_INIT([java-front],[0.4pre],[stratego-bugs@cs.uu.nl])
AM_INIT_AUTOMAKE([1.7.2 -Wall -Wno-portability])

test "x$prefix" = xNONE && prefix=$ac_default_prefix

XT_SETUP
USE_XT_PACKAGES

XT_PKG_ATERM
XT_PKG_SDF
XT_PKG_STRATEGOXT
XT_TERM_DEFINE

AC_PROG_CC
AC_PROG_INSTALL
AC_PROG_LN_S
AC_PROG_LIBTOOL

DETECT_SVN_REVISION
AC_SUBST(SVN_REVISION)

AC_CONFIG_FILES([
  java-front.spec
  Makefile
    
  syn/Makefile
  syn/basic/Makefile
  syn/v1.5/Makefile
  syn/stratego-v1.5/Makefile
  syn/generic/Makefile
  syn/eblock/Makefile

  sig/Makefile
  pp/Makefile
  pp/v1.5/Makefile
  pp/basic/Makefile
  trans/Makefile
  xtc/Makefile
  
  test/Makefile
  test/basic/Makefile
  test/v1.5/Makefile
  test/gnu-classpath/Makefile
  test/gnu-classpath/v1.5/Makefile
  test/j2sdk/Makefile
  test/fc/Makefile
  test/pp/Makefile
])
AC_OUTPUT
