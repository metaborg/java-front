include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/Makefile.parse-unit

SUBDIRS           = comments compilation-units expressions statements
BOOTCLEAN_SUBDIRS = $(SUBDIRS)
EXTRA_DIST        = $(wildcard *.testsuite)

TESTSUITES = \
  comments.testsuite \
  compilation-unit.testsuite \
  expressions.testsuite \
  floating-point-literals.testsuite \
  lexical-translations.testsuite \
  statements.testsuite

PARSE_UNIT_PTABLE = $(sdfdatadir)/Java.tbl

installcheck-local: $(TESTSUITES:.testsuite=.runtestsuite)
