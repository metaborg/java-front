include $(top_srcdir)/Makefile.xt

classpath-0.07.tar.gz :
	wget --passive-ftp ftp://alpha.gnu.org/gnu/classpath/classpath-0.07.tar.gz

gnu-classpath-java : classpath-0.07.tar.gz
	rm -rf classpath-0.07
	tar zxvf classpath-0.07.tar.gz
	find classpath-0.07/java -name '*.java' | sort > $@

gnu-classpath.testsuite : gnu-classpath-java $(prefix)/bin/files2parse-unit-testsuite
	$(prefix)/bin/files2parse-unit-testsuite -i $< -o $@

PARSE_UNIT_TESTSUITES = gnu-classpath.testsuite
PARSE_UNIT_PTABLE = $(sdfdatadir)/Java.tbl

installcheck : $(PARSE_UNIT_TESTSUITES:.testsuite=.runtestsuite)

%.runtestsuite : %.testsuite
	$(SDF_TOOLS)/bin/parse-unit -i $< -p $(PARSE_UNIT_PTABLE) --verbose 1 -o /dev/null

%.jasfix : %.java $(prefix)/share/sdf/java-front/Java.tbl
	sglr -2lv -p $(prefix)/share/sdf/java-front/Java.tbl -i $< -o $@
