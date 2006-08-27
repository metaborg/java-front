include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

bin_PROGRAMS     = parse-java pp-java auto-java-front
libexec_PROGRAMS = jtree15-to-abox jtree15-parenthesize core-lift-eblocks

EXTRA_DIST     = $(wildcard *.str) $(wildcard *.meta)
CLEANFILES     = $(wildcard *.dep) $(wildcard *.c)

STRCFLAGS    = --main main-$* 
STRINCLUDES  = -I $(srcdir) -I $(XTC)/share/xtc -I $(top_builddir)/lib $(STRATEGO_GPP_STRCFLAGS)

LDADD += $(top_builddir)/lib/libjava-front.la $(SSL_LIBS)
