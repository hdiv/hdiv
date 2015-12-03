# Debugging HDIV

There's a sample application that uses HDIV at https://github.com/hdiv/hdiv-spring-mvc-showcase. You can use it to test and debug HDIV itself.

Once you check out its sources, make sure that the HDIV version it's packaged against matches your HDIV sources. You should modify the sample application's POM accordingly.
We'll assume that you have already downloaded and installed the latest sources from the HDIV GitHub repository. As of 2015-09-20, the latest HDIV snapshot is `2.2.0-SNAPSHOT`, so you should set the sample application's POM to:

    <properties>
        [...]
        <org.hdiv-version>2.2.0-SNAPSHOT</org.hdiv-version>
    </properties>

Now you can package the sample application with `mvn package` and run it with `mvn tomcat:run`.

**NOTE:** Make sure your HDIV source snapshot has been previously installed with `mvn install`, or the above commands will fail.

`mvn tomcat:run` will run the sample application normally. If you use **`mvnDebug tomcat:run`** instead, Tomcat will wait until a debugger attaches to it. In the following section, we'll explain how to do that with Eclipse.

### Debugging with Eclipse

First, load the HDIV project into Eclipse. I assume you have the [m2e](http://www.eclipse.org/m2e/) plugin installed. After checking out the HDIV source code, go to "File > Import > Maven > Existing Maven projects...". Then browse to root directory where you placed the HDIV sources.

You now have a development environment well provisioned to dig into the HDIV source code. Make all the changes you want, and install HDIV as usual with `mvn install`.

Now it's time to debug HDIV. From the command line, navigate to the folder where you placed the *hdiv-spring-mvc-showcase* test application's sources. Package it as explained above (run `mvn package`) and then run `mvnDebug tomcat:run`. You should see an output like the following:

```
Preparing to Execute Maven in Debug Mode
Listening for transport dt_socket at address: 8000
```

Tomcat is now waiting a remote debugger to attach to itself, and will go no further until that happens. You now have to set up Eclipse to attach to a remote process. Go to "Run > Debug Configurations..." and create a new debugging session under the category "Remove Java Application". Set "Connection type" to "Standard (Socket Attach)", and the host and port to "localhost" and "8000", respectively. The port should be the same that appeared in the above `mvnDebug` output. It's usually 8000, but make sure they're the same. If you want to be able to debug the sample application as well, go to the "Sources" tab, expand "Default", and add the project. If you only want to debug HDIV sources, you don't have to do anything. This is enough in most of the cases.

When you finally hit "Debug", Eclipse will attach to the Tomcat process and the execution will continue. Now you can place breakpoints and do anything else you would do when debugging a standard Java application. When you've finished your work, click "Run > Disconnect" to detach from Tomcat. At this point, Tomcat will pause the execution again, waiting for a remote debugger to attach. You can re-attach, or you can just press Ctrl-C to stop it.


# Troubleshooting

### `mvn install` fails

After checking out the sources, you might get this error when you run `mvn install` for the first time:

    [INFO] ------------------------------------------------------------------------
    [ERROR] BUILD ERROR
    [INFO] ------------------------------------------------------------------------
    [INFO] Error building POM (may not be this project's POM).
    
    
    Project ID: com.googlecode.concurrentlinkedhashmap:concurrentlinkedhashmap-lru:jar:1.2_jdk5
    
    Reason: Cannot find parent: org.sonatype.oss:oss-parent for project: com.googlecode.concurrentlinkedhashmap:concurrentlinkedhashmap-lru:jar:1.2_jdk5 for project com.googlecode.concurrentlinkedhashmap:concurrentlinkedhashmap-lru:jar:1.2_jdk5

You need Maven 3 or later to build HDIV. If you use an older version, the build will fail with the above error.

In Windows, just make sure you download the latest executable from http://maven.apache.org.

In Debian and other derivatives, Maven 3 is provided by the package `maven` (3.0.5-3 in Debian 8 *stable* and 3.3.3-3 in *testing*). This conflicts with many users who will likely have `maven2` instead. Thus, make sure you upgrade to the latest version.


