# Servidor de mensajería — RabbitMQ

```bash
cd infra/mq
cp .env.example .env   # cambia la clave y la cookie
docker compose up -d
```

Levanta un nodo de RabbitMQ (`rabbitmq1`) con Management UI.

## Acceso

- AMQP (para las apps): `5672`
- Management UI: http://localhost:15672 (usuario/clave del `.env`)

## Firewall (si lo despliegas en un servidor)

- **5672** (AMQP): solo desde el servidor de aplicaciones
- **15672** (Management UI): solo desde tu IP/VPN, nunca abierto a todo Internet

## Colas que usa AndesStay

`q.cmd.email`, `q.cmd.housekeeping`, `q.cmd.voucher` (con su DLQ `q.cmd.dlq`),
declaradas automáticamente por `ms-andesstay-reservations` al arrancar.
