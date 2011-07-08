                                         _     
  __ _  ___ _ __  _   _  ___  _ __ _ __ (_)___ 
 / _` |/ _ \ '_ \| | | |/ _ \| '__| '_ \| / __|
| (_| |  __/ | | | |_| | (_) | |  | | | | \__ \
 \__, |\___|_| |_|\__, |\___/|_|  |_| |_|_|___/
 |___/            |___/                        

This is a COMET style push HTTP server implemented in java. It works a bit 
like NodeJS and ProjectAPE, as it has server side JavaScript scripts. The
most interesting packages are:
* nl.alleveenstra.genyornis.channels
* nl.alleveenstra.genyornis.javascript
* nl.alleveenstra.genyornis.routing
* nl.alleveenstra.genyornis.controllers

The nl.alleveenstra.genyornis.httpd package mostly contain source files that
are not entirely written by me.

Interesting features are:
* It can run JavaScript serverside
* It uses non-blocking IO
* It has a messaging system like IRC
* Serverside JavaScript apps and HTTP clients can communicate

Note that this is a work in progress!


** Running the project **

mvn clean install
java -jar target/genyornis-0.01.00-jar-with-dependencies.jar applications/
visit http://localhost:1337/
