include $(top_srcdir)/Makefile.xt

SUBDIRS=. v1.5
GNUCP_VERSION=0.12

# create GNU classpath testsuites
classpath-$(GNUCP_VERSION).tar.gz :
	wget http://ftp.surfnet.nl/gnu/classpath/classpath-$(GNUCP_VERSION).tar.gz

check_DATA=\
  classpath-$(GNUCP_VERSION).tar.gz

CLEANFILES=classpath-$(GNUCP_VERSION).tar.gz
