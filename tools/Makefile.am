include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

bin_PROGRAMS     = parse-java pp-java auto-java-front
libexec_PROGRAMS = jtree15-to-abox jtree15-parenthesize core-lift-eblocks

EXTRA_DIST     = $(wildcard *.str) $(wildcard *.meta)
CLEANFILES     = $(wildcard *.dep) $(wildcard *.c)

STRCFLAGS    = --main main-$* 
STRINCLUDES  = -I $(srcdir) -I $(XTC)/share/xtc -I $(top_builddir)/lib $(STRATEGO_GPP_STRCFLAGS)

parse_java_LDADD = $(SSL_LIBS)
pp_java_LDADD = $(top_builddir)/lib/libjava-front.la $(STRATEGO_GPP_LIBS) $(SSL_LIBS)
auto_java_front_LDADD = $(SSL_LIBS)
jtree15_to_abox_LDADD = $(top_builddir)/lib/libjava-front.la $(SSL_LIBS)
jtree15_parenthesize_LDADD = $(top_builddir)/lib/libjava-front.la $(SSL_LIBS)
core_lift_eblocks_LDADD = $(top_builddir)/lib/libjava-front.la $(SSL_LIBS)
