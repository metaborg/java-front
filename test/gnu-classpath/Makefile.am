include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/test/Makefile.parse-unit

# create GNU classpath testsuites
classpath-0.07.tar.gz :
	wget --passive-ftp ftp://alpha.gnu.org/gnu/classpath/classpath-0.07.tar.gz

classpath-0.07 : classpath-0.07.tar.gz
	rm -rf classpath-0.07
	tar zxvf classpath-0.07.tar.gz

gnu-classpath.%.list : classpath-0.07
	find classpath-0.07/$* -name '*.java' | sort > $@

gnu-classpath.%.testsuite : gnu-classpath.%.list $(prefix)/bin/files2parse-unit-testsuite
	$(prefix)/bin/files2parse-unit-testsuite -i $< -o $@

gnucheck : $(GNU_TESTSUITES:.testsuite=.runtestsuite)

# targets
GNU_TESTSUITES = \
  gnu-classpath.java.testsuite \
  gnu-classpath.javax.testsuite \
  gnu-classpath.external.testsuite \
  gnu-classpath.gnu.testsuite

PARSE_UNIT_PTABLE = $(sdfdatadir)/Java.tbl

installcheck : gnucheck
