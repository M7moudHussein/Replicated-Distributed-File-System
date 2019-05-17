#!/usr/bin/env bash
kill -9 $(ps ax | grep "java -jar $HOME/Replicated-Distributed-File-System" | grep -v grep | awk '{print $1}')