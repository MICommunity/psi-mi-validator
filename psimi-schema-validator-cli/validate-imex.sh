#!/bin/bash

FILE=$1

mvn -U clean install -Pvalidate -Dfile="$FILE" -Dscope=imex -Dlevel=WARN -Dmaven.test.skip
