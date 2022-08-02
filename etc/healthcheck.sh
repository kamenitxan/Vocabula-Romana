#!/bin/sh

RES=`curl -s localhost:4567/jakon/health | grep  -c JAKON_OK`
if [ $RES = 1 ]
then
  echo 0
else
  echo 1
fi