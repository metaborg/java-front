include $(top_srcdir)/Makefile.xt

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

# create J2SDK testsuites
j2sdk-src : src.zip
	rm -rf j2sdk-src
	mkdir j2sdk-src
	unzip src.zip -d j2sdk-src

j2sdk.%.list : j2sdk-src
	find j2sdk-src/$* -name '*.java' | sort > $@

j2sdk.%.testsuite : j2sdk.%.list $(prefix)/bin/files2parse-unit-testsuite
	$(prefix)/bin/files2parse-unit-testsuite -i $< -o $@

j2sdkcheck : $(J2SDK_TESTSUITES:.testsuite=.runtestsuite)

# generic rules
%.runtestsuite : %.testsuite
	$(SDF_TOOLS)/bin/parse-unit -i $< -p $(PARSE_UNIT_PTABLE) --verbose 1 -o /dev/null

%.jasfix : %.java $(prefix)/share/sdf/java-front/Java.tbl
	sglr -2lv -p $(prefix)/share/sdf/java-front/Java.tbl -i $< -o $@

# targets
GNU_TESTSUITES = \
  gnu-classpath.java.testsuite \
  gnu-classpath.javax.testsuite \
  gnu-classpath.external.testsuite \
  gnu-classpath.gnu.testsuite

J2SDK_TESTSUITES = \
  j2sdk.org.testsuite \
  j2sdk.javax.testsuite \
  j2sdk.sunw.testsuite \
  j2sdk.com.testsuite \
  j2sdk.java.testsuite 

PARSE_UNIT_PTABLE = $(sdfdatadir)/Java.tbl

installcheck : gnucheck
