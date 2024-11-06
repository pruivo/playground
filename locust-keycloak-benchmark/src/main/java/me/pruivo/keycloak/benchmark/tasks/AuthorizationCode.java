package me.pruivo.keycloak.benchmark.tasks;

import com.github.myzhan.locust4j.AbstractTask;
import com.github.myzhan.locust4j.Locust;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AuthorizationCode extends AbstractTask {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationCode.class);
    private static final Pattern LOGIN_FORM_URI_PATTERN = Pattern.compile("action=\"([^\"]*)\"");
    private static final Pattern ID_TOKEN_PATTERN = Pattern.compile("\"id_token\":\"([^\"]*)\"");
    private static final String LOGIN_ENDPOINT = "/protocol/openid-connect/auth";
    private static final String TOKEN_ENDPOINT = "/protocol/openid-connect/token";
    private static final String LOGOUT_ENDPOINT = "/protocol/openid-connect/logout";

    private final String baseUrl;
    private final String clientRedirectUrl;

    public AuthorizationCode(String baseUrl, String realm) {
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 2);
        }
        this.baseUrl = "%s/realms/%s".formatted(baseUrl, realm);
        clientRedirectUrl = "%s/account".formatted(baseUrl);
    }

    @Override
    public int getWeight() {
        return 0;
    }

    @Override
    public String getName() {
        return "AuthorizationCode";
    }

    @Override
    public void execute() throws Exception {
        try (var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.NEVER)
                .build()) {
            var id = ThreadLocalRandom.current().nextInt(10);
            var state = new RunState(client, "user-%s".formatted(id), "user-%s-password".formatted(id), "client-%s".formatted(id), "client-%s-secret".formatted(id));
            openLoginPage(state);
            if (state.abort) {
                return;
            }
            loginUsernamePassword(state);
            if (state.abort) {
                return;
            }
            exchangeCode(state);
            if (state.abort) {
                return;
            }
            logout(state);
        }
    }

    @Override
    public void onStart() throws Exception {
        super.onStart();
        logger.info("Starting Authorization Code scenario");
    }

    @Override
    public void onStop() {
        super.onStop();
        logger.info("Stopping Authorization Code scenario");
    }

    private void openLoginPage(RunState state) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Open Login Page");
        Objects.requireNonNull(state.clientId);
        Objects.requireNonNull(state.state);
        Objects.requireNonNull(state.scope);
        var parameters = Map.of(
                "login", "true",
                "response_type", "code",
                "client_id", state.clientId,
                "state", state.state,
                "redirect_uri", clientRedirectUrl,
                "scope", state.scope
        );
        var req = create(baseUrl, LOGIN_ENDPOINT, parameters).GET().build();

        var start = System.nanoTime();
        HttpResponse<String> rsp = state.httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        var durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);


        if (rsp.statusCode() != 200) {
            Locust.getInstance().recordFailure("Browser to Log In Endpoint", getName(), durationMillis, "Wrong status code: " + rsp.statusCode());
            state.abort = true;
            return;
        }

        var matches = LOGIN_FORM_URI_PATTERN.matcher(rsp.body());
        if (matches.find()) {
            state.loginFormAction = matches.group(1).replaceAll("&amp;", "&");
        }
        Locust.getInstance().recordSuccess("Browser to Log In Endpoint", getName(), durationMillis, rsp.headers().firstValueAsLong("content-length").orElse(0));
    }

    private void loginUsernamePassword(RunState state) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Login Username/Password");
        Objects.requireNonNull(state.username);
        Objects.requireNonNull(state.password);
        Objects.requireNonNull(state.loginFormAction);
        var formParam = encodeParameters(Map.of(
                "username", state.username,
                "password", state.password,
                "login", "Log in"
        ));
        var req = create(state.loginFormAction)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formParam))
                .build();

        var start = System.nanoTime();
        HttpResponse<String> rsp = state.httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        var durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        var contentLength = rsp.headers().firstValueAsLong("content-length").orElse(0);

        if (rsp.statusCode() != 302) {
            Locust.getInstance().recordFailure("Browser posts correct credentials", getName(), durationMillis, "Wrong status code: " + rsp.statusCode());
            state.abort = true;
            return;
        }

        Locust.getInstance().recordSuccess("Browser posts correct credentials", getName(), durationMillis, contentLength);
        var location = rsp.headers().firstValue("Location");
        if (location.isPresent()) {
            state.redirectLogin = location.get();
            var codeStart = location.get().indexOf("code=");
            if (codeStart > -1) {
                state.code = location.get().substring(codeStart + "code=".length());
            }
        }
    }

    private void exchangeCode(RunState state) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Exchange Code");
        Objects.requireNonNull(state.clientId);
        Objects.requireNonNull(state.clientSecret);
        Objects.requireNonNull(state.redirectLogin);
        Objects.requireNonNull(state.code);

        var formParam = encodeParameters(Map.of(
                "grant_type", "authorization_code",
                "client_id", state.clientId,
                "client_secret", state.clientSecret,
                "redirect_uri", clientRedirectUrl,
                "code", state.code
        ));

        var req = create(baseUrl, TOKEN_ENDPOINT, Map.of())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formParam))
                .build();

        var start = System.nanoTime();
        HttpResponse<String> rsp = state.httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        var durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        var contentLength = rsp.headers().firstValueAsLong("content-length").orElse(0);

        if (rsp.statusCode() != 200) {
            Locust.getInstance().recordFailure("Exchange Code", getName(), durationMillis, "Wrong status code: " + rsp.statusCode());
            state.abort = true;
            return;
        }

        Locust.getInstance().recordSuccess("Exchange Code", getName(), durationMillis, contentLength);

        var matches = ID_TOKEN_PATTERN.matcher(rsp.body());
        if (matches.find()) {
            state.idToken = matches.group(1);
        }
    }

    private void logout(RunState state) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Logout");
        Objects.requireNonNull(state.clientId);
        Objects.requireNonNull(state.idToken);
        var parameters = Map.of(
                "client_id", state.clientId,
                "post_logout_redirect_uri", clientRedirectUrl,
                "id_token_hint", state.idToken
        );

        var req = create(baseUrl, LOGOUT_ENDPOINT, parameters).GET().build();

        var start = System.nanoTime();
        HttpResponse<String> rsp = state.httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        var durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);


        if (rsp.statusCode() != 302) {
            Locust.getInstance().recordFailure("Browser logout", getName(), durationMillis, "Wrong status code: " + rsp.statusCode());
            state.abort = true;
            return;
        }

        Locust.getInstance().recordSuccess("Browser logout", getName(), durationMillis, rsp.headers().firstValueAsLong("content-length").orElse(0));

    }

    private static HttpRequest.Builder create(String host, String path, Map<String, String> parameters) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(createURI(host, path, parameters))
                .setHeader("Accept", "text/html,application/xhtml+xml,application/xml")
                .setHeader("Accept-Encoding", "gzip, deflate")
                .setHeader("Accept-Language", "en-US,en;q=0.5")
                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
                .timeout(Duration.ofSeconds(10));
    }

    private static HttpRequest.Builder create(String url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(url))
                .setHeader("Accept", "text/html,application/xhtml+xml,application/xml")
                .setHeader("Accept-Encoding", "gzip, deflate")
                .setHeader("Accept-Language", "en-US,en;q=0.5")
                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
                .timeout(Duration.ofSeconds(10));
    }

    private static URI createURI(String host, String path, Map<String, String> parameters) throws URISyntaxException {
        if (parameters.isEmpty()) {
            return new URI("%s%s".formatted(host, path));
        }
        return new URI("%s%s?%s".formatted(host, path, encodeParameters(parameters)));
    }

    private static String encodeParameters(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(AuthorizationCode::encodeParameter)
                .collect(Collectors.joining("&"));
    }

    private static String encodeParameter(Map.Entry<String, String> entry) {
        var charset = Charset.defaultCharset();
        return "%s=%s".formatted(entry.getKey(), URLEncoder.encode(entry.getValue(), charset));
    }

    private static class RunState {
        final HttpClient httpClient;
        final String username;
        final String password;
        final String clientId;
        final String clientSecret;
        final String state;
        final String scope;
        String loginFormAction;
        String redirectLogin;
        String code;
        String idToken;
        boolean abort;

        RunState(HttpClient httpClient, String username, String password, String clientId, String clientSecret) {
            this.httpClient = httpClient;
            this.username = username;
            this.password = password;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            state = UUID.randomUUID().toString();
            scope = "openid profile";
        }
    }
}
