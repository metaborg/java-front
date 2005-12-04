include $(top_srcdir)/Makefile.xt

SUBDIRS=. v1.5
GNUCP_VERSION=0.19

# create GNU classpath testsuites
classpath-$(GNUCP_VERSION).tar.gz :
	wget ftp://ftp.gnu.org/gnu/classpath/classpath-$(GNUCP_VERSION).tar.gz

check_DATA=\
  classpath-$(GNUCP_VERSION).tar.gz

CLEANFILES=classpath-$(GNUCP_VERSION).tar.gz
