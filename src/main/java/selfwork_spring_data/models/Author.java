package selfwork_spring_data.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // Indica a JPA che questa classe rappresenta un'entità/tabella
@Table(name = "authors") // Specifica il nome esatto della tabella nel DB
public class Author {

    @Id // Imposta questo attributo come chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifica che l'ID è autoincrementale gestito da MySQL 
    private Long id;

    @Column(name = "first_name", nullable = true) // Associa l'attributo name alla colonna first_name
    private String name;

    @Column(name = "last_name", nullable = true) // Associa l'attributo surname alla colonna last_name
    private String surname;

    @Column(name = "email", nullable = false, unique = true) // Obbligatorio e univoco nel DB
    private String email;

    // Relazione uno-a-molti: un autore ha molti post.
    // "mappedBy" indica la variabile 'author' presente nella classe Post che gestisce la chiave esterna
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>(); // Inizializzato con una lista vuota per evitare valori nulli

    // Costruttore vuoto obbligatorio per JPA
    public Author() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
}
