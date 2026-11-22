# Servidor de aplicaciones — los 8 microservicios

```bash
cd infra/apps
cp .env.example .env   # ajusta host de RabbitMQ y Kafka si corren en otro servidor
docker compose up -d --build
```

La primera vez tarda varios minutos (compila los 8 proyectos Maven).

| Servicio | Puerto | Acceso |
|---|---|---|
| ms-andesstay-bff | 8080 | **Público** (único punto de entrada del frontend) |
| ms-andesstay-reservations | 8081 | Interno |
| ms-andesstay-catalog | 8082 | Interno |
| ms-andesstay-notify | 8083 | Interno (solo consume RabbitMQ) |
| ms-andesstay-audit | 8084 | Interno |
| ms-andesstay-report | 8085 | Interno |
| ms-andesstay-rabbitmq-admin | 8086 | Interno |
| ms-andesstay-kafka-admin | 8087 | Interno |

En desarrollo todos los puertos quedan expuestos para poder probarlos uno a
uno. En un servidor real, deja abierto hacia afuera **solo el 8080** en el
firewall; el resto ocurre dentro de la red Docker `andesstay-apps-net`.

Verificar: `docker compose ps` y `curl http://localhost:8080/actuator/health`
