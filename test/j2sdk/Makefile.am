include $(top_srcdir)/Makefile.xt
# include $(top_srcdir)/test/Makefile.parse-unit

# create J2SDK testsuites
j2sdk-src : src.zip
	rm -rf j2sdk-src
	mkdir j2sdk-src
	unzip src.zip -d j2sdk-src

j2sdk.list : j2sdk-src
	find j2sdk-src -name '*.java' | sort > $@

# j2sdk.atestsuite : j2sdk.list $(prefix)/bin/files2parse-unit-testsuite
# 	$(prefix)/bin/files2parse-unit-testsuite -i $< -o $@

j2sdkcheck : ppcheck

# $(J2SDK_TESTSUITES:.testsuite=.runtestsuite)

# targets
# J2SDK_TESTSUITES = j2sdk.testsuite
# PARSE_UNIT_PTABLE = $(sdfdatadir)/Java.tbl
# %.jasfix : %.java $(prefix)/share/sdf/java-front/Java.tbl
# 	sglr -2lv -p $(prefix)/share/sdf/java-front/Java.tbl -i $< -o $@

PARSEJAVA=XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc call parse-java
PPJAVA=XTC_REPOSITORY="$(BUILD_REPOSITORY)" $(XTC)/bin/xtc call pp-java

# pretty-print tests
ppcheck : j2sdk.list
	FILES="`cat j2sdk.list`" && \
	for file in $$FILES; do \
	  if test "$${file}" = "j2sdk-src/com/sun/org/apache/xalan/internal/xsltc/compiler/FunctionCall.java"; then \
	    echo "GRBML: $$file is EVIL. I'm not going to check it." ; \
          else \
            $(MAKE) "$${file%.java}.jtree" "$${file%.java}.ppjtree" "$${file%.java}.ppdiff" || exit 1; \
          fi \
	done

%.ppdiff : %.jtree %.ppjtree
	diff $*.jtree $*.ppjtree > $@
	cat $@

%.ppjtree : %.jtree
	$(PPJAVA) -i $< | $(PARSEJAVA) -1.5 -o $@

%.jtree : %.java
	$(PARSEJAVA) -1.5 -i $< -o $@

clean-local:
	find . -name "*.jtree" | xargs rm
	find . -name "*.ppjtree" | xargs rm
	find . -name "*.ppdiff" | xargs rm
