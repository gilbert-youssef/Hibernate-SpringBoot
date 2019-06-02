package com.bookstore.service;

import java.util.Set;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.AuthorRepository;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import com.bookstore.entity.Author;
import com.bookstore.entity.Book;
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookstoreService {

    private static final Logger logger
            = Logger.getLogger(BookstoreService.class.getName());

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final EntityManager em;

    public BookstoreService(AuthorRepository authorRepository,
            BookRepository bookRepository, EntityManager em) {

        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.em = em;
    }

    @Transactional
    public void registerAuthor() {

        Author author1 = new Author();
        author1.setName("Name_1");
        author1.setSurname("Surname_1");
        author1.setAge(43);

        Author author2 = new Author();
        author2.setName("Name_2");
        author2.setSurname("Surname_2");
        author2.setAge(41);

        Book book1 = new Book();
        book1.setIsbn("Isbn_1");
        book1.setTitle("Title_1");

        Book book2 = new Book();
        book2.setIsbn("Isbn_2");
        book2.setTitle("Title_2");

        Book book3 = new Book();
        book3.setIsbn("Isbn_3");
        book3.setTitle("Title_3");

        author1.addBook(book1);
        author1.addBook(book2);
        author2.addBook(book1);
        author2.addBook(book2);
        author2.addBook(book3);

        authorRepository.save(author1);
        authorRepository.save(author2);
    }

    @Transactional
    public void updateAuthor() {
        Author author = authorRepository.findByName("Name_1");

        author.setAge(45);
    }

    @Transactional
    public void updateBooks() {
        Author author = authorRepository.findByName("Name_1");
        Set<Book> books = author.getBooks();

        for (Book book : books) {
            book.setIsbn("not available");
        }
    }

    @Transactional(readOnly = true)
    public void queryEntityHistory() {
        AuditReader reader = AuditReaderFactory.get(em.unwrap(Session.class));
        
        AuditQuery queryAtRev = reader.createQuery().forEntitiesAtRevision(Book.class, 3);
        System.out.println("Get all Book instances modified at revision #3:");
        System.out.println(queryAtRev.getResultList());
        
        AuditQuery queryOfRevs = reader.createQuery().forRevisionsOfEntity(Book.class, true, true);
        System.out.println("\nGet all Book instances in all their states that were audited:");
        System.out.println(queryOfRevs.getResultList());
        
    }
}