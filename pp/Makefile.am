include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

PP_MODULES = \
  concrete-syntax.str \
  pp-Generic-Java.str \
  pp-Java-Literals.str \
  pp-Java-Names.str \
  pp-Java.str \
  pp-Java-Expressions.str \
  pp-Java-Modifiers.str \
  pp-Java-Statements.str \
  types/pp-Java-PrimitiveTypes.str \
  types/pp-Java-ReferenceTypes.str \
  interfaces/pp-Java-Annotations.str \
  jtree2abox.str

libexec_PROGRAMS = jtree2abox jtree-parenthesize
EXTRA_DIST     = $(PP_MODULES) $(wildcard *.meta)
CLEANFILES     = $(wildcard *.dep) $(wildcard *.c) jtree-parenthesize.str

SCFLAGS        = --main io-$* 
STRINCLUDES    = -I $(top_srcdir)/lib \
                 -I $(top_builddir)/sig \
                 -I $(top_srcdir)/pp \
                 -I $(top_srcdir)/pp/types \
                 -I $(top_srcdir)/pp/interfaces \
                 -I $(GPP)/share/sdf/gpp \
                 -I $(GPP)/share/gpp
LDADD += $(SSL_LIBS)

nodist_jtree2abox_SOURCES = jtree2abox.c

jtree-parenthesize.str : $(top_builddir)/syn/v1.5/Java-15.def
	$(SDF_TOOLS)/bin/sdf2parenthesize -i $< -o $@ -m Java-15 --omod jtree-parenthesize --lang Java-15

