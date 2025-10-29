package com.uade.ligafutbol.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class Neo4jInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Neo4jInitializer.class);

    private final Driver driver;

    public Neo4jInitializer(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            ClassPathResource resource = new ClassPathResource("neo4j/init.cypher");
            if (!resource.exists()) {
                logger.info("No neo4j/init.cypher found on classpath; skipping DB init.");
                return;
            }

            String content;
            try (BufferedReader r = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                content = r.lines().collect(Collectors.joining("\n"));
            }

            // remove // comments
            String withoutComments = content.lines()
                    .filter(line -> !line.trim().startsWith("//"))
                    .collect(Collectors.joining("\n"));

            // split statements by semicolon (simple split, then trim)
            String[] statements = withoutComments.split(";");

            try (Session session = driver.session()) {
                for (String stmt : statements) {
                    String s = stmt.trim();
                    if (s.isEmpty()) continue;
                    try {
                        logger.info("Applying Cypher statement: {}", s.replaceAll("\\n"," "));
                        // run statement and explicitly consume the result to avoid driver errors
                        session.executeWrite(tx -> {
                            var result = tx.run(s);
                            // ensure the result is fully consumed
                            result.consume();
                            return null;
                        });
                    } catch (Exception e) {
                        logger.warn("Failed to run statement: {} -> {}", s, e.getMessage());
                    }
                }
            }

            logger.info("Neo4j init (init.cypher) processing complete.");
        } catch (Exception e) {
            logger.error("Error while applying neo4j/init.cypher: {}", e.getMessage(), e);
        }
    }
}
