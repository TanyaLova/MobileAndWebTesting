#!/usr/bin/env bash

rsync -av --rsh=ssh --progress /Users/name/.jenkins/workspace/test/target/surefire-reports/video/ANDROID name@10.122.5.54:/volume2/SharedRNDData/Robot_Data/Robot_1/videos/ANDROID

rsync -av --rsh=ssh --progress /Users/name/.jenkins/workspace/test/target/surefire-reports/video/IOS name@10.122.5.54:/volume2/SharedRNDData/Robot_Data/Robot_1/videos/IOS

rsync -av --rsh=ssh --progress /Users/name/.jenkins/workspace/test/target/surefire-reports/screenshot/ANDROID name@10.122.5.54:/volume2/SharedRNDData/Robot_Data/Robot_1/screenshots/ANDROID

rsync -av --rsh=ssh --progress /Users/name/.jenkins/workspace/test/target/surefire-reports/screenshot/IOS name@10.122.5.54:/volume2/SharedRNDData/Robot_Data/Robot_1/screenshots/IOS

rsync -av --rsh=ssh --progress /Users/name/.jenkins/workspace/test/target/surefire-reports/screenshot/CHROME name@10.122.5.54:/volume2/SharedRNDData/Robot_Data/Robot_1/screenshots/CHROME

rsync -av --rsh=ssh --progress /Users/name/.jenkins/workspace/test/assets/ name@10.122.5.54:/volume2/SharedRNDData/Robot_Data/Robot_1/videos/CHROME