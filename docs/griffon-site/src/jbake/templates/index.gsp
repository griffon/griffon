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
        href="https://griffon-framework.org/">Griffon</a> is desktop application development platform for the JVM.Inspired by <a
        href="https://grails.org/">Grails</a>, Griffon leverages concepts like
        Convention over Configuration, modularity, and freedom of choice. The framework
        at its core is written 100% in Java allowing developers to write their own applications
        with only Java. Other programming languages such as Groovy and Kotlin may be used too, in
        which case additional capabilities such as builders and extension methods become available.
    </p>

    <p>
        Griffon supports 4 different UI toolkits: Swing, JavaFX, Apache Pivot, and Lanterna.
    </p>

    <p>
        Griffon encourages the use of the MVC pattern but it's not limited to a single interpretation,
        you can for example apply standard MVC, MVP, MVVM, PMVC and others.
        Griffon also follows in the spirit of the Swing Application Framework (JSR 296), it defines
        a simple yet powerful application life cycle and event publishing mechanism regardless of
        the UI toolkit of choice.
    </p>

    <p>
        Seasoned Java developers should be able to pick up the pace quickly, as the framework
        relieves them from the burden of maintaining an application structure, allowing them to
        concentrate on getting the code right.
    </p>

    <div class="sect1">
    <h2 id="_quick_start"><i class="fa fa-bolt"></i> Quick Start</h2>
    <div class="sectionbody">
<div class="paragraph">
<p>The recommended way to get started with a <code>Griffon</code> project is to use a
<a href="https://maven.apache.org">Maven</a> archetype and
<a href="https://maven.apache.org">Maven</a> or <a href="https://gradle.org">Gradle</a>. You can install these tools with
<a href="https://sdkman.io/">SDKMAN</a>.</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ curl -s https://get.sdkman.io | bash
\$ sdk install maven
\$ sdk install gradle</code></pre>
</div>
</div>
<div class="paragraph">
<p>Griffon offers the following Maven archetypes which may be used to bootstrap
either Maven or Gradle projects:</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>griffon-javafx-groovy-archetype
griffon-javafx-java-archetype
griffon-lanterna-java-archetype
griffon-lanterna-groovy-archetype
griffon-pivot-java-archetype
griffon-pivot-groovy-archetype
griffon-swing-java-archetype
griffon-swing-groovy-archetype</code></pre>
</div>
</div>
<div class="paragraph">
<p>Select a starting archetype from the list and invoke the following command to create a project</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ mvn archetype:generate \\
      -DarchetypeGroupId=org.codehaus.griffon.maven \\
      -DarchetypeArtifactId=griffon-swing-groovy-archetype \\
      -DarchetypeVersion=${config.griffon_version_current} \\
      -DgroupId=org.example \\
      -DartifactId=console \\
      -Dversion=1.0.0-SNAPSHOT \\
      -Dgradle=true</code></pre>
</div>
</div>
<div class="paragraph">
<p>Compile, run and test the project with any of these commands</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ ./gradlew build
\$ ./gradlew test
\$ ./gradlew run</code></pre>
</div>
</div>
<div class="paragraph">
<p>You may use Maven as build tool</p>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ mvn archetype:generate \\
      -DarchetypeGroupId=org.codehaus.griffon.maven \\
      -DarchetypeArtifactId=griffon-swing-groovy-archetype \\
      -DarchetypeVersion=${config.griffon_version_current} \\
      -DgroupId=org.example \\
      -DartifactId=console \\
      -Dversion=1.0.0-SNAPSHOT</code></pre>
</div>
</div>
<div class="listingblock">
<div class="content">
<pre class="prettyprint"><code>\$ ./mvnw compile
\$ ./mvnw test
\$ ./mvnw -Prun</code></pre>
</div>
</div>
</div>
    </div>

        </div>

        <div class="col-sm-4">
        <a class="twitter-timeline"  href="https://twitter.com/theaviary"  data-widget-id="492016338076848130">Tweets by @theaviary</a>
    <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
        <br/><script type="text/javascript" src="https://www.openhub.net/p/16389/widgets/project_factoids_stats.js"></script><br/>
        <br/><script type="text/javascript" src="https://www.openhub.net/p/16389/widgets/project_languages.js"></script><br/>
        </div>
    </div>


<% include "footer.gsp" %>
