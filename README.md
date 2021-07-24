# TommyBox
[![License MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](https://github.com/xnbox/tommybox/blob/master/LICENSE)

<h3>About:</h3>
<p><strong>tommybox</strong> is a standalone executable container that makes it possible to launch a static and dynamic web apps on a desktop by providing built-in server and browser functionality.</p>

<p>
An app can be provided as a directory or packed as <abbr title="Web application ARchive">WAR</abbr> (or ZIP) archive that can contain <abbr title="Java Server Pages">JSP</abbr>, servlets, and static stuff like CSS, JavaScript, etc.
</p>

<p>
Under the hood TommyBox build on top of <a href="https://github.com/xnbox/tommy">Tommy</a> web server and SWT browser widget.
App can be packed as <abbr title="Web application ARchive">WAR</abbr> or ZIP archive and optionally contain <abbr title="Progressive Web Apps">PWA</abbr> manifest</li>, <abbr title="Java Server Pages">JSP</abbr>, servlets and all static stuff like CSS, JavaScript files etc.
</p>


<h3>Download:</h3>
Latest release: <a href="https://github.com/xnbox/tommybox/releases/download/2.14.1/tb-2.14.1.jar">tb-2.14.1.jar</a>


<h3>Features:</h3>
<ul>
	<li>Single cross-platform executable jar (starts from ~25Mb)</li>
	<li>No dependencies</li>
	<li>No own configuration files. TommyBox reads the standard <abbr title="Progressive Web Apps">PWA</abbr> webmanifest and the standard Tomcat configuration files</li>
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
	<li>
		Supported apps forms:
		<ul>
			<li>Directory</li>
			<li>WAR (or ZIP) file</li>
			<li>URL (remote WAR (or ZIP) file)</i>
		</ul>
	</li>
	<li>
		Different app natures:
		<ul>
			<li>frontend-only (static content)</li>
			<li>backend-only (pure server-side, no browser)</li>
			<li>frontend + backend (fullstack dynamic apps)</i>
		</ul>
	</li>
	<li>
		Configurable display modes:
		<ul>
			<li>in-window</li>
			<li>in-browser</li>
			<li>fullscreen</li>
			<li>headless</li>
		</ul>
	</li>
	<li>Supports custom command line args</li>
	<li>Supports standard password protected ZIP archives</li>
	<li>Single and multiple windows modes</li>
	<li>Optional custom splash screen</li>
	<li>Optional custom context menu</li>
	<li>Optional custom system tray icon with custom menu</li>
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
java -jar tb.jar --app MyKillerApp.war myparam1 myparam2
```


Run ZIP (or WAR) from web server:
```bash
java -jar tb.jar --app https://example.com/MyKillerApp.zip
```


Run app from directory:
```bash
java -jar tb.jar --app MyKillerAppDir
```


Run encrypted ZIP (or WAR) archive:
```bash
java -jar tb.jar --app MyKillerApp.zip --password mysecret
```


<h3>Embed app:</h3>
<ul>
	<li>Option 1. Copy your app content into the <code>/app</code> directory of the <code>tb.jar</code>
	</li>
	<li>Option 2. Pack your app as <code>app.war</code> or <code>app.zip</code> (the archive can be encrypted) and copy archive to the root directory of the <code>tb.jar</code>
	</li>
</ul>

Brand your app by renaming the <code>tb.jar</code> to the <code>MyKillerApp.jar</code>

Run your embedded app:
```bash
java -jar MyKillerApp.jar
```


Run password-protected embedded app with custom command-line args:
```bash
java -jar MyKillerApp.jar --password mysecret myparam1 myparam2
```

<h3>F.A.Q.</h3>

<strong>Q.</strong> My app failed with <code>java.lang.ClassNotFoundException: javax.servlet.\*</code>
<br>
<strong>A.</strong> As a result of the move from Java EE to Jakarta EE, starts with v10 Apache Tomcat supports only the Jakarta EE spec. <code>javax.servlet.\*</code> is no longer supported.
Replace the <code>javax.servlet.\*</code> imports in your code with <code>jakarta.servlet.\*</code>

