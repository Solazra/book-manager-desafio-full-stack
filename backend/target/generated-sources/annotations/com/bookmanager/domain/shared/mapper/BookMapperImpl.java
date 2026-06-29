package com.bookmanager.domain.shared.mapper;

import com.bookmanager.domain.book.Book;
import com.bookmanager.domain.book.dto.BookRequest;
import com.bookmanager.domain.book.dto.BookResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-26T00:19:12+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public Book toEntity(BookRequest request) {
        if ( request == null ) {
            return null;
        }

        Book.BookBuilder book = Book.builder();

        book.title( request.title() );
        book.author( request.author() );
        book.year( request.year() );
        book.description( request.description() );

        return book.build();
    }

    @Override
    public BookResponse toResponse(Book book) {
        if ( book == null ) {
            return null;
        }

        Long id = null;
        String title = null;
        String author = null;
        Integer year = null;
        String description = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        id = book.getId();
        title = book.getTitle();
        author = book.getAuthor();
        year = book.getYear();
        description = book.getDescription();
        createdAt = book.getCreatedAt();
        updatedAt = book.getUpdatedAt();

        BookResponse bookResponse = new BookResponse( id, title, author, year, description, createdAt, updatedAt );

        return bookResponse;
    }

    @Override
    public List<BookResponse> toResponseList(List<Book> books) {
        if ( books == null ) {
            return null;
        }

        List<BookResponse> list = new ArrayList<BookResponse>( books.size() );
        for ( Book book : books ) {
            list.add( toResponse( book ) );
        }

        return list;
    }
}
