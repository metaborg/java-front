include $(top_srcdir)/Makefile.xt

JAVA_FILES = $(wildcard *.java)
EXTRA_DIST = $(JAVA_FILES)

FC_JTREE = $(STRATEGO_REGULAR)/bin/format-check --rtg-nf $(prefix)/share/java-front/Generic-Java.rtg-nf
PARSE_TABLE = $(prefix)/share/sdf/java-front/Generic-Java.tbl

check : $(JAVA_FILES:.java=.fc)

%.fc : %.jtree
	@$(FC_JTREE) --verbose 3 -i $< -o /dev/null 2> $@

%.jtree : %.java
	@$(SGLR)/bin/sglr -2A -s CompilationUnit -p $(PARSE_TABLE) -i $< | $(ASFIX_TOOLS)/bin/implode-asfix -o $@ 
