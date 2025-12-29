#!/bin/sh
if [ ! -f "./antlr-4.13.2-complete.jar" ]; then
    wget https://www.antlr.org/download/antlr-4.13.2-complete.jar
fi
alias antlr4='java -Xmx500M -cp "./antlr-4.13.2-complete.jar:$CLASSPATH" org.antlr.v4.Tool'
antlr4 -Dlanguage=Java -visitor -package com.cloud.apim.seclang.antlr -o ./src/main/scala/com/cloud/apim/secland/antlr ./src/main/resources/g4/*.g4

