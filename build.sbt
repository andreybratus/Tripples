name := "Tripples"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)     

libraryDependencies ++= Seq(
"com.google.apis" % "google-api-services-youtube" % "v3-rev99-1.17.0-rc",
"org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.13",
"org.codehaus.jackson" % "jackson-core-asl" % "1.9.13",
"com.google.http-client" % "google-http-client-jackson2" % "1.18.0-rc",
"org.apache.httpcomponents" % "httpclient" % "4.3.3",
"org.mongodb" % "mongo-java-driver" % "2.11.4",
"org.slf4j" % "slf4j-api" % "1.7.6",
"org.json" % "json" % "20140107",
"commons-io" % "commons-io" % "2.4"
)

play.Project.playJavaSettings
