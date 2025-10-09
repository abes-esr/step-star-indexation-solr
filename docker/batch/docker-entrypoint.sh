#!/bin/bash

export DOCUMENT_INDEXATION_SOLR_CRON=${DOCUMENT_INDEXATION_SOLR_CRON:='* * * * *'}
export $DOCUMENT_INDEXATION_SOLR_AT_STARTUP=${$DOCUMENT_INDEXATION_SOLR_AT_STARTUP:='1'}

# Réglage de /etc/environment pour que les crontab s'exécutent avec les bonnes variables d'env
echo "$(env)
LANG=en_US.UTF-8" > /etc/environment

# Charge la crontab depuis le template
envsubst < /etc/cron.d/document-indexation-solr.tmpl > /etc/cron.d/tasks
echo "-> Installation des crontab :"
cat /etc/cron.d/tasks
crontab /etc/cron.d/tasks

# Force le démarrage du batch au démarrage du conteneur
if [ "$DOCUMENT_INDEXATION_SOLR_AT_STARTUP" = "1" ]; then
  echo "-> Lancement de document-indexation-solr.sh au démarrage du conteneur"
  /scripts/document-indexation-solr.sh
fi

# execute CMD (crond)
exec "$@"