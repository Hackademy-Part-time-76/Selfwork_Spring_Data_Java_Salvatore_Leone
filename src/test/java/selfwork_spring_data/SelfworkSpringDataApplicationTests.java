package selfwork_spring_data;

import selfwork_spring_data.models.Author;
import selfwork_spring_data.models.Post;
import selfwork_spring_data.repositories.AuthorRepository;
import selfwork_spring_data.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Diciamo a Spring di caricare solo il livello JPA/Database, senza avviare tutta l'applicazione web.
// Alla fine di ogni singolo test viene fatto un rollback automatico.
@DataJpaTest
// Non sostituire il mio database con un DB in-memory (come H2). Usa il database MySQL vero definito nel properties".
@AutoConfigureTestDatabase(replace = Replace.NONE)
class SelfworkSpringDataApplicationTests {

    // Iniettiamo i repository reali generati da Spring Data JPA.
    // Query SQL senza dover creare le classi di implementazione a mano.
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PostRepository postRepository;

    // Dichiariamo delle variabili d'istanza per riutilizzarle tra i metodi se ne ho bisogno.
    private Author author1;
    private Author author2;

    // Ogni test parte da uno stato pulito e con dati di prova ben definiti.
    @BeforeEach
    void setUp() {
        System.out.println("--- LOADING INITIAL TEST DATA ---");

        // Crea e salvo il primo autore nel database
        author1 = new Author();
        author1.setName("Giuseppe");
        author1.setSurname("Verdi");
        author1.setEmail("verdi.giuseppe@test.it");
        authorRepository.save(author1); // Qui creo la riga sulla tabella "authors"

        // Crea e salvo il secondo autore
        author2 = new Author();
        author2.setName("Mario");
        author2.setSurname("Rossi");
        author2.setEmail("mario.rossi@test.it");
        authorRepository.save(author2);

        // Crea un terzo autore (questo non ha post associati)
        Author author3 = new Author();
        author3.setName("Luigi");
        author3.setSurname("Bianchi");
        author3.setEmail("luigi.bianchi@test.it");
        authorRepository.save(author3);

        // Crea i post collegando la Foreign Key (Author) alle entità degli autori creati sopra

        Post p1 = new Post();
        p1.setTitle("Primo post di Giuseppe");
        p1.setPublishDate(LocalDateTime.now());
        p1.setAuthor(author1); // Associamo Giuseppe come autore
        postRepository.save(p1);

        Post p2 = new Post();
        p2.setTitle("Post di Mario");
        p2.setPublishDate(LocalDateTime.now());
        p2.setAuthor(author2); // Associamo Mario
        postRepository.save(p2);

        Post p3 = new Post();
        p3.setTitle("Secondo post di Giuseppe");
        p3.setPublishDate(LocalDateTime.now());
        p3.setAuthor(author1); // Associamo di nuovo Giuseppe
        postRepository.save(p3);
    }

    // TEST 1: Verifichiamo la conta degli elementi
    @Test
    void testCountAuthors() {
        System.out.println("--- RUNNING: testCountAuthors ---"); 
        
        // Il metodo count() fornito da JpaRepository (fa SELECT COUNT(*) FROM authors)
        long count = authorRepository.count();
        
        // AssertJ: Verifico che la conta restituita sia esattamente 3 (i 3 autori inseriti)
        assertThat(count).isEqualTo(3);
    }

    // TEST 2: Verifichiamo una query di ricerca personalizzata per Email
    @Test
    void testFindAuthorByEmail() {
        System.out.println("--- RUNNING: testFindAuthorByEmail ---"); 
        
        // Chiamiamo il metodo definito nel repository con la convenzione di nome "findByEmail"
        Author found = authorRepository.findByEmail("verdi.giuseppe@test.it");

        assertThat(found).isNotNull();
        
        // Estrae i campi Name e Surname e verifica che corrispondano a "Giuseppe Verdi"
        assertThat(found)
            .extracting(Author::getName, Author::getSurname)
            .containsExactly("Giuseppe", "Verdi");
    }

    // TEST 3: Verifico le relazioni tra Post e Autore
    @Test
    void testPostAuthorRelationship() {
        System.out.println("--- RUNNING: testPostAuthorRelationship ---"); 
        
        // Prendo tutti i post salvati (3 post totali)
        Iterable<Post> posts = postRepository.findAll(); 

        // AssertJ naviga dentro la collezione dei post:
        // 1. Per ogni post estrae l'oggetto Author associato
        // 2. Per ogni Author estrae il Nome
        // 3. Verifica che tra tutti i post trovati gli unici autori siano "Giuseppe" e "Mario"
        assertThat(posts)
            .extracting(Post::getAuthor) 
            .extracting(Author::getName) 
            .containsOnly("Giuseppe", "Mario");
    }

    // TEST 4: Verifichiamo l'eliminazione di un record
    @Test
    void testDeletePost() {
        System.out.println("--- RUNNING: testDeletePost ---");
        
        // Recuperiamo i post attuali e ne prendiamo uno qualsiasi usando l'iteratore
        Iterable<Post> postsBefore = postRepository.findAll();
        Post postToDelete = postsBefore.iterator().next();

        // Elimina il post preso dal DB
        postRepository.delete(postToDelete);

        // Rilanciamo la query di select all e verifichiamo che la dimensione della lista si sia ridotta a 2
        Iterable<Post> postsAfter = postRepository.findAll();
        assertThat(postsAfter).hasSize(2);
    }

    // TEST 5: Verifichiamo l'aggiornamento di una riga (UPDATE)
    @Test
    void testUpdatePost() {
        System.out.println("--- RUNNING: testUpdatePost ---");
        
        // Prende un post dal DB
        Iterable<Post> posts = postRepository.findAll();
        Post postToUpdate = posts.iterator().next();

        // Modifica il campo title in memoria dell'oggetto Java
        postToUpdate.setTitle("Titolo 1 modificato");
        
        // Salva di nuovo l'entità. Spring Data capisce che l'oggetto ha già una Primary Key (ID) e farà una UPDATE invece di una INSERT
        postRepository.save(postToUpdate);

        // Ripesca lo stesso post dal DB usando il suo ID ed assicura che la modifica sia stata presa dal DB
        Post updatedPost = postRepository.findById(postToUpdate.getId()).orElse(null);
        
        assertThat(updatedPost).isNotNull();
        assertThat(updatedPost.getTitle()).isEqualTo("Titolo 1 modificato");
    }

    // TEST 6: Verifichiamo le query personalizzate (Native SQL e JPQL)
    @Test
    void testCustomQueries() {
        System.out.println("--- RUNNING: testCustomQueries ---");

        // Primo test: Metodo con query SQL Nativa definita sul Repository
        List<Author> nativeResult = authorRepository.authorsWithSameName("Giuseppe");
        assertThat(nativeResult).hasSize(1);
        assertThat(nativeResult.get(0).getSurname()).isEqualTo("Verdi");

        // Secondo test: Metodo con query orientata agli oggetti (JPQL)
        List<Author> jpqlResult = authorRepository.findAuthorsByJpqlName("Giuseppe");
        assertThat(jpqlResult).hasSize(1);
        assertThat(jpqlResult.get(0).getSurname()).isEqualTo("Verdi");
    }
}