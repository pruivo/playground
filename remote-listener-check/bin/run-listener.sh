#!/bin/bash

java -cp "$(dirname $0)/../target/remote-listener-check-1.0-SNAPSHOT.jar:$(dirname $0)/../target/lib/*" me.pruivo.ListenerCheck $@