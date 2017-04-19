#!/bin/sh

sleep 70
touch /tmp/jetpack.log
echo "opening vision" >> /tmp/jetpack.log
export LD_LIBRARY_PATH=/home/ubuntu/Documents/mjpg-streamer-master/
/home/ubuntu/Documents/PegTargeting/Vision &> /tmp/jetpack.log
echo "running vision" >> /tmp/jetpack.log
