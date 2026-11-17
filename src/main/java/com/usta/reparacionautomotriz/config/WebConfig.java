package com.usta.reparacionautomotriz.config;

import com.usta.reparacionautomotriz.entities.ModeloEntity;
import com.usta.reparacionautomotriz.entities.UserEntity;
import com.usta.reparacionautomotriz.entities.VehiculoEntity;
import com.usta.reparacionautomotriz.models.services.ModeloService;
import com.usta.reparacionautomotriz.models.services.UsuarioService;
import com.usta.reparacionautomotriz.models.services.VehiculoService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ModeloService modeloService;
    private final VehiculoService vehiculoService;
    private final UsuarioService usuarioService;

    public WebConfig(@Lazy ModeloService modeloService,
                     @Lazy VehiculoService vehiculoService,
                     @Lazy UsuarioService usuarioService) {
        this.modeloService = modeloService;
        this.vehiculoService = vehiculoService;
        this.usuarioService = usuarioService;
    }

    // El id que llega de un <select> o checkbox se convierte en la entidad
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, ModeloEntity.class,
                id -> id.isBlank() ? null : modeloService.findById(Long.valueOf(id.trim())));
        registry.addConverter(String.class, VehiculoEntity.class,
                id -> id.isBlank() ? null : vehiculoService.findById(Long.valueOf(id.trim())));
        registry.addConverter(String.class, UserEntity.class,
                id -> id.isBlank() ? null : usuarioService.findById(Long.valueOf(id.trim())));
    }

    // ---------- Internacionalización (i18n) ----------

    // Guarda el idioma elegido en la sesión; por defecto español
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.forLanguageTag("es"));
        return resolver;
    }

    // Cambia el idioma con cualquier URL que lleve ?lang=es o ?lang=en
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    // Los mensajes de validación ({val.xxx}) se leen de messages_es / messages_en
    @Bean
    public static LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}