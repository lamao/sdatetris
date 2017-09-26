#!/bin/sh
cd `dirname $0`
if [ -n "$JAVA_HOME" ]; then
  $JAVA_HOME/bin/java -Djava.library.path="native" -jar sdatetris.jar
else
   java -Djava.library.path="native" -jar sdatetris.jar
fi

