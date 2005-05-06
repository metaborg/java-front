include $(top_srcdir)/Makefile.xt

javafrontlib = \
  $(wildcard $(srcdir)/java/*.str) \
  $(wildcard $(srcdir)/java/pp/*.str) \
  $(wildcard $(srcdir)/java/pp/class/*.str) \
  $(wildcard $(srcdir)/java/pp/interface/*.str) \
  $(wildcard $(srcdir)/java/pp/type/*.str)

javafrontlibextra = \
  $(wildcard $(srcdir)/java/*.meta) \
  $(wildcard $(srcdir)/java/pp/*.meta) \
  $(wildcard $(srcdir)/java/pp/class/*.meta) \
  $(wildcard $(srcdir)/java/pp/interface/*.meta) \
  $(wildcard $(srcdir)/java/pp/type/*.meta) \
  $(wildcard $(srcdir)/java/typematch/*.meta)
  
javafrontlibgen = \
  java/typematch/common.str

BUILT_SOURCES = \
  java/typematch/common.str

STRINCLUDES = -I $(GPP)/share/sdf/gpp

nobase_pkgdata_DATA = $(javafrontlib:.str=.rtree) $(javafrontlibgen:.str=.rtree)
EXTRA_DIST = $(javafrontlib) $(javafrontlibextra)
CLEANFILES = $(nobase_pkgdata_DATA)

Java-15.rtg :
	$(LN_S) $(top_builddir)/syn/v1.5/Java-15.rtg $@

java/typematch/common.str : Java-15.rtg
	mkdir -p java/typematch
	$(STRATEGO_REGULAR)/bin/rtg2typematch --module "java/typematch/common" -i $< -o $@
