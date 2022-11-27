<div align="center"><img height="200px" alt="logo" src="/favicon.webp?raw=true"/></div>

## <div align="right"><a href="https://www.buymeacoffee.com/zggis" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a></div>

### Description
Howler is a log file monitoring application that allows you to setup notifications for specific events. Currently Discord and Gotify are supported notification platforms. Unlike other log analysis tools, Hower is simple and easy to configure.

### Installing
#### Unraid
To install Howler on Unraid you can install the docker container through the community applications. You will need to map the CONFIG path in the application template to a directory Howler will save its database file to.
#### Docker Desktop
You can run Howler on Docker locally by using the following command. Replace CONFIG with the directory you want the database to be saved to, and any other directories that contain your log files.
```
$ docker run -v CONFIG:/data/config -v LOGS1:/data/logs1 LOGS2:/data/logs2 zggis/howler:latest
```
Windows Example
```
$ docker run -v "C:/temp":/data/config -v "C:/logs1":/data/logs1 -v "C:/logs2":/data/logs2 zggis/howler:latest
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
