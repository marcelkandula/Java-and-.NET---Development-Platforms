package org.example;

import java.util.List;
import java.util.Scanner;

import jakarta.persistence.*;
import org.example.entity.Author;
import org.example.entity.Book;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("name_PU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        while (true) {
            System.out.println("\n1. Dodaj autora");
            System.out.println("2. Usuń autora");
            System.out.println("3. Dodaj książkę");
            System.out.println("4. Usuń książkę");
            System.out.println("5. Wyświetl wpisy");
            System.out.println("6. Zapytania");
            System.out.println("7. Wyjście");
            System.out.print("Wybierz opcję: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Podaj autora");
                    String name = scanner.nextLine();
                    Author author = new Author();
                    author.setName(name);
                    tx.begin();
                    em.persist(author);
                    tx.commit();
                    System.out.println("Dodano autora.");
                    break;
                case 2:
                    List<Author> authorsDel = em.createQuery("SELECT a FROM Author a", Author.class).getResultList();
                    System.out.println("Wybierz autora do usunięcia:");
                    for (int i = 0; i < authorsDel.size(); i++) {
                        System.out.println((i + 1) + ". " + authorsDel.get(i).getName());
                    }
                    int authorChoice = scanner.nextInt();
                    scanner.nextLine();
                    Author selectedAuthor = authorsDel.get(authorChoice - 1);
                    tx.begin();
                    em.remove(selectedAuthor);
                    tx.commit();
                    System.out.println("Autor został usunięty.");
                    break;
                case 3:
                    List<Author> authorsAddBook = em.createQuery("SELECT a FROM Author a", Author.class).getResultList();
                    System.out.println("Wybierz autora:");
                    for (int i = 0; i < authorsAddBook.size(); i++) {
                        System.out.println((i + 1) + ". " + authorsAddBook.get(i).getName());
                    }
                    int authChoice = scanner.nextInt();
                    scanner.nextLine();
                    Author selAuth = authorsAddBook.get(authChoice - 1);
                    System.out.print("Tytuł książki: ");
                    String title = scanner.nextLine();
                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(selAuth);
                    tx.begin();
                    em.persist(book);
                    tx.commit();
                    System.out.println("Książka dodana.");
                    break;
                case 4:
                    System.out.print("Podaj tytuł książki do usunięcia: ");
                    String titleToDelete = scanner.nextLine();
                    tx.begin();
                    List<Book> booksToRemove = em.createQuery(
                                    "SELECT b FROM Book b WHERE b.Title = :title", Book.class)
                            .setParameter("title", titleToDelete)
                            .getResultList();
                    if (!booksToRemove.isEmpty()) {
                        for (Book b : booksToRemove) {
                            em.remove(b);
                        }
                        System.out.println("Usunięto książkę.");
                    } else {
                        System.out.println("Nie znaleziono książki o podanym tytule.");
                    }
                    tx.commit();
                    break;

                case 5:
                    System.out.println("1. Autorzy  2. Książki");
                    int entity = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Wyświetlić wszystkie (0) czy pierwsze n (>0)? Podaj liczbę: ");
                    int n = scanner.nextInt();
                    scanner.nextLine();
                    if (entity == 1) {
                        TypedQuery<Author> q = em.createQuery("SELECT a FROM Author a", Author.class);
                        if (n > 0) q.setMaxResults(n);
                        List<Author> listA = q.getResultList();
                        System.out.println("=== Autorzy ===");
                        listA.forEach(a -> System.out.println(a.getId() + ": " + a.getName()));
                    } else {
                        TypedQuery<Book> q = em.createQuery("SELECT b FROM Book b", Book.class);
                        if (n > 0) q.setMaxResults(n);
                        List<Book> listB = q.getResultList();
                        System.out.println("=== Książki ===");
                        listB.forEach(b -> System.out
                                .println(b.id + ": \"" + b.getTitle() + "\" autor: " + b.getAuthor().getName()));
                    }
                    break;

                case 6:
                    System.out.println("\nZapytania:");
                    System.out.println("1. Autorzy mający co najmniej jedną książkę");
                    System.out.println("2. Pierwsze 5 książek posortowane alfabetycznie");
                    System.out.println("3. Liczba książek per autor");
                    System.out.println("4. Autorzy bez książek");
                    System.out.println("5. Książki, których tytuł ma więcej niż 10 znaków");
                    System.out.print("Wybierz zapytanie: ");
                    int qChoice = scanner.nextInt();
                    scanner.nextLine();
                    switch (qChoice) {
                        case 1:
                            List<Author> q1 = em.createQuery(
                                            "SELECT DISTINCT a FROM Author a JOIN a.Books b", Author.class)
                                    .getResultList();
                            System.out.println("Autorzy z co najmniej jedną książką:");
                            q1.forEach(a -> System.out.println(a.getName()));
                            break;
                        case 2:
                            List<Book> q2 = em.createQuery(
                                            "SELECT b FROM Book b ORDER BY b.Title ASC", Book.class)
                                    .setMaxResults(5)
                                    .getResultList();
                            System.out.println("Pierwsze 5 książek alfabetycznie:");
                            q2.forEach(b -> System.out.println(b.getTitle()));
                            break;
                        case 3:
                            List<Object[]> q3 = em.createQuery(
                                            "SELECT a.Name, COUNT(b) FROM Author a LEFT JOIN a.Books b GROUP BY a.Name",
                                            Object[].class)
                                    .getResultList();
                            System.out.println("Liczba książek per autor:");
                            q3.forEach(row -> System.out
                                    .println(row[0] + ": " + row[1]));
                            break;
                        case 4:
                            List<Author> q4 = em.createQuery(
                                            "SELECT a FROM Author a WHERE SIZE(a.Books) = 0", Author.class)
                                    .getResultList();
                            System.out.println("Autorzy bez książek:");
                            q4.forEach(a -> System.out.println(a.getName()));
                            break;
                        case 5:
                            List<Book> q5 = em.createQuery(
                                            "SELECT b FROM Book b WHERE LENGTH(b.Title) > 10", Book.class)
                                    .getResultList();
                            System.out.println("Książki z tytułem >10 znaków:");
                            q5.forEach(b -> System.out.println(b.getTitle()));
                            break;
                        default:
                            System.out.println("Nieprawidłowy wybór.");
                    }
                    break;

                case 7:
                    em.close();
                    emf.close();
                    System.out.println("Koniec.");
                    return;

                default:
                    System.out.println("Nieprawidłowa opcja.");
            }
        }
    }
}
