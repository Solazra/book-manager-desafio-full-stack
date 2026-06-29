package com.bookmanager.domain.book;

import com.bookmanager.domain.book.dto.BookRequest;
import com.bookmanager.domain.book.dto.BookResponse;
import com.bookmanager.domain.shared.exception.BusinessException;
import com.bookmanager.domain.shared.mapper.BookMapper;
import com.bookmanager.domain.user.User;
import com.bookmanager.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private final BookRequest bookRequest = new BookRequest("Clean Code", "Robert Martin", 2008, "desc");

    @Test
    void create_associaUsuarioCorretoESalva() {
        var user = User.builder().id(1L).build();
        var book = Book.builder().title("Clean Code").author("Robert Martin").build();
        var saved = Book.builder().id(10L).title("Clean Code").author("Robert Martin").user(user).build();
        var response = new BookResponse(10L, "Clean Code", "Robert Martin", 2008, "desc", null, null);

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(bookMapper.toEntity(bookRequest)).thenReturn(book);
        when(bookRepository.save(book)).thenAnswer(invocation -> {
            Book toSave = invocation.getArgument(0);
            assertSame(user, toSave.getUser());
            return saved;
        });
        when(bookMapper.toResponse(saved)).thenReturn(response);

        var result = bookService.create(1L, bookRequest);

        assertEquals(response, result);
        verify(bookRepository).save(book);
    }

    @Test
    void findAll_semTitulo_usaFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of());
        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(bookMapper.toResponseList(List.of())).thenReturn(List.of());

        bookService.findAll(null, pageable);

        verify(bookRepository).findAll(pageable);
        verify(bookRepository, never()).findByTitleContainingIgnoreCase(any(), any());
    }

    @Test
    void findAll_comTitulo_usaFindByTitleContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of());
        when(bookRepository.findByTitleContainingIgnoreCase("clean", pageable)).thenReturn(page);
        when(bookMapper.toResponseList(List.of())).thenReturn(List.of());

        bookService.findAll("clean", pageable);

        verify(bookRepository).findByTitleContainingIgnoreCase("clean", pageable);
        verify(bookRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void findById_quandoLivroNaoExiste_lanca404() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        var ex = assertThrows(BusinessException.class, () -> bookService.findById(99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void update_quandoLivroDeOutroUsuario_lanca404() {
        when(bookRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        var ex = assertThrows(BusinessException.class,
            () -> bookService.update(1L, 99L, bookRequest));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void update_alteraCamposERetornaResponse() {
        var book = Book.builder().id(5L).title("Old").author("Old Author").year(1999).description("old").build();
        var updatedRequest = new BookRequest("New Title", "New Author", 2020, "new desc");
        var response = new BookResponse(5L, "New Title", "New Author", 2020, "new desc", null, null);

        when(bookRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(response);

        var result = bookService.update(1L, 5L, updatedRequest);

        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
        assertEquals(2020, book.getYear());
        assertEquals("new desc", book.getDescription());
        assertEquals(response, result);
    }

    @Test
    void delete_quandoLivroDeOutroUsuario_lanca404() {
        when(bookRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        var ex = assertThrows(BusinessException.class, () -> bookService.delete(1L, 99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(bookRepository, never()).delete(any());
    }

    @Test
    void delete_chamaRepositoryDelete() {
        var book = Book.builder().id(5L).build();
        when(bookRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(book));

        bookService.delete(1L, 5L);

        verify(bookRepository).delete(book);
    }
}
