#!/bin/bash

# Write a shell script that prints out the household (the column NameSurname) that
# spent more electricity in the month of x than in the month of y
# x and y are arguments (integer) used when the script is invoked.

if [ $# -ne 2 ]
then
        echo "Invalid usage! Correct: $0 number1 number2"
        exit 1
fi

x=$1
y=$2

IFS=$'\n'
array=( `cat data.csv | awk -F\| '{print $1, $3, $NF}' | sort | grep 0[${1}-${2}]` )
for ((index=0; index < ${#array[@]} - 1; index++))
do
        first_name=`echo "${array[index]}" | awk '{print $1}'`
        second_name=`echo "${array[index+1]}" | awk '{print $1}'`
        first=`echo "${array[index]}" | awk '{print $NF;}'`
        second=`echo "${array[index+1]}" | awk '{print $NF;}'`
        if [ "$first_name" = "$second_name" ]
        then
                if [ "$first" -gt "$second" ]
                then
                        echo "${array[index]}" | awk '{print $1, $2}'
                fi
        fi
done
