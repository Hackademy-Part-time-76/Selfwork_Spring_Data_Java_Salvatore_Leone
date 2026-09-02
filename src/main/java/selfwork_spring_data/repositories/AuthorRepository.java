package selfwork_spring_data.repositories;

import selfwork_spring_data.models.Author;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

// Specifichiamo l'entità (Author) e il tipo della chiave primaria (Long)
public interface AuthorRepository extends CrudRepository<Author, Long> {

    // ---  DERIVED QUERIES  ---
    // Hibernate legge il nome del metodo e genera la query automatica filtro

    // Trova un autore tramite l'email
    Author findByEmail(String email);

    // Trova autori per nome e cognome
    List<Author> findByNameAndSurname(String name, String surname);


    // ---  NATIVE QUERY  ---
    // Usiamo l'annotazione @Query indicando nativeQuery = true per scrivere in SQL
    @Query(value = "SELECT * FROM authors WHERE first_name = ?1", nativeQuery = true)
    List<Author> authorsWithSameName(String name);


    // ---  JPQL QUERY  ---
    // Usa le classi Java e i loro attributi al posto di tabelle e colonne SQL
    @Query("SELECT a FROM Author a WHERE a.name = ?1")
    List<Author> findAuthorsByJpqlName(String name);
}
