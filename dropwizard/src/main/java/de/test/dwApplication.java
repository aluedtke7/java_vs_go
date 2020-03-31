package de.test;

import de.test.health.DummyHealthCheck;
import de.test.resources.LoginResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class dwApplication extends Application<dwConfiguration> {

    public static void main(final String[] args) throws Exception {
        new dwApplication().run(args);
    }

    @Override
    public String getName() {
        return "dw";
    }

    @Override
    public void initialize(final Bootstrap<dwConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final dwConfiguration configuration, final Environment environment) {
        final DummyHealthCheck healthCheck = new DummyHealthCheck();
        environment.healthChecks().register("login", healthCheck);

        final LoginResource loginResource = new LoginResource();
        environment.jersey().register(loginResource);
    }
}
