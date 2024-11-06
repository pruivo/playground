package me.pruivo.keycloak.benchmark;

import com.github.myzhan.locust4j.Locust;
import me.pruivo.keycloak.benchmark.tasks.AuthorizationCode;

/**
 * Hello world!
 */
public class KeycloakBenchmark {

    public static void main(String[] args) {
        var locust = Locust.getInstance();
        locust.setMasterHost("127.0.0.1");
        locust.setMasterPort(5557);
        locust.setMaxRPS(10);
        locust.run(new AuthorizationCode("http://keycloak:8080", "realm-0"));
    }
}
