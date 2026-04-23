@echo off
setlocal

echo Reseteando colecciones de MongoDB en el contenedor 'mongo'...

:: Verificar si el contenedor 'mongo' existe y está corriendo
docker ps --filter "name=mongo" --filter "status=running" --format "{{.Names}}" | findstr /I "^mongo$" >nul
if %errorlevel% neq 0 (
    echo Error: El contenedor 'mongo' no está en ejecución.
    echo Asegúrate de haber levantado la infraestructura con: docker-compose --profile infra up -d
    exit /b 1
)

:: Ejecutar el script reset_mongo.js
type reset_mongo.js | docker exec -i mongo mongosh --quiet

if %errorlevel% equ 0 (
    echo MongoDB reseteado correctamente.
) else (
    echo Hubo un error al resetear MongoDB.
    exit /b 1
)

endlocal

