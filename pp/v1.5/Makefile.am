include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

LOCAL_PP_MODULES = \
  jtree15-to-abox.str \
  concrete-syntax.str

libexec_PROGRAMS = \
  jtree15-to-abox \
  jtree15-parenthesize

EXTRA_DIST = \
  $(LOCAL_PP_MODULES) \
  $(wildcard *.meta)

CLEANFILES = \
  $(wildcard *.dep) \
  $(wildcard *.c) \
  jtree15-parenthesize.str

SCFLAGS  = --main io-$* 
STRINCLUDES = \
  -I $(top_builddir)/sig \
  -I $(top_srcdir)/lib \
  -I $(GPP)/share/sdf/gpp \
  -I $(GPP)/share/gpp

LDADD += $(SSL_LIBS)

nodist_jtree15_parenthesize_SOURCES = jtree15-parenthesize.c
nodist_jtree15_to_abox_SOURCES = jtree15-to-abox.c

jtree15-parenthesize.str : $(top_builddir)/syn/v1.5/Java-15.def
	$(SDF_TOOLS)/bin/sdf2parenthesize -i $< -o $@ -m Java-15 --omod jtree15-parenthesize --lang Java-15
