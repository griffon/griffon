Requirements
------------

 * Griffon @griffon.version@
 * JDK 1.6+

Troubleshooting
---------------

It's very likely the build will complain that some plugins can't be resolved.
Specify the following flag when compiling/running the app for the first time

    griffon -Dgriffon.artifact.force.upgrade=true compile

