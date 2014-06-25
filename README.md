##################
# Tripples v 1.0 #
##################

Date: 28/05/2014
Authors: E. Bodnari [catenika_03@mail.ru], A. Bratus [andreybratus@yahoo.com]

The compiled code is porvided in the /bin directory and the source code under /src folder
Documentation in the form of JavaDoc is available at /doc folder.

- Provided binaries can be run under Windows, Linux, Mac OS.

Prerequisites to run Binary code.
=====================================
1. Insalled JRE, version 1.6 or higher
2. Locally running MongoDB Database, version 2.6.1
3. Google API key, with YoutubeData API v3 and Freebase API permissions.

Running the binary code
=======================================
1. Insert the Google APi Key in conf/youtube.properites file
2. Start locally a mongoDB instance
3. go to bin/ directory and run the "start" script. Application will start bind to the 
	local ip-adress, accessible by the loopback adress, port 9000. (127.0.0.1:9000)

To pass additional environmental variables to the application , use option -J-X:

db.host - specify not default address of the database (127.0.0.1)
db.port - specify not default port of the database	  (27017)


#############################################################

-To compile the application from the source code:

Prerequisites to compile from the source
=========================================
1. Installed Java version 1.6+
2. Installed Scala version 2.1+ libraries http://www.scala-lang.org/download/
3. Installed Play Framework 2.2+  http://www.playframework.com/

Compiling from the source
===========================================
1. Go to the src folder and enter the Play Framework Console by typing: "play" in the command line
2. In the play Console type: "dist", which will compile and prepare a distribution package
