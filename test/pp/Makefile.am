include $(top_srcdir)/Makefile.xt

JAVA_FILES = $(wildcard *.java)
EXTRA_DIST = $(JAVA_FILES)
CLEANFILES = $(JAVA_FILES:.java=.fc) $(JAVA_FILES:.java=.jtree)

check : $(JAVA_FILES:.java=.pp)

%.pp : %.jtree
	@$(prefix)/bin/pp-java -i $< -o $@

%.jtree : %.java
	@$(prefix)/bin/parse-java -i $< -o $@ 
