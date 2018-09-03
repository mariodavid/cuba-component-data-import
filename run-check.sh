#!/usr/bin/env bash

./gradlew startDb
./gradlew createTestDb
./gradlew check
./gradlew assemble
./gradlew stopDb
