#!/bin/bash

java -Xmx768M -XX:MaxPermSize=250m \
    -jar sbt-launch-*.jar "$@"

