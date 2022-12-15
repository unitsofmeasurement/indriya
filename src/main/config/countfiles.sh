#!/bin/sh
# find ~/repo/./target/surefire-reports -iname "*.txt" -exec grep -Li "Failures: 0" {} \+
number_of_failures=`grep -L -r -i "Failures: 0" ~/repo/./target/surefire-reports/*.txt | wc -l`
echo $number_of_failures