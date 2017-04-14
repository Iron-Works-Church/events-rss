## Configuring SSL
Certficate needs to be imported into the Java keystore in order to use https. 

 1. Export certification as `X.509 Certificate (DER)` from Firefox as `ironworkschurchorg.der`
 1. sudo keytool -import -alias ironworkschurchorg -keystore /Library/Java/JavaVirtualMachines/jdk1.8.0_77.jdk/Contents/Home/jre/lib/security/cacerts -file ironworkschurchorg.der 

## Running

Execute `./gradlew bootRun` (or `gradlew.bat bootRun` on Windows) to build and execute.