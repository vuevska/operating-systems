#!/bin/bash

# Направете скрипта која ги печати имињата
# на сите извршни датотеки од тековниот директориум
# во датотеката executables.txt и на крај го печати
# бројот на датотеки


if [ -f "executables.txt" ]
then
        rm executables.txt
fi

# shellcheck disable=SC2045
for f in `ls`
do
        if [ -x "$f" -a -f "$f" ]
        then
                echo "$f" >> executables.txt
        fi
done

cat executables.txt
cat executables.txt | wc -l


