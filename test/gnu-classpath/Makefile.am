include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/Makefile.parse-unit

GNUCP_VERSION=0.09

# create GNU classpath testsuites
classpath-$(GNUCP_VERSION).tar.gz :
	wget --passive-ftp ftp://ftp.gnu.org/pub/gnu/classpath/classpath-$(GNUCP_VERSION).tar.gz

classpath-$(GNUCP_VERSION) : classpath-$(GNUCP_VERSION).tar.gz
	rm -rf classpath-$(GNUCP_VERSION)
	tar zxvf classpath-$(GNUCP_VERSION).tar.gz

gnu-classpath.list : classpath-$(GNUCP_VERSION)
	find classpath-$(GNUCP_VERSION) -name '*.java' | sort > $@

gnu-classpath.atestsuite : gnu-classpath.list
	 $(top_builddir)/xtc/files2parse-unit-testsuite -i $< -o $@

parsecheck : $(GNU_TESTSUITES:.testsuite=.runtestsuite)

clean-local :
	rm -rf classpath-$(GNUCP_VERSION) classpath-$(GNUCP_VERSION).tar.gz

# targets
GNU_TESTSUITES = \
  gnu-classpath.testsuite

PARSE_UNIT_PTABLE="`XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc get Java.tbl`"
PARSEJAVA=XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc call parse-java
PPJAVA=XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc call pp-java

check : parsecheck ppcheck

# pretty-print tests
ppcheck : gnu-classpath.list
	FILES="`cat gnu-classpath.list`" && \
	for file in $$FILES; do \
		$(MAKE) "$${file%.java}.ppjtree" || exit 1; \
	done

%.ppdiff : %.jtree %.ppjtree
	diff $*.jtree $*.ppjtree 

%.ppjtree : %.java
	$(PARSEJAVA) -i $<  \
	| $(PPJAVA) \
	| $(PARSEJAVA) -o $@

%.jtree : %.java
	$(PARSEJAVA) -i $< -o $@
