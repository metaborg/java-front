include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

MODULES = \
  concrete-syntax.str \
  pp-Generic-Java.str \
  pp-Java-Literals.str \
  pp-Java-Names.str \
  pp-Java.str \
  pp-Java-Expressions.str \
  pp-Java-Modifiers.str \
  pp-Java-Statements.str \
  jtree-generic2abox.str

libexec_PROGRAMS = jtree-generic2abox jtree-generic-parenthesize
EXTRA_DIST     = $(MODULES) $(wildcard *.meta)
CLEANFILES     = $(wildcard *.dep) $(wildcard *.c) jtree-generic-parenthesize.str

SCFLAGS        = --main io-$* 
STRINCLUDES    = -I $(top_srcdir)/lib \
                 -I $(top_builddir)/sig \
                 -I $(top_srcdir)/pp/basic \
                 -I $(GPP)/share/sdf/gpp \
                 -I $(GPP)/share/gpp
LDADD += $(SSL_LIBS)

nodist_jtree_generic2abox_SOURCES = jtree-generic2abox.c
nodist_jtree_generic_parenthesize_SOURCES = jtree-generic-parenthesize.c

jtree-generic-parenthesize.str : $(top_builddir)/syn/generic/Generic-Java.def
	$(SDF_TOOLS)/bin/sdf2parenthesize -i $< -o $@ -m Generic-Java --omod jtree-generic-parenthesize --lang Generic-Java
