<div align="center"><img height="200px" alt="logo" src="/logo.png?raw=true"/></div>

## <div align="right"><a href="https://www.buymeacoffee.com/zggis" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a></div>

### Description
Howler is a log file monitoring application that allows you to setup notifications for specific events in log files. Currently Discord, Gotify, and Slack are supported notification platforms. Unlike other log analysis tools, Howler is simple and easy to configure.

### Installing
#### Unraid
To install Howler on Unraid you can install the docker container through the community applications. You will need to map the PATH's for your log files in the template.
#### Docker Desktop
You can run Howler on Docker locally by using the following command. Replace CONFIG with the directory you want the database to be saved to, and any other directories that contain your log files.
```
$ docker run -v CONFIG:/config -v LOGS1:/app1/logs LOGS2:/app2/logs -p 8080:8080 zggis/howler:latest
```
Windows Example
```
$ docker run -v "C:/temp":/config -v "C:/logs1":/app1/logs -v "C:/logs2":/app2/logs -p 8080:8080 zggis/howler:latest
```
#### Java App
You can run Howler as a Java program from command prompt. Java JRE 11 is required. To grab the latest Howler JAR, navigate to the <a href="https://github.com/Zggis/howler/releases">Releases</a> page. Run the JAR using this command.
```
$ java -jar -Dspring.profiles.active=local howler.jar --spring.datasource.url=C:/temp
```

### Building the Code
Clone the repo and update application-local.properties as you need, then build with gradle. JAR should be placed in /build/libs
```
$ gradlew assemble
```

### Usage
Lets say you have two application's whose log files you want to monitor and setup alerts for. When you configure the container be sure to expose all the log file directories to the container using PATHs. Assuming both these applications have their own log file directories you will need to map two PATHs to the correct log file directories. You can use any container path you like, but you must remember it when you configure the data sources in the Howler UI. We will use /app1/logs and /app2/logs<br>
You will also need to map the container path /data/config to a location on your machine where Howler can save its database file.<br>
Lastly make sure you expose container port 8080 to a desired port, you will need this to access the WebGUI.<br>
Start Howler and navigate to the WebGUI using the port you configured above. Start by setting up two data sources one for /app1/logs and another for /app2/logs. By default all .txt and .log files in these directories will be monitored, to monitor other file types configure the FILETYPES variable, see below.<br>
Next you can setup a few alerts on these data sources. Make sure you test your alerts after you add them and you should be all set. You can setup multiple alerts for each data source to monitor error events, completion events, and any others you desire.

### Configuration

#### Required Path
Name | Type | Container Path | Description
--- | --- | --- | ---
CONFIG | PATH | /data/config | Map this to a directory where Howler can save its database file.

#### Optional Variables
Container Variable | Default Value | Description
--- | --- | ---
FILETYPES | txt,log | A comma separated string of file types that will be monitored by all data sources.
LOGLEVEL | INFO | Set to DEBUG or TRACE to inclease the logging level for debugging.

### FAQ
**Question:** So this is like Splunk?

**Answer:** No, Howler does not provide any searching capability on your log files. It is intended to be a bare bones alerting service. Splunk's alerting feature requires a paid license, Howler delivers this for free.
##
**Q:** What happens when my log files roll over?

**A:** Howler monitors the data source directories for changes, when new files are added to the directory a new thread will spin up to monitor that new file. Likewise when files are deleted its monitor will die. Data sources <strong>DO NOT</strong> need to be recreated or updated to handle this.
##
**Q:** Why aren't my alerts triggering?

**A:** One of the most common reasons for this is a log file might have multile empty lines at the end and the file is appended to in the middle rather than the end of the file. For example:<br>
Line 1:This is<br>
Line 2:a test<br>
Line 3:file<br>
Line 4:\n<br>
We would expect new content to be inserted on line 5 and terminated with a newline, however if content is inserted on line 4 (a seemingly blank line) it will not be detected correctly by Howler, since line 4's newline will be pushed down to line 5. <strong>Make sure there are no blank lines at the end of your log file.</strong> It may be helpful to set the LOGLEVEL environment variable to DEBUG, this will dump all events to Howler's logs so you can see if the events are being captured. If you see the events, the problem must be with your trigger event string in the alert.
