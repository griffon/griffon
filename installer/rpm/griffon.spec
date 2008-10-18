Name:           griffon
Version:        @griffon.version.rpm@
Release:        1
License:        Apache Software License v2
Provides:       griffon
BuildRoot:      %{_tmppath}/%{name}-%{version}-build
Group:          Development/Frameworks/Griffon
Summary:        Griffon is an agile development framework for Java Swing applications
Source:         http://dist.groovy.codehaus.org/distributions/griffon/griffon-src-@griffon.version@.zip
BuildArch:      noarch
BuildRequires:  unzip
Packager: 	The Griffon team <dev@griffon.codehaus.org>

%description
Griffon is a Grails like application framework for developing desktop applications in Groovy.

%prep
%setup -n griffon-@griffon.version@
rm bin/*.bat

%build

%install

install -d $RPM_BUILD_ROOT/usr/local/share/griffon/bin
install -p bin/* $RPM_BUILD_ROOT/usr/local/share/griffon/bin
install -d $RPM_BUILD_ROOT/usr/local/share/griffon/conf
install -p conf/* $RPM_BUILD_ROOT/usr/local/share/griffon/conf
install -d $RPM_BUILD_ROOT/usr/local/share/griffon/dist
install -p dist/* $RPM_BUILD_ROOT/usr/local/share/griffon/dist
install -d $RPM_BUILD_ROOT/usr/local/share/griffon/lib
install -p lib/* $RPM_BUILD_ROOT/usr/local/share/griffon/lib
install -d $RPM_BUILD_ROOT/usr/local/share/griffon/scripts
install -p scripts/* $RPM_BUILD_ROOT/usr/local/share/griffon/scripts

install -d $RPM_BUILD_ROOT/usr/local/share/griffon/ant
cp -r ant $RPM_BUILD_ROOT/usr/local/share/griffon/ant
install -d $RPM_BUILD_ROOT/usr/local/share/griffon/samples
cp -r samples $RPM_BUILD_ROOT/usr/local/share/griffon/samples

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
