<div align="center"><img height="200px" alt="logo" src="/favicon.webp?raw=true"/></div>

## <div align="right"><a href="https://www.buymeacoffee.com/zggis" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a></div>

### Description
Howler is a log file monitoring application that allows you to setup notifications for specific events in .txt and .log files. Currently Discord and Gotify are supported notification platforms. Unlike other log analysis tools, Howler is simple and easy to configure.

### Installing
#### Unraid
To install Howler on Unraid you can install the docker container through the community applications. You will need to map the CONFIG path in the application template to a directory Howler will save its database file to.
#### Docker Desktop
You can run Howler on Docker locally by using the following command. Replace CONFIG with the directory you want the database to be saved to, and any other directories that contain your log files.
```
$ docker run -v CONFIG:/data/config -v LOGS1:/data/logs1 LOGS2:/data/logs2 -p 8080:8080 zggis/howler:latest
```
Windows Example
```
$ docker run -v "C:/temp":/data/config -v "C:/logs1":/data/logs1 -v "C:/logs2":/data/logs2 -p 8080:8080 zggis/howler:latest
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
Start Howler and navigate to the WebGUI using the port you configured above. Start by setting up two data sources one for /app1/logs and another for /app2/logs. All .txt and .log files in these directories will be monitored.<br>
Next you can setup a few alerts on these data sources. Make sure you test your alerts after you add them and you should be all set. You can setup multiple alerts for each data source to monitor error events, completion events, and any others you desire.

### Configuration

#### Required Path
Name | Type | Container Path | Description
--- | --- | --- | ---
CONFIG | PATH | /data/config | Map this to a directory where Howler can save its database file.

#### Optional Variables
Container Variable | Default Value | Description
--- | --- | ---
PUID | 0 (99 in Unraid template) | Controls the user the container runs as.
PGID | 0 (100 in Unraid template) | Controls the group the container runs as.
UMASK | 0000 | Controls the UMASK the container uses.
LOGLEVEL | INFO | Set to DEBUG or TRACE to inclease the logging level for debugging.

### FAQ
**Question:** So this is like Splunk?

**Answer:** No, Howler does not provide any searching capability on your log files. It is intended to be a bare bones alerting service. Splunk's alerting feature requires a paid license, Howler delivers this for free.
##
**Q:** What happens when my log files roll over?

**A:** Howler monitors the data source directories for changes, when new files are added to the directory a new thread will spin up to monitor that new file. Likewise when files are deleted its monitor will die. Data sources <strong>DO NOT</strong> need to be recreated or updated to handle this.
