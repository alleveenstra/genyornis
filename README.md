<pre>                                         _     
  __ _  ___ _ __  _   _  ___  _ __ _ __ (_)___ 
 / _` |/ _ \ '_ \| | | |/ _ \| '__| '_ \| / __|
| (_| |  __/ | | | |_| | (_) | |  | | | | \__ \
 \__, |\___|_| |_|\__, |\___/|_|  |_| |_|_|___/
 |___/            |___/</pre>                        

This is a COMET style push HTTP server in Java.

Interesting features are:
* It uses non-blocking IO
* It supports Websockets
* It has a messaging system like IRC
* Serverside JavaScript apps and HTTP clients can communicate

Note that this is a work in progress!

** Running the project **

mvn clean install
java -jar target/genyornis-0.01.00-jar-with-dependencies.jar applications/
visit http://localhost:1337/
