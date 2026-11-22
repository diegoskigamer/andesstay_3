# Servidor de eventos — Kafka

```bash
cd infra/kafka
cp .env.example .env   # ajusta KAFKA_ADVERTISED_HOST si las apps corren en otro servidor
docker compose up -d   # espera ~1 min
```

Un solo broker en modo KRaft (sin Zookeeper).

## Despliegue en AWS (EC2)

Misma idea que la instancia de RabbitMQ: Kafka corre en su propia EC2 y las apps lo alcanzan por IP privada (misma VPC).

1. **Instancia:** Amazon Linux 2023 o Ubuntu, mínimo `t3.small` (2 GB RAM; con 1 GB Kafka no arranca bien), 10 GB de disco.
2. **Docker:**
   ```bash
   # Amazon Linux 2023
   sudo dnf install -y docker git && sudo systemctl enable --now docker
   sudo usermod -aG docker ec2-user   # cierra sesión y vuelve a entrar
   sudo mkdir -p /usr/local/lib/docker/cli-plugins
   sudo curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
        -o /usr/local/lib/docker/cli-plugins/docker-compose
   sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
   ```
3. **Configura el host anunciado** con la IP privada de ESTA instancia:
   ```bash
   cd infra/kafka
   cp .env.example .env
   # edita .env:  KAFKA_ADVERTISED_HOST=172.31.x.x
   docker compose up -d
   ```
4. **Security Group** de la instancia de Kafka: entrada TCP **19092** solo desde el Security Group de la instancia de apps. Nunca `0.0.0.0/0`.
5. **En la instancia de apps**, en `infra/apps/.env`:
   ```
   KAFKA_BOOTSTRAP_SERVERS=172.31.x.x:19092
   ```
   y luego `docker compose up -d --build`.
6. **Verifica la conexión** desde la instancia de apps: `nc -zv 172.31.x.x 19092`.

> Si apagas y vuelves a encender la EC2, la IP privada se mantiene; si la **terminas** y creas otra, cambia la IP en ambos `.env`.

## Acceso

- Desde las apps (`KAFKA_BOOTSTRAP_SERVERS`): `host.docker.internal:19092` (mismo equipo) o `<IP-privada-de-la-EC2>:19092` (AWS).
- Firewall: **19092** solo desde el servidor de aplicaciones.

## Topic que usa AndesStay

`reservations.events` (3 particiones). Lo crea `ms-andesstay-reservations` al arrancar.

| Rol | Microservicio | Grupo |
|---|---|---|
| Productor | `ms-andesstay-reservations` | — |
| Consumidor | `ms-andesstay-audit` | `ms-andesstay-audit` |
| Consumidor | `ms-andesstay-report` | `ms-andesstay-report` |

## Comprobar mensajes

```bash
docker exec -it andesstay-kafka-kafka-1 /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 --topic reservations.events --from-beginning
```
