# TommyBox
[![License MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](https://github.com/xnbox/tommybox/blob/master/LICENSE)

<h3>About:</h3>
<p><strong>TommyBox</strong> is a standalone executable container that makes it possible to launch static and dynamic web apps on a desktop by providing built-in server and browser functionality.
<br>
TommyBox is similar to Electron and NW.js.</p>

<p>
An app can be provided as a directory or packed as <abbr title="Web application ARchive">WAR</abbr> (or ZIP) archive that can contain <abbr title="Java Server Pages">JSP</abbr>, servlets, and static stuff like CSS, JavaScript, etc.
</p>

<p>
Under the hood, TommyBox is built on top of <a href="https://github.com/xnbox/tommy">Tommy</a> web server and SWT browser widget.
App can be packed as <abbr title="Web application ARchive">WAR</abbr> or ZIP archive and can optionally contain <abbr title="Progressive Web Apps">PWA</abbr> manifest</li>, <abbr title="Java Server Pages">JSP</abbr>, servlets and all static stuff like CSS, JavaScript files etc.
</p>

<p>
See TommyBox in action: <a href="https://github.com/xnbox/tommybox_demo">https://github.com/xnbox/tommybox_demo</a>
</p>

<h3>Download:</h3>
Latest release: <a href="https://github.com/xnbox/tommybox/releases/download/2.14.1/tb-2.14.1.jar">tb-2.14.1.jar</a>


<h3>Features:</h3>
<ul>
    <li>Single cross-platform executable jar (starts from ~25Mb)</li>
    <li>No dependencies</li>
    <li>Doesn't have configuration files, instead, TommyBox uses standard standard <abbr title="Progressive Web Apps">PWA</abbr> webmanifest and standard Tomcat configuration files</li>
    <li>
        Operating systems:
        <ul>
            <li>Linux</li>
            <li>macOS</li>
            <li>Windows</li>
        </ul>
    </li>
    <li>
        Architectures:
        <ul>
            <li>x86_64</li>
            <li>win32-x86_64</li>
            <li>aarch64</li>
            <li>ppc64le</li>
        </ul>
    </li>
	<li>Supports custom command line args</li>
    <li>
        Configurable display modes:
        <ul>
            <li>in-window</li>
            <li>in-browser</li>
            <li>fullscreen</li>
            <li>headless</li>
        </ul>
    </li>
    <li>Single and multiple windows modes</li>
    <li>Optional custom splash screen</li>
    <li>Optional custom context menu</li>
    <li>Optional custom system tray icon with custom menu</li>
</ul>

<h3>Supported web apps:</h3>
<ul>
	<li>WAR files</li>
	<li>Web apps packed as ZIP archives (including standard password-protected ZIPs)</li>
	<li>Exploded web apps (local directories)</li>
	<li>Remote WAR / ZIP files (HTTP)</li>
	<li>Embedded WAR / ZIP files and directories</li>
</ul>

<h3>Command line:</h3>


```text
java -jar tb.jar [options] [custom arg1] [custom arg2] ...

Options:
  --help                   print help message
  --app <file | dir | URL> run app from ZIP (or WAR) archive, directory or URL
  --password <password>    provide password (for encrypted ZIP (or WAR) archive)

```


<h3>Run app:</h3>


Run ZIP (or WAR) file:
```bash
java -jar tb.jar --app MyKillerApp.war
```


Run ZIP (or WAR) file with custom command-line args:
```bash
java -jar tb.jar --app MyKillerApp.war myparam1 myparam2 ...
```


Run ZIP (or WAR) from web server:
```bash
java -jar tb.jar --app https://example.com/MyKillerApp.zip
```


Run exploded web app from directory:
```bash
java -jar tb.jar --app MyKillerAppDir
```


Run password-protected ZIP (or WAR) archive:
```bash
java -jar tb.jar --app MyKillerApp.zip --password mysecret
```


<h3>Embed app:</h3>
<ul>
    <li>Option 1. Copy your app content into the <code>/app</code> directory of the <code>tb.jar</code>.
    </li>
    <li>Option 2. Pack your app as <code>app.war</code> or <code>app.zip</code> (the archive can be encrypted) and copy the archive to the root directory of the <code>tb.jar</code>.
    </li>
</ul>

Brand your app by renaming the <code>tb.jar</code> to the <code>MyKillerApp.jar</code>.


Run embedded app:
```bash
java -jar MyKillerApp.jar
```


Run embedded app with custom command-line args:
```bash
java -jar MyKillerApp.jar myparam1 myparam2 ...
```


Run password-protected embedded app:
```bash
java -jar MyKillerApp.jar --password mysecret
```


Run password-protected embedded app with custom command-line args:
```bash
java -jar MyKillerApp.jar --password mysecret myparam1 myparam2 ...
```


<h3>Access to the custom command-line args and system streams programmatically (JNDI):</h3>


```java

// ...somewhere in your Servlet or JSP

InitialContext ctx = new InitialContext();

/* get custom command-line args */
String[] args = (String[]) ctx.lookup("java:comp/env/tommy/args");

/* get standard input (stdin) */
InputStream stdin = (InputStream) ctx.lookup("java:comp/env/tommy/stdin");

/* get standard output (stdout) */
PrintStream stdout = (PrintStream) ctx.lookup("java:comp/env/tommy/stdout");

/* get standard error (stderr) */
PrintStream stderr = (PrintStream) ctx.lookup("java:comp/env/tommy/stderr");

/* get "--app" parameter value */
String app = (String) ctx.lookup("java:comp/env/tommy/app");

// ...


```



<h3>F.A.Q.</h3>

<strong>Q.</strong> My app failed with <code>java.lang.ClassNotFoundException: javax.servlet.\*</code>
<br>
<strong>A.</strong> As a result of the move from Java EE to Jakarta EE, starting from v10, Apache Tomcat supports only the Jakarta EE spec. <code>javax.servlet.\*</code> is no longer supported.
Replace the <code>javax.servlet.\*</code> imports in your code with <code>jakarta.servlet.\*</code>.

