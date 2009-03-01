eventPrepareIzpackInstallerEnd = { ->
    ant.copy( todir: "${basedir}/installer/izpack/resources", overwrite: true ) {
        fileset( dir: "${basedir}/src/installer/izpack/resources", includes: "**" )
    }
    ant.replace( dir: "${basedir}/installer/izpack/resources" ) {
        replacefilter(token: "@app.name@", value: griffonAppName)
        replacefilter(token: "@app.version@", value: griffonAppVersion)
    }
}

eventCopyLibsEnd = { jardir ->
   ["flamingo","tray","macwidgets","swingxtras"].each { dir ->
      ant.copy(todir: "${jardir}/${dir}") {
         fileset(dir: "${basedir}/lib/${dir}", includes:"*.jar")
      }
   }
}
