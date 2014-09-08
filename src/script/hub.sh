#!/bin/bash

set -e

RESOURCE=hub

usage() {
	nom=`basename $0`
	echo "+"
    echo "+ +  ${nom} [-l] list all"
    echo "+ +  ${nom} [-g id] get single"      
	echo "+ +  ${nom} [-c file ] post with specified file"
	#echo "+ +  ${nom} [-p file ] patch with specified file"   
    echo "+ +  ${nom} [-d id ] delete single"   
	echo "+ +  ${nom} [-h] help"   
	echo "+"
	}

# HELP
if [ $# -eq 1 -a "$1" = -h ]; then usage; exit 2; fi

. commons/conf.sh
. commons/curl.sh

# OPTIONS
errOption=0
OPTIND=1
#while getopts "lg:c:p:d:" option
while getopts "lcg:d:f:" option
do
	case $option in
        l)  GET=OK   
			;;
        g)  GET=OK
            ID="${OPTARG}"        
			;;
		c)  POST=OK
            FILE="${OPTARG}"
            ;;
        xx)  PATCH=OK
            FILE="${OPTARG}"        
            ;;
        d)  DELETE=OK
            ID="${OPTARG}"
			;;       
        f)  FILE="${OPTARG}"
			;;
		\?) echo " option $OPTARG INVALIDE" >&2
			errOption=3
	esac
done

if [ $errOption == 3 ]; then usage >&2; exit $errOption; fi

# GET
if [ -n "$GET" ]; then
    if [ -n "$QUERY" ]; then
        QUERY="?$QUERY"
    fi
    get "${RESOURCE}/${ID}${QUERY}"
    exit 2
fi

# POST
if [ -n "$POST" ]; then
    if [ ! -n "$FILE" ]; then
        FILE=hub.json
    fi
    post "${RESOURCE}" $FILE
    exit 2
fi

# DELETE
if [ -n "$DELETE" ]; then
    delete "${RESOURCE}/${ID}"
    exit 2
fi

usage >&2


