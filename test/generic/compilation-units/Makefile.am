CLEANFILES=$(wildcard *.jcs) $(wildcard *.jas) $(wildcard *.pp) $(wildcard *.abox) $(wildcard *.ast)

EXTRA_DIST = $(wildcard *.java)

check: ${top_builddir}/pretty-print/jtree2abox
	for file in $(wildcard *.java) ; do \
	   echo "------------- pretty-printing $$file" ;\
	   $(SGLR)/bin/sglr -2t  -p ${top_builddir}/grammar/generic/Generic-Java.tbl -i $$file | \
	   $(ASFIXTOOLS)/bin/implode-asfix -S -o $$file.ast ; \
		 ${top_builddir}/pretty-print/jtree2abox -i $$file.ast -o $$file.abox ; \
		 $(STRATEGO)/bin/abox-format -S -i $$file.abox -o /dev/null ; \
		 $(GPP)/bin/abox2text   -i $$file.abox -o $$file.pp ; \
	done ;

