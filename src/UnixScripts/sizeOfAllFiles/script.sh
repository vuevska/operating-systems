#!/bin/bash

# Да се пресмета вкупната големина на сите фајлови во фолдерот
# што се праќа како прв аргумент на скриптата. Изминувањето на
# датотеките треба да се имплементира со помош на рекурзивна
# функција. Резултатот е цел број во бајти

if [ ! $# -eq 1 ]
then
        echo "Invalid usage"
        exit 1
fi

size=0
for f in `ls -l "$1"`
do
        if [ -f "$1/$f" ]
        then
                fileSize=`ls -l "$1/$f" | awk '{print $6}'`
                size=$(($size + $fileSize))
        fi
        if [ -d "$1/$f" ]
        then
                for rf in `ls -l "$1/$f/$rf"`
                do
                        if [ -f "$1/$f/$rf" ]
                        then
                                fileSize=`ls -l "$1/$f/$rf" | awk '{print $6}'`
                                size=$(($size + $fileSize))
                        fi
                done
        fi
done
echo $size
