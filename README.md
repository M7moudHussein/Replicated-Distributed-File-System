# Replicated-Distributed-File-System

[![Build Status](https://travis-ci.com/M7moudHussein/Replicated-Distributed-File-System.svg?token=ui1vZpqLuQ1oXxYH7t2x&branch=master)](https://travis-ci.com/M7moudHussein/Replicated-Distributed-File-System)

## How to run:

### To start the server
Execute the bash script start_server.sh with the required ip and port

```./start_server.sh -ip <server_ip> -port <required_rmi_registry_port>```

### To stop the server
Execute the bash script stop_server.sh

```./stop_server.sh```

<aside class="warning">
The replicas and the server are run as background processes. Running the start_server.sh script multiple times without executing stop_server.sh script leads to consuming the machine resources by the background processes.
</aside>
