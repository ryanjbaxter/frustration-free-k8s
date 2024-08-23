package org.example.frustrationfreek8s;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import org.springframework.boot.SpringApplication;

public class TestFrustrationFreeK8sApplication {

	public static void main(String[] args) {
		// We need to start the config server before the application because spring.config.imports are processed before Boot starts
		// any testcontainers that are beans.
		GenericContainer<?> configserver = new GenericContainer<>(DockerImageName.parse("ryanjbaxter/spring-cloud-config-server-testcontainer:latest")).withExposedPorts(8888)
				.withCopyToContainer(MountableFile.forClasspathResource("testconfig.yaml"), "/configdata/application.yaml").waitingFor(Wait.forHttp("/application/default")).withReuse(true);
		configserver.start();
		SpringApplication.from(FrustrationFreeK8sApplication::main).with(TestcontainersConfiguration.class)
				.run("--spring.config.import=configserver:http://localhost:" + configserver.getMappedPort(8888),
						"--eureka.client.enabled=false");
	}

}
