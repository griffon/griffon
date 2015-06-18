<% include "header.gsp" %>

<% include "menu.gsp" %>

<div class="container">
    <div class="page-header">
         <p align="center"><img src="/img/griffon-banner-large.png"
                           alt="griffon banner"></p>
    </div>

    <div class="row">
        <div class="col-md-8">

    <p><a
        href="http://griffon-framework.org/">Griffon</a> is desktop application development platform for the JVM.Inspired by <a
        href="http://grails.org/">Grails</a>, Griffon leverages the use of the <a
        href="http://groovy-lang.org/">Groovy</a> language and concepts like Convention over
    Configuration. The Swing toolkit is the default UI toolkit of choice however others may be used,
    for example JavaFX.
    </p>

    <p>
        Griffon encourages the use of the MVC pattern. Griffon also follows in the spirit of
        the Swing Application Framework (JSR 296), it defines a simple yet powerful application
        life cycle and event publishing mechanism. Another interesting feature comes from the
        Groovy language itself: automatic property support and property binding (inspired by
        BeansBinding (JSR 295)), which makes creating observable beans and binding to their
        properties a snap! As if property binding was not enough Groovy’s SwingBuilder also
        simplifies building multi-threaded applications, say goodbye to the ugly gray rectangle
        (the bane of Swing apps)!
    </p>

    <p>
        Grails developers should feel right at home when trying out Griffon. Many of Grails’
        conventions and commands are shared with Griffon. Granted, Swing is not the same as
        HTML/GSP but Builders simplify the task of creating the UI.
    </p>

    <p>
        Seasoned Java developers will also be able to pick up the pace quickly, as the framework
        relieves you of the burden of maintaining an application structure, allowing you to
        concentrate on getting the code right.
    </p>

    <div class="sect1">
    <h2 id="_quick_start"><i class="fa fa-bolt"></i> Quick Start</h2>
    <div class="sectionbody">
<div class="paragraph">
<p>The recommended way to get started with a <code>Griffon</code> project is to use a
<a href="http://github.com/pledbrook/lazybones">Lazybones</a> project template and
<a href="http://gradle.org">Gradle</a>. You can install these tools with
<a href="http://gvmtool.net">GVM</a>.</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ curl -s get.gvmtool.net | bash
\$ gvm install lazybones
\$ gvm install gradle</code></pre>
</div>
</div>
<div class="paragraph">
<p>Next register the <code>griffon-lazybones-templates</code> repository with Lazybones'
config file. Edit <code>\$USER_HOME/.lazybones/config.groovy</code></p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint groovy language-groovy"><code>bintrayRepositories = [
    "griffon/griffon-lazybones-templates",
    "pledbrook/lazybones-templates"
]</code></pre>
</div>
</div>
<div class="paragraph">
<p>List all available templates by invoking the following command</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ lazybones list
Available templates in griffon/griffon-lazybones-templates

    griffon-javafx-groovy
    griffon-javafx-java
    griffon-lanterna-groovy
    griffon-lanterna-java
    griffon-pivot-groovy
    griffon-pivot-java
    griffon-plugin
    griffon-swing-groovy
    griffon-swing-java</code></pre>
</div>
</div>
<div class="paragraph">
<p>Select a starting template from the list and invoke the <code>create</code> command</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ lazybones create griffon-javafx-java sample-javax-java</code></pre>
</div>
</div>
<div class="paragraph">
<p>Compile, run and test the project with any of these commands</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ gradle build
\$ gradle test
\$ gradle run</code></pre>
</div>
</div>
<div class="paragraph">
<p>You may use Maven as an alternate build tool</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ mvn compile
\$ mvn test
\$ mvn -Prun</code></pre>
</div>
</div>
</div>
    </div>

        </div>

        <div class="col-sm-4">
        <a class="twitter-timeline"  href="https://twitter.com/theaviary"  data-widget-id="492016338076848130">Tweets by @theaviary</a>
    <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
        <br/><script type="text/javascript" src="http://www.openhub.net/p/16389/widgets/project_factoids_stats.js"></script><br/>
        <br/><script type="text/javascript" src="http://www.openhub.net/p/16389/widgets/project_languages.js"></script><br/>
        </div>
    </div>


<% include "footer.gsp" %>