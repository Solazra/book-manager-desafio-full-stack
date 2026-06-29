package com.bookmanager.domain.book;

import com.bookmanager.domain.book.dto.BookPageResponse;
import com.bookmanager.domain.book.dto.BookRequest;
import com.bookmanager.domain.book.dto.BookResponse;
import com.bookmanager.domain.shared.exception.BusinessException;
import com.bookmanager.domain.shared.mapper.BookMapper;
import com.bookmanager.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;

    @Transactional
    public BookResponse create(Long userId, BookRequest req) {
        var user = userRepository.getReferenceById(userId);
        var book = bookMapper.toEntity(req);
        book.setUser(user);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public BookPageResponse findAll(String title, Pageable pageable) {
        Page<Book> page = (title == null || title.isBlank())
            ? bookRepository.findAll(pageable)
            : bookRepository.findByTitleContainingIgnoreCase(title.trim(), pageable);
        return new BookPageResponse(
            bookMapper.toResponseList(page.getContent()),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast());
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return bookMapper.toResponse(getById(id));
    }

    @Transactional
    public BookResponse update(Long userId, Long id, BookRequest req) {
        var book = getOwned(userId, id);
        book.setTitle(req.title());
        book.setAuthor(req.author());
        book.setYear(req.year());
        book.setDescription(req.description());
        return bookMapper.toResponse(book);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        bookRepository.delete(getOwned(userId, id));
    }

    private Book getById(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Livro não encontrado", HttpStatus.NOT_FOUND));
    }

    private Book getOwned(Long userId, Long id) {
        return bookRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new BusinessException("Livro não encontrado", HttpStatus.NOT_FOUND));
    }
}
