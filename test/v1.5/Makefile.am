include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/Makefile.parse-unit

EXTRA_DIST = $(wildcard *.testsuite)

TESTSUITES = \
  enum-decs.testsuite

PARSE_UNIT_PTABLE="`XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc get Java-15.tbl`"

check : $(TESTSUITES:.testsuite=.runtestsuite)
