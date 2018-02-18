#!/usr/bin/env bash

if [[ -z "$1" ]]
  then
    echo "Define tag version. Usage: ./tag-release.sh 0.3.1"

  else
    git tag -a $1 -m "version $1"
    git push origin $1
    git tag
fi
