package ar.edu.unju.fi.poo.apprest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class ApirestApplication {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST - Sistema de Ventas y Gestion Comercial Grupo 5")
                        .version("1.0.0")
                        .description("Documentacion interactiva de los servicios REST para gestion de Categorias, Productos, Clientes, Ventas (Contado y Financiadas), Estado de Cuenta y Cobro de Cuotas - POO 2026.")
                        .contact(new Contact()
                                .name("Grupo 5 - APU Extension Aulica San Pedro")
                                .email("aravenad782@fi.unju.edu.ar")));
    }
}