include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

SIGS = \
  Generic-Java.str \
  Java.str \
  Java-Classes.str \
  Java-Comments.str \
  Java-Expressions.str \
  Java-Identifiers.str \
  Java-Interfaces.str \
  Java-Keywords.str \
  Java-Lexical.str \
  Java-Literals.str \
  Java-MethodDecs.str \
  Java-Modifiers.str \
  Java-Names.str \
  Java-Statements.str \
  Java-TypeDecs.str \
  Java-Types.str \
  Java-VarDecs.str \
  Java-WhiteSpace.str
  

pkgdata_DATA    = $(subst .str,.rtree,$(SIGS))
EXTRA_DIST      = $(SIGS) $(pkgdata_DATA)
CLEANFILES      = Generic-Java.def
BOOTCLEANFILES	= $(SIGS) $(pkgdata_DATA)

Generic-Java.def : 
	ln -s $(top_srcdir)/syn/generic/Generic-Java.def $@

$(SIGS) : Generic-Java.def

