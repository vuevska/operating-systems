#!/bin/bash

cat zad3.csv | awk -F\| '$3==6{print $2, $3, $NF}' | grep '^Debar' | awk 'BEGIN{vkupno=0; br=0} {vkupno+=$NF; br++} END{print "Prosecen broj na KWh potroseni vo juni vo opstina Debar e " vkupno/br}'

