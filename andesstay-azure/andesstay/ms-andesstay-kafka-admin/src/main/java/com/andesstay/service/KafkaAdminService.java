package com.andesstay.service;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaAdminService {
    @Value("${spring.kafka.bootstrap-servers}") private String bootstrapServers;

    private Admin newClient() {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 5000);
        return Admin.create(props);
    }

    public Map<String, Object> overview() {
        try (Admin admin = newClient()) {
            DescribeClusterResult cluster = admin.describeCluster();
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("clusterId", cluster.clusterId().get(5, TimeUnit.SECONDS));
            out.put("brokers", cluster.nodes().get(5, TimeUnit.SECONDS).size());
            out.put("bootstrapServers", bootstrapServers);
            return out;
        } catch (Exception e) { throw unavailable(e); }
    }

    public List<Map<String, Object>> listTopics() {
        try (Admin admin = newClient()) {
            Set<String> names = admin.listTopics().names().get(5, TimeUnit.SECONDS);
            Map<String, TopicDescription> descriptions = admin.describeTopics(names).allTopicNames().get(5, TimeUnit.SECONDS);
            List<Map<String, Object>> out = new ArrayList<>();
            new TreeMap<>(descriptions).forEach((name, d) -> {
                Map<String, Object> t = new LinkedHashMap<>();
                t.put("name", name);
                t.put("partitions", d.partitions().size());
                t.put("replicas", d.partitions().isEmpty() ? 0 : d.partitions().get(0).replicas().size());
                t.put("internal", d.isInternal());
                out.add(t);
            });
            return out;
        } catch (Exception e) { throw unavailable(e); }
    }

    public List<Map<String, Object>> listConsumerGroups() {
        try (Admin admin = newClient()) {
            List<Map<String, Object>> out = new ArrayList<>();
            for (ConsumerGroupListing g : admin.listConsumerGroups().all().get(5, TimeUnit.SECONDS)) {
                Map<String, OffsetAndMetadata> offsets = admin.listConsumerGroupOffsets(g.groupId())
                        .partitionsToOffsetAndMetadata().get(5, TimeUnit.SECONDS)
                        .entrySet().stream().collect(java.util.stream.Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
                long committed = offsets.values().stream().mapToLong(OffsetAndMetadata::offset).sum();
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("groupId", g.groupId());
                m.put("state", g.state().map(Enum::toString).orElse("UNKNOWN"));
                m.put("partitions", new TreeSet<>(offsets.keySet()));
                m.put("committedOffsets", committed);
                out.add(m);
            }
            return out;
        } catch (Exception e) { throw unavailable(e); }
    }

    private ResponseStatusException unavailable(Exception e) {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Kafka no disponible: " + e.getMessage());
    }
}
