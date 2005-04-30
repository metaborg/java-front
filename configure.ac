AC_PREREQ([2.58])
AC_INIT([java-front],[0.7],[stratego-bugs@cs.uu.nl])
AC_CONFIG_AUX_DIR([config])
AM_INIT_AUTOMAKE([1.7.2 -Wall -Wno-portability])

# comment to make this a stable release
XT_PRE_RELEASE

m4_pattern_forbid([^(AB|XT)_])
m4_pattern_forbid([^PKG_CHECK_MODULES$])

test "x$prefix" = xNONE && prefix=$ac_default_prefix

XT_SETUP
XT_USE_XT_PACKAGES

XT_PKG_ATERM
XT_PKG_SDF
XT_PKG_STRATEGOXT
XT_SVN_REVISION
XT_TERM_DEFINE

AC_PROG_CC
AC_PROG_INSTALL
AC_PROG_LN_S
AC_PROG_LIBTOOL

AC_CONFIG_FILES([
  java-front.spec
  java-front.pc
  Makefile
  news-archive/Makefile
    
  syn/Makefile
  syn/v1.5/Makefile
  syn/stratego-v1.5/Makefile
  syn/eblock/Makefile

  sig/Makefile
  lib/Makefile
  pp/Makefile
  pp/v1.5/Makefile
  trans/Makefile
  xtc/Makefile
  
  test/Makefile
  test/v1.5/Makefile
  test/gnu-classpath/Makefile
  test/gnu-classpath/v1.5/Makefile
  test/j2sdk/Makefile
  test/pp/Makefile
])
AC_OUTPUT
