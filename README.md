![Griffon][logo]
===============================

[Griffon](http://griffon.codehaus.org) is a [Grails](http://grails.org) inspired 
development platform for developing desktop applications in the JVM.

Griffon follows the Convention over Configuration paradigm, paired with an 
intuitive MVC architecture and a command line interface. Griffon also follows 
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
   * LICENSE - project license (ASL 2.0)
   * README.md - this file
   * archetypes - bundled application archetypes
   * bash - griffon command autocompletion script
   * bin - griffon launchers
   * conf - griffon command classloading configuration
   * dist - current griffon buildtime and runtime distribution
   * doc - miscellaneous documentation
   * guide - the Griffon Guide
      ** api - framework javadocs
   * lib - buildtime dependencies
   * media - griffon images and icons
   * samples
      ** FileViewer - simple file contents visualizer
      ** FontPicker - displays available system fonts and preview
      ** Greet - the 1st Griffon application ever: a Twitter client
      ** GroovyEdit - simple multi-tabbed file editor
      ** SwingPad - visual editor for View scripts
      ** Weatherwidget - showcases animations and network calls
   * scripts - minimal set of griffon commands
   * src - application and artifact templates

Links
-----

* [Home](http://griffon.codehaus.org)
* [Documentation](http://griffon.codehaus.org/Documentation)
* [Issue Tracker](http://jira.codehaus.org/browse/griffon)
* [Source](https://github.com/griffon/griffon)
* [Mailing Lists](http://griffon.codehaus.org/)


[logo]: http://media.xircles.codehaus.org/_projects/griffon/_logos/medium.png