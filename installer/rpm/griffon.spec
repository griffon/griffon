%define is_mandrake %(test -e /etc/mandrake-release && echo 1 || echo 0)
%define is_suse %(test -e /etc/SuSE-release && echo 1 || echo 0)
%define is_fedora %(test -e /etc/fedora-release && echo 1 || echo 0)

%define dist redhat
%define disttag rh

%if %is_mandrake
%define dist mandrake
%define disttag mdk
%endif
%if %is_suse
%define dist suse
%define disttag suse
%define kde_path /opt/kde3
%endif
%if %is_fedora
%define dist fedora
%define disttag rhfc
%endif

%define _bindir         %kde_path/bin
%define _datadir        %kde_path/share
%define _iconsdir       %_datadir/icons
%define _docdir         %_datadir/doc
%define _localedir      %_datadir/locale
%define qt_path         /usr/lib/qt3

%define distver %(release="`rpm -q --queryformat='%{VERSION}' %{dist}-release 2> /dev/null | tr . : | sed s/://g`" ; if test $? != 0 ; then release="" ; fi ; echo "$release")
%define distlibsuffix %(%_bindir/kde-config --libsuffix 2>/dev/null)
%define _lib lib%distlibsuffix
%define packer %(finger -lp `echo "$USER"` | head -n 1 | cut -d: -f 3)

Name:           griffon
Version:        @griffon.version.rpm@
Release:        1
License:        Apache Software License v2
Provides:       griffon
BuildRoot:      %{_tmppath}/%{name}-%{version}-build
Group:          Development/Frameworks/Griffon
Summary:        Griffon is an agile development framework for Java Swing applications
Source:         griffon-@griffon.version@-bin.zip
BuildArch:      noarch
Packager: 	The Griffon team <dev@griffon.codehaus.org>

%description
Griffon is a Grails like application framework for developing desktop applications in Groovy.

%prep
%setup -n griffon-@griffon.version@
rm bin/*.bat

%build
echo "nothing to compile"

%install
install -d $RPM_BUILD_ROOT/usr/local/share/griffon/
for entry in `ls .`
do
    if (test -d ${entry}) then
        if (${entry} -ne archetypes guide samples src) then
            install -d $RPM_BUILD_ROOT/usr/local/share/griffon/$entry
            install -p ${entry}/* $RPM_BUILD_ROOT/usr/local/share/griffon/$entry/
        else
            cp -r ${entry} $RPM_BUILD_ROOT/usr/local/share/griffon/$entry
        fi
    fi
    if (test -f ${entry}) then
        install -p ${entry} $RPM_BUILD_ROOT/usr/local/share/griffon/$entry
    fi
done

install -d $RPM_BUILD_ROOT/etc/profile.d
echo "export GRIFFON_HOME=/usr/local/share/griffon" >$RPM_BUILD_ROOT/etc/profile.d/griffon.sh
echo "setenv GROOVY_HOME /usr/local/share/griffon" >$RPM_BUILD_ROOT/etc/profile.d/griffon.csh

%clean
rm -rf "$RPM_BUILD_ROOT"

%post
/sbin/ldconfig

%postun
/sbin/ldconfig

%files
%defattr(-,root,root)
/etc/profile.d/*
/usr/*

%changelog
