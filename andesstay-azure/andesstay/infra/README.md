# Infraestructura — AndesStay

Tres grupos independientes de contenedores, cada uno con su `compose.yml`:

| Carpeta | Contiene |
|---|---|
| `apps/` | Los 8 microservicios Spring Boot (BFF + 5 de dominio + 2 admin) |
| `mq/` | RabbitMQ + Management UI (comandos: email, housekeeping, voucher) |
| `kafka/` | Kafka en modo KRaft (eventos: auditoría y reportería) |
| `db/` | `init.sql` con los schemas de PostgreSQL (`reservations`, `catalog`, `audit`, `report`) |

Cada uno puede correr en el mismo equipo (desarrollo) o en servidores
separados (producción) — solo cambian los hosts en `infra/apps/.env`.

## Quién produce y quién consume

| Broker | Productor | Consumidor |
|---|---|---|
| RabbitMQ | `ms-andesstay-reservations` | `ms-andesstay-notify` |
| Kafka (`reservations.events`) | `ms-andesstay-reservations` | `ms-andesstay-audit`, `ms-andesstay-report` |

El consumidor de RabbitMQ (`notify`) no contiene ningún código de Kafka.

## Orden para levantar todo en un mismo equipo

1. `cd infra/kafka && cp .env.example .env && docker compose up -d`  (espera ~1 min)
2. `cd infra/mq && docker compose up -d`
3. `cd infra/apps && cp .env.example .env && docker compose up -d --build`
   (el `.env.example` ya apunta a `host.docker.internal`, que funciona
   cuando los 3 grupos corren en el mismo equipo)

## En AWS

| Instancia | Qué corre | Puertos (Security Group) |
|---|---|---|
| `andesstay-apps` | `infra/apps` | 8080 público; el resto cerrado |
| `andesstay-rabbitmq` | `infra/mq` | 5672 y 15672 solo desde apps / tu IP |
| `andesstay-kafka` | `infra/kafka` | 19092 solo desde apps |
| RDS PostgreSQL | `infra/db/init.sql` | 5432 solo desde apps |

Todo por IP privada dentro de la misma VPC. Pasos de Kafka en `infra/kafka/README.md`.
