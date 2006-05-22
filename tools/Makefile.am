include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

bin_PROGRAMS     = parse-java pp-java auto-java-front
libexec_PROGRAMS = jtree15-to-abox jtree15-parenthesize

EXTRA_DIST     = $(wildcard *.str) $(wildcard *.meta)
CLEANFILES     = $(wildcard *.dep) $(wildcard *.c)

STRCFLAGS    = --main main-$* 
STRINCLUDES  = -I $(srcdir) -I $(XTC)/share/xtc -I $(top_builddir)/lib $(STRATEGO_GPP_STRCFLAGS)

nodist_parse_java_SOURCES = parse-java.c
nodist_pp_java_SOURCES = pp-java.c
nodist_auto_java_front_SOURCES = auto-java-front.c
nodist_jtree15_parenthesize_SOURCES = jtree15-parenthesize.c
nodist_jtree15_to_abox_SOURCES = jtree15-to-abox.c

parse_java_LDADD = $(SSL_LIBS)
pp_java_LDADD = $(top_builddir)/lib/libjava-front.la $(STRATEGO_GPP_LIBS) $(SSL_LIBS)
auto_java_front_LDADD = $(SSL_LIBS)
jtree15_to_abox_LDADD = $(top_builddir)/lib/libjava-front.la $(SSL_LIBS)
jtree15_parenthesize_LDADD = $(top_builddir)/lib/libjava-front.la $(SSL_LIBS)
