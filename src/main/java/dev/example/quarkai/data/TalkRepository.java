package dev.example.quarkai.data;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TalkRepository {

    private static final List<Talk> TALKS = List.of(
            new Talk(1, "Architecture", "Building Modular Monoliths with Quarkus", "Sandra Parsick", "EN"),
            new Talk(2, "AI", "Practical LLM Integration in Java Applications", "Lize Raes", "EN"),
            new Talk(3, "Cloud", "Kubernetes-Native Java with GraalVM", "Thomas Würthinger", "EN"),
            new Talk(4, "Security", "Zero Trust Architecture for Microservices", "Daniel Deogun", "EN"),
            new Talk(5, "Frontend", "Server-Side Web UIs with Vaadin Flow", "Sebastian Kühnau", "EN"),
            new Talk(6, "AI", "RAG Patterns for Enterprise Search", "Julien Dubois", "EN"),
            new Talk(7, "Architecture", "Von Monolith zu Microservices – ein Erfahrungsbericht", "Eberhard Wolff", "DE"),
            new Talk(8, "Cloud", "Serverless Java on AWS Lambda", "Adam Bien", "DE"),
            new Talk(9, "Security", "Sichere APIs mit OAuth2 und OpenID Connect", "Niko Köbler", "DE"),
            new Talk(10, "Frontend", "Building Accessible Web Applications", "Marcus Hellberg", "EN"),
            new Talk(11, "AI", "Fine-Tuning LLMs for Domain-Specific Tasks", "Hanno Embregts", "EN"),
            new Talk(12, "Architecture", "Event-Driven Systems with Apache Kafka", "Gunnar Morling", "EN"),
            new Talk(13, "Cloud", "Cost-Optimized Cloud Deployments", "Burr Sutter", "EN"),
            new Talk(14, "Security", "Dependency Vulnerability Scanning in CI/CD", "Brian Vermeer", "EN"),
            new Talk(15, "Frontend", "Reactive UIs mit Vaadin Signals", "Leif Åstrand", "DE"),
            new Talk(16, "AI", "Agenten-basierte Architekturen mit LangChain4j", "Dmytro Liubarskyi", "DE")
    );

    public List<Talk> findAll() {
        return TALKS;
    }

    public Optional<Talk> findById(int id) {
        return TALKS.stream()
                .filter(talk -> talk.id() == id)
                .findFirst();
    }
}