#!/bin/bash

# Verificar si el contenedor 'mongo' está en ejecución
if ! docker ps --format '{{.Names}}' | grep -q "^mongo$"; then
    echo "Error: El contenedor 'mongo' no está en ejecución."
    echo "Asegúrate de haber levantado la infraestructura con: docker-compose --profile infra up -d"
    exit 1
fi

echo "Reseteando colecciones de MongoDB en el contenedor 'mongo'..."

# Ejecutar el script reset_mongo.js dentro del contenedor
docker exec -i mongo mongosh --quiet < reset_mongo.js

if [ $? -eq 0 ]; then
    echo "MongoDB reseteado correctamente."
else
    echo "Hubo un error al resetear MongoDB."
    exit 1
fi

