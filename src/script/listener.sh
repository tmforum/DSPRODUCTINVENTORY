#!/bin/bash

set -e

nom=`basename $0`
echo "+"
echo "+ +  ${nom} port pathToLogFile"
echo "+ +"      
echo "+ +  ...listening at http://localhost:$1/listener"
echo "+ +"       
echo "+"
if [[ -n $1 ]]
then 
printf "listening at http://localhost:$1/listener\n\n" 
else
printf "listening at http://localhost:9000/listener\n\n" 
fi
java -classpath lib/commons-io-2.4.jar -jar lib/DSMockService-2.0.jar  $1