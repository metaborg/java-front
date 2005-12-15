include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

bin_PROGRAMS   = parse-java pp-java auto-java-front

EXTRA_DIST     = $(wildcard *.str) $(wildcard *.meta)
CLEANFILES     = $(wildcard *.dep) $(wildcard *.c)

SCFLAGS      = --main main-$* 
STRINCLUDES  = -I $(srcdir) -I $(XTC)/share/xtc 
LDADD       += $(SSL_LIBS)

nodist_parse_java_SOURCES = parse-java.c
nodist_pp_java_SOURCES = pp-java.c
nodist_auto_java_front_SOURCES = auto-java-front.c

parse_java_LDADD = $(SSL_LIBS)
pp_java_LDADD = $(SSL_LIBS)
auto_java_front_LDADD = $(SSL_LIBS)
