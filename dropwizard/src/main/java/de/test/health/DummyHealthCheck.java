package de.test.health;

import com.codahale.metrics.health.HealthCheck;

public class DummyHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
//        final String saying = String.format(template, "TEST");
//        if (!saying.contains("TEST")) {
//            return Result.unhealthy("template doesn't include a name");
//        }
        return Result.healthy();
    }
}
