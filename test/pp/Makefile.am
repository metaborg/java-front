include $(top_srcdir)/Makefile.xt

JAVA_FILES = $(wildcard *.java)
EXTRA_DIST = $(JAVA_FILES)
CLEANFILES = $(JAVA_FILES:.java=.pp) $(JAVA_FILES:.java=.jtree)

check : $(JAVA_FILES:.java=.pp)

%.pp : %.jtree
	XTC_REPOSITORY="$(BUILD_REPOSITORY)" \
	  $(XTC)/bin/xtc call pp-java -i $< -o $@

%.jtree : %.java
	XTC_REPOSITORY="$(BUILD_REPOSITORY)" \
	  $(XTC)/bin/xtc call parse-java -i $< -o $@ 
