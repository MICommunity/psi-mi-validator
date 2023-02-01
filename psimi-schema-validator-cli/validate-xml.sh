#!/bin/sh

FILE=$1

mvn -U clean install -Pvalidate -Dfile=$FILE -Dscope=syntax -Dlevel=WARN -Dmaven.test.skip
