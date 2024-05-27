package fintech.bo.components;

import com.vaadin.spring.server.SpringVaadinServlet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableScheduling
@Configuration
public class SpringConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }

    @Component("vaadinServlet")
    public class BoSpringVaadinServlet extends SpringVaadinServlet {

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();

            getService().addSessionInitListener(new VaadinSessionListener.VaadinSessionInitListener());
            getService().addSessionDestroyListener(new VaadinSessionListener.VaadinSessionDestroyListener());
        }
    }
}
