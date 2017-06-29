java -jar /home/rayrishty/git/events-rss/build/libs/rss-to-html-1.0-SNAPSHOT.jar > weekly.html
RETVAL=$?
[ $RETVAL -eq 0 ] && cp weekly.html /usr/local/nginx/html/weekly/weekly.html
[ $RETVAL -ne 0 ] && echo Failure