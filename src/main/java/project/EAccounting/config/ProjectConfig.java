package project.EAccounting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;

import project.EAccounting.managers.CommandManager;

@Configuration
public class ProjectConfig {
    
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(
            ProjectConfigData.customPersistenceUnitInfo,
            ProjectConfigData.hibernateProperties);
    }
}
