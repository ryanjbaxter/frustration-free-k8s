package org.example.frustrationfreek8s;

import java.util.Collections;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.test.context.DynamicPropertyRegistry;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	@RestartScope
	MySQLContainer<?> mysqlContainer() {
		return new MySQLContainer<>(DockerImageName.parse("mysql:latest"));
	}

	@Bean
	public DiscoveryClient testContaiersDiscoveryClient(GenericContainer<?> nameServiceContainer, DynamicPropertyRegistry registry) {
		SimpleDiscoveryProperties simpleDiscoveryProperties = new SimpleDiscoveryProperties();
		simpleDiscoveryProperties.setInstances(Collections.singletonMap("k8s-workshop-name-service", Collections.singletonList(new DefaultServiceInstance("k8s-workshop-name-service", "k8s-workshop-name-service", nameServiceContainer.getHost(), nameServiceContainer.getMappedPort(8080), false))));
		return new TestContainersDiscoveryClient(simpleDiscoveryProperties);
	}

	@Bean
	@RestartScope
	public GenericContainer<?> nameServiceContainer() {
		return new GenericContainer<>(DockerImageName.parse("ryanjbaxter/k8s-workshop-name-service-arm64:latest")).withEnv("EUREKA_CLIENT_ENABLED", "false").withEnv("INSTANCE_HOSTNAME", "localhost").withExposedPorts(8080);
	}

		}

	// This could be implemented in a much better way but gets the idea across
	class TestContainersDiscoveryClient extends SimpleDiscoveryClient implements Ordered {

		@Override
		public int getOrder() {
			return HIGHEST_PRECEDENCE;
		}

		public TestContainersDiscoveryClient(SimpleDiscoveryProperties simpleDiscoveryProperties) {
			super(simpleDiscoveryProperties);
		}

		@Override
		public String description() {
			return "TestContainers Discovery Client";
		}
	}
