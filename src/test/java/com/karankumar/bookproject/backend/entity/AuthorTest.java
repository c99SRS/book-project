/*
    The book project lets a user keep track of different books they would like to read, are currently
    reading, have read or did not finish.
    Copyright (C) 2020  Karan Kumar

    This program is free software: you can redistribute it and/or modify it under the terms of the
    GNU General Public License as published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
    PURPOSE.  See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program.
    If not, see <https://www.gnu.org/licenses/>.
 */

package com.karankumar.bookproject.backend.entity;

import com.karankumar.bookproject.annotations.IntegrationTest;
import com.karankumar.bookproject.backend.service.AuthorService;
import com.karankumar.bookproject.backend.service.BookService;
import com.karankumar.bookproject.backend.service.PredefinedShelfService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

@IntegrationTest
class AuthorTest {
    private final BookService bookService;

    private static Book testBook1;
    private static Book testBook2;
    private final AuthorService authorService;
    private static PredefinedShelf toRead;

    @Autowired
    public AuthorTest(PredefinedShelfService predefinedShelfService,
                      BookService bookService,
                      AuthorService authorService) {
        toRead = predefinedShelfService.findToReadShelf();
        testBook1 = createBook("How the mind works", toRead);
        testBook2 = createBook("The better angels of our nature", toRead);

        this.bookService = bookService;
        this.authorService = authorService;
    }

    @BeforeEach
    public void setUp() {
        resetBookService();
        saveBooks();
    }

    private void saveBooks() {
        bookService.save(testBook1);
        bookService.save(testBook2);
    }

    private void resetBookService() {
        bookService.deleteAll();
    }

    private static Book createBook(String title, PredefinedShelf shelf) {
        Author author = new Author("Steven", "Pinker");
        return new Book(title, author, shelf);
    }

    /**
     * Updating the author of a book should only affect that book, not any other book that
     * originally had the same author name
     */
    @Test
    @Disabled
    // TODO: fix failing test
    void updateAuthorAffectsOneRow() {
        Author newAuthor = new Author("Matthew", "Walker");
        testBook1.setAuthor(newAuthor);
        bookService.save(testBook1);

        Assertions.assertNotEquals(testBook1.getAuthor(), testBook2.getAuthor());
    }

    @Test
    @Disabled
        // TODO: fix failing test
    void orphanAuthorsRemoved() {
        Author orphan = new Author("Jostein", "Gardner");
        Book book = new Book("Sophie's World", orphan, toRead);
        bookService.delete(book);

        assertAll(
                () -> assertThrows(RuntimeException.class,
                        () -> authorService.findById(orphan.getId())),
                () -> assertTrue(authorService.findAll().isEmpty())
        );
    }
}
