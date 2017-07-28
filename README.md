[![Build Status](https://travis-ci.org/hdiv/hdiv.svg)](https://travis-ci.org/hdiv/hdiv)
[![Maven Central](https://img.shields.io/maven-central/v/org.hdiv/hdiv.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22hdiv%22)
[![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/hdiv/hdiv/master/LICENSE_HEADER)

![Hdiv](https://hdivsecurity.com/img/Hdiv-main.png)
> *New to Hdiv? Check [this](https://www.youtube.com/watch?v=f4UPPxYId4Q)* out

## Hdiv: Application Self-Protection
Hdiv is a leading provider of open source software for real-time, self-protected applications.  Hdiv solutions are built into applications during development to deliver the strongest available runtime application self-protection (RASP) against OWASP Top 10 threats. Since 2008, Hdiv has pioneered self-protection cyber security software, and today its solutions are used by leading commercial software providers and global enterprises in banking, government, retail, technology, and aerospace.

> Official Site: [https://hdivsecurity.com](http://hdivsecurity.com)
Online documentation: [https://hdivsecurity.com/docs/](https://hdivsecurity.com/docs/)
Community Technical documentation: [https://hdivsecurity.com/docs/installation/library-setup/](https://hdivsecurity.com/docs/installation/library-setup/)


`April 26th, 2017` - **[Hdiv v3.3.0 released!](https://github.com/hdiv/hdiv/releases)**

## How does Hdiv help?

Hdiv repels 90% of application security risks included in the OWASP Top 10—a broad consensus of the most critical web application security flaws —such as SQL injection, cross-site scripting, cross-site request forgery, data tampering, and brute force attacks. Hdiv offers higher effectiveness than any of the solutions currently available to fight web application security risks.    

- Want to ensure strong security without having to know and understand all current security threats.

- Want strong security without compromising application performance or the user experience.

- Want to build security into applications while in development, instead of having to go back and patch and tweak applications later.

## Editions

### Hdiv Community

Is an open-source web application security framework that includes read-only data protection and editable data risk mitigation. It is appropriate for customers who have less stringent security requirements but want protection from cyber threats.

### Hdiv Enterprise
Is a commercial all-in-one solution integrating the best of AST (Application Security Testing), RASP (Runtime application self protection), and WAF (Web application firewall) approaches with enterprise-class security, exclusive functionality, scalability, and enterprise-level support services. For more information, contact us at support@hdivsecurity.com

![Hdiv](https://hdivsecurity.com/img/technologies.png)

## Installation

### Spring MVC

![-](https://hdivsecurity.com/img/hdivInstallation.gif)

> https://www.youtube.com/watch?v=6-BNTh8AqQY

### Steps
Thanks to the new [extension point](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/support/RequestDataValueProcessor.html) included in Spring 3.1, Hdiv installation and configuration for Spring MVC is cleaner and easier than previously.

1. Add Hdiv Jars.

        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-config</artifactId>
            <version>3.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-spring-mvc</artifactId>
            <version>3.2.0</version>
        </dependency>

2. Add Hdiv listener and filter within `web.xml` file.

        <listener>
            <listener-class>org.hdiv.listener.InitListener</listener-class>
        </listener>
        
        <!-- Hdiv Validator Filter -->
        <filter>
            <filter-name>ValidatorFilter</filter-name>
            <filter-class>org.hdiv.filter.ValidatorFilter</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>ValidatorFilter</filter-name>
            <!-- Spring MVC Servlet name-->
            <servlet-name>SampleMvc</servlet-name>
        </filter-mapping>

3. Multipart configuration. Replace Spring MVC's `MultipartResolver` with the one from Hdiv. If `commons-fileupload` library is used for multipart processing:

        <bean id="multipartResolver" class="org.hdiv.web.multipart.HdivCommonsMultipartResolver">
            <property name="maxUploadSize" value="100000" />
        </bean>
        
    If Servlet 3 standard multipart processing is used:

        <bean id="multipartResolver" class="org.hdiv.web.multipart.HdivStandardServletMultipartResolver"></bean>

4. Editable data validation. In order to add editable validation errors into Spring MVC binding and validation errors, configure `hdivEditableValidator` as application wide validator.

        <mvc:annotation-driven validator="hdivEditableValidator"/>

If you are using a Spring MVC version prior to 3.1, it is necessary to introduce an additional step, replacing Spring MVC tlds with Hdiv tlds according to the specific version. The next example shows the code for Spring MVC 3.0.4:
        
        <jsp-config>
            <taglib>
                <taglib-uri>http://www.springframework.org/tags/form</taglib-uri>
                <taglib-location>/WEB-INF/tlds/hdiv-spring-form-3_0_4.tld</taglib-location>
            </taglib>
            <taglib>
                <taglib-uri>http://www.springframework.org/tags</taglib-uri>
                <taglib-location>/WEB-INF/tlds/hdiv-spring.tld</taglib-location>
            </taglib>
        </jsp-config>

### Spring MVC and Thymeleaf

Thanks to the implementation of [RequestDataValueProcessor](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/support/RequestDataValueProcessor.html) interface in Thymeleaf, Hdiv support is straightforward.
1. Follow configuration steps for Spring MVC installation.
2. Add Hdiv jar for Thymeleaf

        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-thymeleaf</artifactId>
            <version>3.2.0</version>
        </dependency>

### Grails

With the plugin architecture and [Hdiv Plugin](http://grails.org/plugin/hdiv) implementation supported by Grails, Hdiv installation is implemented using the BuildConfig.groovy configuration file adding the Hdiv plugin to it:

    compile ':hdiv:1.0-RC2'

### JSTL

1. Add Hdiv jars.

        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-config</artifactId>
            <version>3.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-jstl-taglibs-1.2</artifactId>
            <version>3.2.0</version>
        </dependency>

2. Add Hdiv Listener and Filter within `web.xml` file

        <listener>
            <listener-class>org.hdiv.listener.InitListener</listener-class>
        </listener>
        
        <!-- Hdiv Validator Filter -->
        <filter>
            <filter-name>ValidatorFilter</filter-name>
            <filter-class>org.hdiv.filter.ValidatorFilter</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>ValidatorFilter</filter-name>
            <!-- Spring MVC Servlet name-->
            <servlet-name>SampleMvc</servlet-name>
        </filter-mapping>
        
        <!-- Replace JSTL tld with Hdiv tld-->
        <jsp-config>
            <taglib>
                <taglib-uri>http://java.sun.com/jsp/jstl/core</taglib-uri>
                <taglib-location>/WEB-INF/tlds/hdiv-c.tld</taglib-location>
            </taglib>
        </jsp-config>

### Struts 1

1. Add Hdiv jars.

        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-config</artifactId>
            <version>3.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-struts-1</artifactId>
            <version>3.2.0</version>
        </dependency>

2. Add Hdiv listener and Filter within `web.xml` file.

        <!-- Hdiv Initialization listener -->
        <listener>
            <listener-class>org.hdiv.listener.InitListener</listener-class>
        </listener>
        
        <!-- Hdiv Validator Filter -->
        <filter>
            <filter-name>ValidatorFilter</filter-name>
            <filter-class>org.hdiv.filter.ValidatorFilter</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>ValidatorFilter</filter-name>
            <url-pattern>*.do</url-pattern>
        </filter-mapping>
        
        <!-- Replace Struts 1 tld with Hdiv tlds -->
        <jsp-config>
            <taglib>
                <taglib-uri>/WEB-INF/struts-html.tld</taglib-uri>
                <taglib-location>/WEB-INF/hdiv-html.tld</taglib-location>
            </taglib>
            <taglib>
                <taglib-uri>/WEB-INF/struts-nested.tld</taglib-uri>
                <taglib-location>/WEB-INF/hdiv-nested.tld</taglib-location>
            </taglib>
            <taglib>
                <taglib-uri>/WEB-INF/struts-logic.tld</taglib-uri>
                <taglib-location>/WEB-INF/hdiv-logic.tld</taglib-location>
            </taglib> 
        </jsp-config>

### Struts 2
The latest Struts 2 version supported by Hdiv is Struts 2.0.11. Higher versions are supported by Hdiv Enterprise Edition.
1. Add Hdiv jars.

        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-core</artifactId>
            <version>2.0.4</version>
        </dependency>
        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-struts-2.0.11</artifactId>
            <version>2.0.4</version>
        </dependency>

2. Add Hdiv Listener and Filter within `web.xml` file.

        <!-- Hdiv Initialization listener -->
        <listener>
            <listener-class>org.hdiv.listener.InitListener</listener-class>
        </listener>
        
        <!-- Hdiv Validator Filter -->
        
        <filter-mapping>
            <filter-name>ValidatorFilter</filter-name>
            <url-pattern>*.action</url-pattern>
        </filter-mapping>
        
        <filter-mapping>
            <filter-name>ValidatorFilter</filter-name>
            <url-pattern>*.jsp</url-pattern>
        </filter-mapping>		
        
        <!-- Replace Struts 2 tld with Hdiv tlds -->
        
        <jsp-config>
            <taglib>
                <taglib-uri>/struts-tags</taglib-uri>
                <taglib-location>/WEB-INF/hdiv-tags.tld</taglib-location>
            </taglib> 	
        </jsp-config>

### JSF
1. Add Hdiv jars.

        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-config</artifactId>
            <version>3.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.hdiv</groupId>
            <artifactId>hdiv-jsf</artifactId>
            <version>3.2.0</version>
        </dependency>

2. Add Hdiv listener, Hdiv Filter and define your custom faces-config (with Hdiv configuration, see the next point) within `web.xml` file.

        <!-- Define the hdiv-faces-config -->
        <context-param>
             <param-name>javax.faces.CONFIG_FILES</param-name>
             <param-value>/WEB-INF/hdiv-faces2-config.xml</param-value>
        </context-param>
        
        <!-- Hdiv Initialization listener -->
        <listener>
            <listener-class>org.hdiv.listener.InitListener</listener-class>
        </listener>
        
        <!-- Hdiv Validator Filter -->
        <filter>
            <filter-name>ValidatorFilter</filter-name>
            <filter-class>org.hdiv.filter.ValidatorFilter</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>ValidatorFilter</filter-name>
            <servlet-name>Faces Servlet</servlet-name>
        </filter-mapping>

3. This is the content of `/WEB-INF/hdiv-faces2-config.xml` file:

        <?xml version="1.0" encoding="UTF-8"?>
        <faces-config xmlns="http://java.sun.com/xml/ns/javaee"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-facesconfig_2_0.xsd"
            version="2.0">
            
            <component>
                <component-type>javax.faces.HtmlOutcomeTargetLink</component-type>
                <component-class>org.hdiv.components.HtmlOutcomeTargetLinkExtension</component-class>
            </component>
            <component>
                <component-type>javax.faces.HtmlOutcomeTargetButton</component-type>
                <component-class>org.hdiv.components.HtmlOutcomeTargetButtonExtension</component-class>
            </component>
            <component>
                <component-type>javax.faces.Parameter</component-type>
                <component-class>org.hdiv.components.UIParameterExtension</component-class>
            </component>
            <component>
                <component-type>javax.faces.HtmlInputHidden</component-type>
                <component-class>org.hdiv.components.HtmlInputHiddenExtension</component-class>
            </component>
            <component>
                <component-type>javax.faces.HtmlOutputLink</component-type>
                <component-class>org.hdiv.components.HtmlOutputLinkExtension</component-class>
            </component>
            <component>
                <component-type>javax.faces.HtmlCommandLink</component-type>
                <component-class>org.hdiv.components.HtmlCommandLinkExtension</component-class>
            </component>
            <component>
                <component-type>javax.faces.HtmlCommandButton</component-type>
                <component-class>org.hdiv.components.HtmlCommandButtonExtension</component-class>
            </component>
            
            <lifecycle>
                <phase-listener>org.hdiv.phaseListeners.ComponentMessagesPhaseListener</phase-listener>
                <phase-listener>org.hdiv.phaseListeners.ConfigPhaseListener</phase-listener>
            </lifecycle>
        
            <factory>
                <external-context-factory>org.hdiv.context.ExternalContextFactoryWrapper</external-context-factory>
                <exception-handler-factory>org.hdiv.exceptionHandler.HDIVExceptionHandlerFactory</exception-handler-factory>
            </factory>
            
        </faces-config>

## Configuration
Hdiv configuration is based on Spring configuration. Hdiv has a custom schema to reduce the configuration tasks. First of all we need a Spring configuration file to add Hdiv configuration.

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans" 
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
           xmlns:hdiv="http://www.hdiv.org/schema/hdiv" 
           xsi:schemaLocation="http://www.springframework.org/schema/beans 
                               http://www.springframework.org/schema/beans/spring-beans.xsd 
                               http://www.hdiv.org/schema/hdiv http://www.hdiv.org/schema/hdiv/hdiv.xsd">

From Hdiv version 2.1.7, it is possible to configure Hdiv in Java instead of XML.

    @Configuration
    @EnableHdivWebSecurity
    public class HdivSecurityConfig extends HdivWebSecurityConfigurerAdapter {
    
        @Override
        public void configure(SecurityConfigBuilder builder) {
    
            // Configuration options
        }
    }

All the configuration options are on the [technical documentation](https://hdivsecurity.com/docs/installation/library-setup/).
                
## How to build your own Hdiv jar
Clone this repository and build jar files (you'll need Git and Maven installed):

    git clone git://github.com/hdiv/hdiv.git
    cd hdiv
    mvn install

The jars will be created in a folder named "target" and installed in local Maven repository.

## Do you need help?

If you have questions or problems, please [open an issue](https://github.com/hdiv/hdiv/issues) on this repository (hdiv/hdiv) or contact us at support@hdivsecurity.com

## License
Hdiv is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

## Profiler
JProfiler is kindly supporting Hdiv open source project with its full-featured Java Profiler.
Take a look at JProfiler's leading software products: [Java Profiler](http://www.ej-technologies.com/products/jprofiler/overview.html)