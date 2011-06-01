This is a COMET style push HTTP server implemented in java. It works a bit 
like NodeJS and ProjectAPE, as it has server side JavaScript scripts. The
most interesting packages are:
* nl.alleveenstra.qpserv.channels
* nl.alleveenstra.qpserv.javascript
* nl.alleveenstra.qpserv.routing
* nl.alleveenstra.qpserv.controllers

The nl.alleveenstra.qpserv.httpd package mostly contain source files that
are not entirely written by me.

Interesting features are:
* It can run JavaScript serverside
* It uses non-blocking IO
* It has a messaging system like IRC
* Serverside JavaScript apps and HTTP clients can communicate

Note that this is a work in progress.