#!/bin/bash

# check if 1 argument is sent to the script
if [ $# -ne 1 ]
then
        echo "Invalid usage"
        exit 1
fi

# check if the result file exists
if [ -f out.txt ]
then
        rm out.txt
fi

month=$1
IFS=$'\n'
vkupno=0
br=0

# calculate the average amount of electricity spent
for f in `cat zad4.csv | grep -v "NameSurname"`
do

        ind=`echo $f | awk -F\| '{print $NF}'`
        vkupno=$(($vkupno + $ind))
        br=$(($br + 1))
done

prosecno=`echo "scale=0; $vkupno/$br" | bc`

# filter csv file and send result to the out.txt file
for p in `cat zad4.csv | grep -v "NameSurname"`
do
        each_month=`echo $p | awk -F\| '{print $3}'`
        if [ $month = $each_month ]
        then
                m=`echo $p | awk -F\| '{print $NF}'`
                if [ $m -gt $prosecno ]
                then
                        echo $p | awk -F\| '{print $1, $5}' >> out.txt
                fi
        fi
done

cat out.txt | sort -r -k 5
