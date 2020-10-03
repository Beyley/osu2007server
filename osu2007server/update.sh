#!/bin/sh
git pull
mvn clean 
mvn install
sudo mvn exec:java -Dexec.mainClass=poltixe.osu2007.App