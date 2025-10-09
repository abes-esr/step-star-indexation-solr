#!/bin/bash

LANG=fr_FR.UTF-8
if [[ $(pgrep -cf "step-star-indexation-solr.jar --spring.batch.job.names=documentIndexationSolr") < 1 ]];
then
    java -Xmx5120m -jar /scripts/step-star-indexation-solr.jar --spring.batch.job.names=documentIndexationSolr
fi