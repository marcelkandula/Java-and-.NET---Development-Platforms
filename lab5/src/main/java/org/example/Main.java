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
            System.out.println("5. Wyjście");
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice == 1){
                System.out.println("Podaj autora");
                String name = scanner.nextLine();
                Author author = new Author();
                author.setName(name);
                tx.begin();
                em.persist(author);
                tx.commit();
                System.out.println("Dodano autora.");
            } else if(choice == 2){
                List<Author> authors = em.createQuery("SELECT a FROM Author a", Author.class).getResultList();
                System.out.println("Wybierz autora do usunięcia:");
                for (int i = 0; i < authors.size(); i++) {
                    System.out.println((i + 1) + ". " + authors.get(i).getName());
                }
                int authorChoice = scanner.nextInt();
                scanner.nextLine();
                Author selectedAuthor = authors.get(authorChoice - 1);
                tx.begin();
                em.remove(selectedAuthor);
                tx.commit();
                System.out.println("Autor został usunięty.");
            } else if (choice == 3) {
                List<Author> authors = em.createQuery("SELECT a FROM Author a", Author.class).getResultList();
                System.out.println("Wybierz autora:");
                for (int i = 0; i < authors.size(); i++) {
                    System.out.println((i + 1) + ". " + authors.get(i).getName());
                }
                int authorChoice = scanner.nextInt();
                scanner.nextLine();
                Author selectedAuthor = authors.get(authorChoice - 1);
                System.out.print("Tytuł książki: ");
                String title = scanner.nextLine();
                Book book = new Book();
                book.setTitle(title);
                book.setAuthor(selectedAuthor);
                tx.begin();
                em.persist(book);
                tx.commit();
                System.out.println("Książka dodana.");
            } else if (choice == 4) {
                System.out.print("Podaj tytuł książki do usunięcia: ");
                String titleToDelete = scanner.nextLine();
                tx.begin();
                List<Book> books = em.createQuery("SELECT b FROM Book b WHERE b.Title = :title", Book.class).setParameter("title", titleToDelete).getResultList();
                if (!books.isEmpty()) {
                    for (Book b : books) {
                        em.remove(b);
                    }
                    System.out.println("Usunięto książkę.");
                } else {
                    System.out.println("Nie znaleziono książki o podanym tytule.");
                }
                tx.commit();
            } else {
                em.close();
                emf.close();
                break;
            }
        }
    }
}