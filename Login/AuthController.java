package Login;

import Login.Modelo_basico.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        if (request == null || isBlank(request.usuario()) || isBlank(request.senha())) {
            return error(HttpStatus.BAD_REQUEST, "E-mail e senha são obrigatórios.");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.usuario().trim())
                .filter(candidato -> passwordEncoder.matches(request.senha(), candidato.getSenhaHash()))
                .orElse(null);
        if (usuario == null) {
            return error(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }

        session.setAttribute("usuarioNome", usuario.getNome());
        return ResponseEntity.ok(Map.of("message", "Login realizado com sucesso!"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpSession session) {
        if (request == null || isBlank(request.nome()) || isBlank(request.usuario()) || isBlank(request.senha())) {
            return error(HttpStatus.BAD_REQUEST, "Nome, e-mail e senha são obrigatórios.");
        }
        if (request.senha().length() < 8) {
            return error(HttpStatus.BAD_REQUEST, "A senha deve ter pelo menos 8 caracteres.");
        }

        String email = request.usuario().trim();
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return error(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }

        Usuario usuario = new Usuario(
                request.nome().trim(),
                email,
                passwordEncoder.encode(request.senha())
        );
        try {
            usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException exception) {
            return error(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }
        session.setAttribute("usuarioNome", usuario.getNome());
        return ResponseEntity.ok(Map.of("message", "Conta criada com sucesso!"));
    }

    @GetMapping("/session")
    public ResponseEntity<?> obterSessao(HttpSession session) {
        String nome = (String) session.getAttribute("usuarioNome");
        if (nome == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "name", nome
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Sessão encerrada com sucesso."));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }

    public record LoginRequest(String usuario, String senha) {}
    public record RegisterRequest(String nome, String usuario, String senha) {}
}