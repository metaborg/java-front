include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/Makefile.parse-unit

EXTRA_DIST = $(wildcard *.testsuite)

TESTSUITES = \
  annotations.testsuite \
  comments.testsuite \
  enum-decs.testsuite \
  identifiers.testsuite \
  compilation-unit.testsuite \
  expressions.testsuite \
  floating-point-literals.testsuite \
  lexical-translations.testsuite \
  statements.testsuite \
  types.testsuite

PARSE_UNIT_PTABLE=$(top_builddir)/syn/v1.5/Java-15.tbl

check : $(TESTSUITES:.testsuite=.runtestsuite)
