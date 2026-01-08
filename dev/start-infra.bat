@echo off
echo ========================================
echo Starting HexRide Infrastructure
echo ========================================
echo.

cd /d "%~dp0.."
docker-compose -f docker-compose.infra.yml up -d

echo.
echo Infrastructure starting...
echo.
echo Services:
echo   PostgreSQL:     localhost:5432
echo   Redis:          localhost:6379
echo   Kafka:          localhost:9092
echo   Elasticsearch:  localhost:9200
echo   Kibana:         localhost:5601
echo   Zipkin:         localhost:9411
echo   Prometheus:     localhost:9090
echo   Grafana:        localhost:3000
echo.
echo Run 'docker-compose -f docker-compose.infra.yml logs -f' to see logs
echo.
pause
