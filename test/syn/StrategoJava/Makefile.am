include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/syn/Makefile.parse-unit
include $(wildcard *.dep)

TESTSUITES = preterm.testsuite
PARSE_UNIT_PTABLE = Main.tbl

check_DATA = Main.tbl
CLEANFILES = Main.def Main.tbl
EXTRA_DIST = Main.sdf $(wildcard *.testsuite)

SDFINCLUDES = -Idef $(top_srcdir)/syn/Stratego-Java-15.def

check : $(TESTSUITES:.testsuite=.runtestsuite)
