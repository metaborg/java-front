include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/Makefile.parse-unit

EXTRA_DIST = $(wildcard *.testsuite)

TESTSUITES = \
  enum-decs.testsuite \
  $(top_srcdir)/test/basic/identifiers.testsuite \
  $(top_srcdir)/test/basic/compilation-unit.testsuite \
  $(top_srcdir)/test/basic/expressions.testsuite \
  $(top_srcdir)/test/basic/floating-point-literals.testsuite \
  $(top_srcdir)/test/basic/lexical-translations.testsuite \
  $(top_srcdir)/test/basic/statements.testsuite

PARSE_UNIT_PTABLE="`XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc get Java-15.tbl`"

check : $(TESTSUITES:.testsuite=.runtestsuite)
