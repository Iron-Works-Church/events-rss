java -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.dateTimeFormat="yyyy-MM-dd HH:mm:ss:SSS Z" -Dorg.slf4j.simpleLogger.log.org.ironworkschurch=debug -Dorg.slf4j.simpleLogger.logFile=weekly.log -jar /home/rayrishty/git/events-rss/build/libs/rss-to-html-1.0-SNAPSHOT.jar > weekly.html
RETVAL=$?
[ $RETVAL -eq 0 ] && cp weekly.html /usr/local/nginx/html/weekly/weekly.html
[ $RETVAL -ne 0 ] && echo Failure