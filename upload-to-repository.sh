#!/usr/bin/env bash

if [[ -z "$1" || -z "$2" ]]
  then
    echo "Define username, password and repository URL as parameters. Usage: ./upload-to-repository.sh myUser myPassword https://api.bintray.com/maven/balvi/..."

  else
    if [[ -z "$3" ]]
      then
        ./gradlew -DuploadRepositoryUsername=$1 -DuploadRepositoryPassword=$2 uploadArchives
      else
        ./gradlew -DuploadRepositoryUsername=$1 -DuploadRepositoryPassword=$2 -DuploadRepositoryRelease=$3 uploadArchives

    fi

fi
