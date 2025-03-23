package site.easy.to.build.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Autorise les endpoints sous /api
                .allowedOrigins("http://localhost:5188") // URL de NewApp
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Méthodes autorisées
                .allowedHeaders("*") // Tous les en-têtes autorisés
                .allowCredentials(true); // Autorise les cookies et les en-têtes d'authentification
    }
}
