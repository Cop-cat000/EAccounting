package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;

import main.Bot;
import managers.CommandManager;

@Configuration
@ComponentScan(basePackages = { "main", "managers", "utils" })
public class ProjectConfig {
    
    @Bean
    public Bot bot(CommandManager commandManager) {
        return new Bot(ProjectConfigData.botToken, commandManager);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(
            ProjectConfigData.customPersistenceUnitInfo,
            ProjectConfigData.hibernateProperties);
    }
}
