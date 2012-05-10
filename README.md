![Griffon][logo]
===============================

[Griffon][1] is dekstop application development platform for the JVM. Inspired
by [Grails][2], Griffon leverages the use of of the [Groovy][3] language and
concepts like Convention over Configuration. The Swing toolkit is the default
UI toolkit of choice however others may be used, principaly SWT and JavaFX.

Griffon also allows developers to use a combination of the Groovy and Java
languages to write their applications. If this were not enough, Griffon allows
the usage of other JVM languages such as Scala, Clojure, Mirah and Jython too.

The capabilities of the platform can be extended by installing plugins; there
are many options to choose from as appreciated at the [Griffon Central Repository][4]

Griffon encourages the use of the MVC pattern. Griffon also follows in
the spirit of the Swing Application Framework (JSR 296), it defines a simple 
yet powerful application life cycle and event publishing mechanism. Another 
interesting feature comes from the Groovy language itself: automatic property 
support and property binding (inspired by BeansBinding (JSR 295)), which makes 
creating observable beans and binding to their properties a snap! As if 
property binding was not enough Groovy's SwingBuilder also simplifies building 
multi-threaded applications, say goodbye to the ugly gray rectangle (the bane 
of Swing apps)!

Grails developers should feel right at home when trying out Griffon. Many of 
Grails' conventions and commands are shared with Griffon. Granted, Swing is not
the same as HTML/GSP but Builders simplify the task of creating the UI.

Seasoned Java developers will also be able to pick up the pace quickly, as the
framework relieves you of the burden of maintaining an application structure,
allowing you to concentrate on getting the code right.

Directory Structure
-------------------

The Griffon installation structure is defined as follows

   * INSTALL - install instructions
   * LICENSE - project license (Apache Software License 2.0)
   * README.md - this file
   * bash - griffon command autocompletion script
   * bin - griffon launchers
   * conf - griffon command classloading configuration
   * dist - current griffon buildtime and runtime distribution
   * doc - miscellaneous documentation
   * guide - the Griffon Guide
      * api - framework javadocs
   * lib - buildtime dependencies
   * media - griffon images and icons
   * samples
      * FileViewer - simple file contents visualizer
      * FontPicker - displays available system fonts and preview
      * Greet - the 1st Griffon application ever: a Twitter client
      * GroovyEdit - simple multi-tabbed file editor
      * SwingPad - visual editor for View scripts
      * Weatherwidget - showcases animations and network calls
   * scripts - minimal set of griffon commands

Links
-----

* [Home][1]
* [Documentation][5]
* [Issue Tracker][6]
* [Source][7]
* [Mailing Lists][1]


[logo]: http://media.xircles.codehaus.org/_projects/griffon/_logos/medium.png
[1]: http://griffon.codehaus.org
[2]: http://grails.org
[3]: http://groovy.codehaus.org
[4]: http://artifacts.griffon-framework.org
[5]: http://griffon.codehaus.org/Documentation
[6]: http://jira.codehaus.org/browse/griffon
[7]: https://github.com/griffon/griffon
