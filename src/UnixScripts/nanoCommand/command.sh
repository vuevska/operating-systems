#!/bin/bash

# Напишете команда која ќе изброи колку студенти во моментот
# работат со nano (имаат активен прроцес nano)

ps -ef | grep nano | awk '{print $1}' | sort | uniq | wc -l
