include $(top_srcdir)/Makefile.xt

javafrontlib = \
  $(wildcard java/*.str) \
  $(wildcard java/pp/*.str) \
  $(wildcard java/pp/class/*.str) \
  $(wildcard java/pp/interface/*.str) \
  $(wildcard java/pp/type/*.str)

javafrontlibextra = \
  $(wildcard java/*.meta) \
  $(wildcard java/pp/*.meta) \
  $(wildcard java/pp/class/*.meta) \
  $(wildcard java/pp/interface/*.meta) \
  $(wildcard java/pp/type/*.meta)

STRINCLUDES = -I $(GPP)/share/sdf/gpp

nobase_pkgdata_DATA = $(javafrontlib:.str=.rtree)
EXTRA_DIST = $(javafrontlib) $(javafrontlibextra)
CLEANFILES = $(nobase_pkgdata_DATA)
