Requirements
------------

 * Griffon @griffon.version@
 * JDK 1.6+

Troubleshooting
---------------

It's very likely the build will complain that some plugins can't be resolved.
Specify the following flag when compiling/running the app for the first time

    griffon -Dgriffon.artifact.force.upgrade=true compile

Notice
------

Contains code from [http://code.google.com/p/gturtle/][1] used with explicit
permission from Eitan Suez:

"I hereby grant the Griffon project and its developers free license to use any
of the code in gturtle for use in Griffon or Griffon samples or SwingPad for
whatever purposes it would like, including the right to redistribute parts of
the code under the ASL2 license."

[1]: http://code.google.com/p/gturtle/
