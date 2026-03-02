package com.airline.conf;

import com.airline.controllers.*;
import com.airline.facade.*;
import com.google.inject.AbstractModule;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;
import ninja.utils.NinjaProperties;

import java.util.Properties;

public class Module extends AbstractModule {
    private final NinjaProperties ninjaProperties;

    public Module() {
        this.ninjaProperties = null;
    }

    public Module(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    protected void configure() {
        // Install the persistence private module
        install(new PersistenceModule(ninjaProperties));
        bind(PersistenceLifecycle.class).asEagerSingleton();

        // Bind Auth
        bind(AuthFacade.class).to(AuthFacadeImpl.class).in(Singleton.class);
        bind(AuthController.class).to(AuthControllerImpl.class).in(Singleton.class);

        // Bind Flight
        bind(FlightFacade.class).to(FlightFacadeImpl.class).in(Singleton.class);
        bind(FlightController.class).to(FlightControllerImpl.class).in(Singleton.class);

        // Bind Booking
        bind(BookingFacade.class).to(BookingFacadeImpl.class).in(Singleton.class);
        bind(BookingController.class).to(BookingControllerImpl.class).in(Singleton.class);

        // Bind CORS
        bind(CorsController.class).to(CorsControllerImpl.class).in(Singleton.class);
    }

    /**
     * Private module that sets up JPA persistence.
     * Reads DB config and installs JpaPersistModule.
     */
    static class PersistenceModule extends PrivateModule {
        private final NinjaProperties ninjaProperties;

        PersistenceModule(NinjaProperties ninjaProperties) {
            this.ninjaProperties = ninjaProperties;
        }

        @Override
        protected void configure() {

            Properties jpaProps = new Properties();

            // JPA JDBC properties
            jpaProps.put("javax.persistence.jdbc.url",
                    getProperty("db.connection.url",
                            "jdbc:postgresql://localhost:5432/airline_db"));

            jpaProps.put("javax.persistence.jdbc.user",
                    getProperty("db.connection.username", "airline_user"));

            jpaProps.put("javax.persistence.jdbc.password",
                    getProperty("db.connection.password", "airline123"));

            // Hibernate settings
            jpaProps.put("hibernate.hbm2ddl.auto",
                    getProperty("hibernate.hbm2ddl.auto", "validate"));
            jpaProps.put("hibernate.dialect",
                    "org.hibernate.dialect.PostgreSQLDialect");
            jpaProps.put("hibernate.show_sql", "true");

            install(new JpaPersistModule("postgres").properties(jpaProps));

            // Expose required bindings
            expose(javax.persistence.EntityManager.class);
            expose(com.google.inject.persist.UnitOfWork.class);
            expose(com.google.inject.persist.PersistService.class);
        }

        private String getProperty(String key, String defaultValue) {
            String val = System.getProperty(key);
            if ((val == null || val.isEmpty()) && ninjaProperties != null) {
                val = ninjaProperties.get(key);
            }
            return (val != null && !val.isEmpty()) ? val : defaultValue;
        }
    }
}
