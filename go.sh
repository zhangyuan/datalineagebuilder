#!/usr/bin/env bash

set -e

./gradlew bootRun
dot -Tpng graph.dot -ograph.png