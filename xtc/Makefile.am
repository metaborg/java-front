include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

bin_PROGRAMS   = parse-java jtree2text pp-java files2parse-unit-testsuite auto-java-front

EXTRA_DIST     = $(wildcard *.str) $(wildcard *.meta)
CLEANFILES     = $(wildcard *.dep) $(wildcard *.c)

SCFLAGS      = --main main-$* 
STRINCLUDES  = -I $(srcdir) -I $(XTC)/share/xtc 
LDADD       += $(SSL_LIBS)

nodist_parse_java_SOURCES = parse-java.c
nodist_jtree2text_SOURCES = jtree2text.c
nodist_pp_java_SOURCES = pp-java.c
nodist_files2parse_unit_testsuite_SOURCES = files2parse-unit-testsuite.c
nodist_auto_java_front_SOURCES = auto-java-front.c

parse_java_LDADD = $(SSL_LIBS)
jtree2text_LDADD = $(SSL_LIBS)
pp_java_LDADD = $(SSL_LIBS)
files2parse_unit_testsuite_LDADD = $(SSL_LIBS)
auto_java_front_LDADD = $(SSL_LIBS)
