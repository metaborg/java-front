include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/Makefile.parse-unit

SUBDIRS           = comments compilation-units expressions statements
BOOTCLEAN_SUBDIRS = $(SUBDIRS)
EXTRA_DIST        = $(wildcard *.testsuite) $(wildcard *.atestsuite)

TESTSUITES = \
  identifiers.testsuite \
  comments.testsuite \
  compilation-unit.testsuite \
  expressions.testsuite \
  floating-point-literals.testsuite \
  lexical-translations.testsuite \
  statements.testsuite

PARSE_UNIT_PTABLE="`XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc get Java.tbl`"

check : $(TESTSUITES:.testsuite=.runtestsuite)
