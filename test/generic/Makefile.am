include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/Makefile.parse-unit

SUBDIRS           = compilation-units
BOOTCLEAN_SUBDIRS = $(SUBDIRS)
EXTRA_DIST        = $(wildcard *.testsuite) $(wildcard *.atestsuite)

TESTSUITES = \
  generic-compilation-unit.testsuite

PARSE_UNIT_PTABLE="`XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc get Generic-Java.tbl`"

check: $(TESTSUITES:.testsuite=.runtestsuite)
