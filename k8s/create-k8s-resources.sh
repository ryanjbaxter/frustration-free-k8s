#!/bin/bash

./mvnw k8s:resource -Pdev > target/k8s.log
cat target/classes/META-INF/jkube/kubernetes.yml
