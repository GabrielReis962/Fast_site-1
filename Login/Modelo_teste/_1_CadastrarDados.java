package Login.Modelo_teste;

import Login.Modelo_basico.Login;
import Login.Modelo_basico.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class _1_CadastrarDados {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Usuario usuario1 = new Usuario("Carlos Silva", "carlos@gmail.com", "$2a$10$e74409395f4e");
        Usuario usuario2 = new Usuario("Ana Souza", "ana@gmail.com", "$2a$10$f85510406g5f");

        Login livro1 = new Login("Java para Iniciantes", "Herbert Schildt", "Geral", "978-85-8055-000-1", 5);
        Login livro2 = new Login("Clean Code", "Robert C. Martin", "Geral", "978-85-7608-267-5", 2);

        em.persist(usuario1);
        em.persist(usuario2);
        em.persist(livro1);
        em.persist(livro2);

        em.getTransaction().commit();

        System.out.println("Usuários e Livros cadastrados com sucesso!");

        em.close();
        emf.close();
    }
}