package Login;

import Login.Modelo_basico.Usuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String emailPadrao = "admin@email.com";

            if (usuarioRepository.findByEmail(emailPadrao).isEmpty()) {
                Usuario admin = new Usuario(
                        "Administrador",
                        emailPadrao,
                        passwordEncoder.encode("12345678")
                );

                usuarioRepository.save(admin);
                System.out.println(">>> Utilizador padrão criado com sucesso!");
                System.out.println(">>> E-mail: admin@email.com | Senha: 12345678");
            }
        };
    }
}