include $(top_srcdir)/Makefile.xt

javafrontlib = \
  $(wildcard java/*.str) \
  $(wildcard java/pp/*.str) \
  $(wildcard java/pp/class/*.str) \
  $(wildcard java/pp/interface/*.str) \
  $(wildcard java/pp/type/*.str) \
  $(wildcard java/typematch/*.str)

javafrontlibextra = \
  $(wildcard java/*.meta) \
  $(wildcard java/pp/*.meta) \
  $(wildcard java/pp/class/*.meta) \
  $(wildcard java/pp/interface/*.meta) \
  $(wildcard java/pp/type/*.meta) \
  $(wildcard java/typematch/*.meta)
 

BUILT_SOURCES = \
	java/typematch/common.str
	
	
STRINCLUDES = -I $(GPP)/share/sdf/gpp

nobase_pkgdata_DATA = $(javafrontlib:.str=.rtree)
EXTRA_DIST = $(javafrontlib) $(javafrontlibextra)
CLEANFILES = $(nobase_pkgdata_DATA)

Java-15.rtg :
	$(LN_S) $(top_builddir)/syn/v1.5/Java-15.rtg $@

java/typematch/common.str : Java-15.rtg
	$(STRATEGO_REGULAR)/bin/rtg2typematch --module "java/typematch/common" -i Java-15.rtg -o java/typematch/common.str 
