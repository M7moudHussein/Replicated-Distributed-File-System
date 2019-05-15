#!/usr/bin/env bash

while [[ "$#" -gt 0 ]]; do case $1 in
  -ip) ip="$2"; shift;;
  -port) port="$2"; shift;;
  *) echo "Unknown parameter passed: $1"; exit 1;;
esac; shift; done

app_path=$(echo "Replicated-Distributed-File-System")

rm -rf ~/${app_path}

mkdir -p ~/${app_path}/master
mkdir -p ~/${app_path}/replica
mkdir -p ~/${app_path}/client

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
mvn package

#${JAVA_HOME}/bin/rmiregistry 1900 &

replicas="${parent_path}/repServers.txt"
identifier="1"

while IFS=" " read -r host_port _3
do

IFS=: read -a ARRAY <<< "${host_port}"

_1=${ARRAY[0]}
_2=${ARRAY[1]}

#Noraml ssh
#ssh ${_1} "
    mkdir -p ~/${app_path}/replica/${identifier} &&
    cp ${parent_path}/target/ReplicaJar-Core.jar ~/${app_path}/replica/${identifier} &&
    java -jar ~/${app_path}/replica/${identifier}/ReplicaJar-Core.jar ${ip} ${_2} ${identifier} ${_3} &
#"

printf 'user_ip: "%s" - port: "%s" - appDir: "%s"\n' "$_1" "$_2" "$_3"
identifier=$((identifier+1))
done <"$replicas"

cp target/ServerJar-Core.jar ~/${app_path}/master
cp ${parent_path}/repServers.txt ~/${app_path}/master
echo "${ip} ${port}"

sleep 3
java -jar ~/${app_path}/master/ServerJar-Core.jar ${ip} ${port} &

sleep 1
cp target/ClientJar-Core.jar ~/${app_path}/client
cp ${parent_path}/sample_actions.txt ~/${app_path}/client

java -jar ~/${app_path}/client/ClientJar-Core.jar
