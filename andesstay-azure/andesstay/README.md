# AndesStay — Caso semestral

Plataforma de reservas para hostales, cabañas y lodges con arquitectura de
**microservicios**, login con **Azure AD (MSAL)**, mensajería con **RabbitMQ**
y **Kafka**, y despliegue con **Docker Compose**.

## Estructura

```
andesstay/
├── ms-andesstay-bff/            Único punto público (8080). Valida JWT de Azure AD y reenvía.
├── ms-andesstay-reservations/   Reservas, estados, disponibilidad (8081). Publica a RabbitMQ y Kafka.
├── ms-andesstay-catalog/        Unidades, tarifas, disponibilidad (8082).
├── ms-andesstay-notify/         Consume RabbitMQ: email, housekeeping, voucher (8083).
├── ms-andesstay-audit/          Consume Kafka: timeline de auditoría (8084).
├── ms-andesstay-report/         Consume Kafka: KPIs (8085).
├── ms-andesstay-rabbitmq-admin/ API administrativa de RabbitMQ (8086).
├── ms-andesstay-kafka-admin/    API administrativa de Kafka (8087).
├── frontend/                    React + Vite (5173).
└── infra/                       Docker Compose: apps/ · mq/ · kafka/
```

## Flujo

```
Usuario → Azure AD (MSAL, correo + contraseña) → id_token con roles
  → Frontend React → BFF (valida JWT)
       → reservations ──REST──→ catalog        (disponibilidad)
              ├──RabbitMQ──→ notify             (email / housekeeping / voucher, con DLQ)
              └──Kafka─────→ audit y report     (timeline y KPIs)
       → catalog / audit / report (lecturas)
```

## Probarlo en local, sin Docker (lo más rápido)

Cada microservicio es un proyecto Maven independiente. Mínimo para usar la app:

| Orden | Servicio | Cómo |
|---|---|---|
| 1 | catalog | `cd ms-andesstay-catalog && mvn spring-boot:run` |
| 2 | reservations | `cd ms-andesstay-reservations && mvn spring-boot:run` |
| 3 | audit | `cd ms-andesstay-audit && mvn spring-boot:run` |
| 4 | report | `cd ms-andesstay-report && mvn spring-boot:run` |
| 5 | bff | `cd ms-andesstay-bff && mvn spring-boot:run` |
| 6 | frontend | `cd frontend && npm install && npm run dev` |

(En VS Code: abre cada `*Application.java` y usa el botón **Run**.)
`notify`, `rabbitmq-admin` y `kafka-admin` son opcionales y requieren RabbitMQ / Kafka.

Sin RabbitMQ ni Kafka corriendo, reservas y catálogo funcionan igual: verás
avisos "No se pudo publicar en Kafka/RabbitMQ" en el log de reservations
(esperado). Auditoría y Reportería solo se llenan cuando Kafka está arriba.

## Probarlo completo con Docker

Ver `infra/README.md`:

```bash
cd infra/kafka && cp .env.example .env && docker compose up -d   # espera ~1 min
cd ../mq && docker compose up -d
cd ../apps && cp .env.example .env && docker compose up -d --build
```

## Login con Azure AD

Por defecto el frontend usa **login simulado** (sin Azure). Para el login real,
sigue la guía de `frontend/README.md` (App Registration, rol por usuario y 3
variables en `frontend/.env`). La validación del token en el BFF se activa con:

```
ANDESSTAY_SECURITY_ENABLED=true
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://login.microsoftonline.com/<TENANT_ID>/v2.0
```

> Importante: si alguna vez dejaste `ANDESSTAY_SECURITY_ENABLED=true` como
> variable de entorno permanente de Windows (con `setx`), el BFF exigirá token
> en todo y verás errores 401. Cámbiala a `false` o bórrala mientras desarrollas.

## Notas

- **Bases de datos:** cada microservicio con datos usa su propia H2 en memoria
  (se reinicia al apagar). Cambiar a Oracle/PostgreSQL = agregar el driver al
  `pom.xml` y editar el `datasource` del `application.yml` de ese servicio.
- **Seguridad:** solo el BFF valida tokens; los demás servicios viven en la
  red interna y no deben exponerse al exterior.
