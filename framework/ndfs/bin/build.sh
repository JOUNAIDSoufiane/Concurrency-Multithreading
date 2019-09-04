#!/usr/bin/env bash
if  hostname -f | grep -q -e 'fs.\.cm\.cluster' ; then
  module load java/jdk-1.8.0
fi

ant clean jar
