package fintech.db.impl;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(
        basePackageClasses = {Jsr310JpaConverters.class},
        basePackages = {"fintech"}
)
@EnableJpaRepositories(basePackages = {"fintech"},
        repositoryBaseClass = BaseRepositoryImpl.class
)
@Configuration
public class JpaConfiguration {

}
