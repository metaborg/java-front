include $(top_srcdir)/Makefile.xt

nobase_pkgdata_DATA = libjava-front.rtree $(javafrontlib:.str=.rtree)
lib_LTLIBRARIES     = libjava-front.la
EXTRA_DIST          = $(javafrontlibsrc) $(javafrontlibsrcextra)
CLEANFILES          = $(nobase_pkgdata_DATA) libjava-front.c $(javafrontlibgen)

nodist_libjava_front_la_SOURCES  = libjava-front.c
libjava_front_la_CPPFLAGS = $(STRATEGO_LIB_CFLAGS) $(STRATEGO_RUNTIME_CFLAGS) $(ATERM_CFLAGS)
libjava_front_la_LDFLAGS  = -avoid-version -no-undefined
libjava_front_la_LIBADD   = $(XTC)/lib/libstratego-xtc.la $(STRATEGO_LIB_LIBS) $(STRATEGO_RUNTIME_LIBS) $(ATERM_LIBS)

javafrontlib = $(javafrontlibsrc) $(javafrontlibgen)

javafrontlibsrc = \
  javafront.str \
  $(wildcard $(srcdir)/java/*.str) \
  $(wildcard $(srcdir)/java/pp/*.str) \
  $(wildcard $(srcdir)/java/pp/class/*.str) \
  $(wildcard $(srcdir)/java/pp/interface/*.str) \
  $(wildcard $(srcdir)/java/pp/type/*.str)

javafrontlibsrcextra = \
  $(wildcard $(srcdir)/java/*.meta) \
  $(wildcard $(srcdir)/java/pp/*.meta) \
  $(wildcard $(srcdir)/java/pp/class/*.meta) \
  $(wildcard $(srcdir)/java/pp/interface/*.meta) \
  $(wildcard $(srcdir)/java/pp/type/*.meta) \
  $(wildcard $(srcdir)/java/typematch/*.meta)

javafrontlibgen = \
  java/signature/v5.str \
  java/typematch/common.str \
  java/pp/parenthesize.str

BUILT_SOURCES = \
  java/typematch/common.str

STRINCLUDES = -I $(GPP)/share/sdf/gpp -I $(GPP)/share/gpp -I $(top_builddir)/lib -I $(top_srcdir)/lib

libjava-front.rtree libjava-front.c : $(javafrontlib:.str=.rtree)
	@$(STRC)/bin/strc -c --library -i $< -o libjava-front.rtree $(STRINCLUDES)
	rm libjava-front.str

java/typematch/common.str :
	mkdir -p java/typematch
	$(STRATEGO_REGULAR)/bin/rtg2typematch --module "java/typematch/common" -i $(top_builddir)/syn/v1.5/Java-15.rtg -o $@

java/signature/v5.rtg :
	mkdir -p java/signature
	rm -f $@
	$(LN_S) @abs_top_builddir@/syn/v1.5/Java-15.rtg $@

java/pp/parenthesize.str : $(top_builddir)/syn/v1.5/Java-15.def
	$(SDF_TOOLS)/bin/sdf2parenthesize -i $< -o $@ \
	  -m Java-15 \
	  --omod java/pp/parenthesize \
	  --sig-module java/signature/v5 \
	  --main-strategy io-java5-parenthesize \
	  --lang Java5
