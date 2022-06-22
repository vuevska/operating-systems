#!/bin/bash

# Излистајте ги во посебна датотека сите датотеки
# од  моменталниот директориум кои се модифицирани во
# тековиниот ден

if [ -f output.txt ]
then
        rm output.txt
fi

day=`date | awk '{print $3}'`

IFS=$'\n'

for f in `ls -l | grep -v "total"`
do
        fday=`echo "$f" | awk '{print $8}'`
        name=`echo "$f" | awk '{print $NF}'`
        if [ "$fday" = "$day" ]
        then
                echo "$name" >> output.txt
        fi
done

cat output.txt

