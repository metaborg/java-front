include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/Makefile.parse-unit

EXTRA_DIST = $(wildcard *.testsuite) $(wildcard *.atestsuite)

TESTSUITES = \
  enum-decs.testsuite \
  identifiers.testsuite \
  compilation-unit.testsuite \
  expressions.testsuite \
  floating-point-literals.testsuite \
  lexical-translations.testsuite \
  statements.testsuite \
  types.testsuite

PARSE_UNIT_PTABLE="`XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc get Java-15.tbl`"

check : $(TESTSUITES:.testsuite=.runtestsuite)
