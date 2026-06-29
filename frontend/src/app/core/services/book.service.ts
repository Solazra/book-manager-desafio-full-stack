import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Book, BookRequest, PaginatedResponse } from '../models/book.model';

@Injectable({ providedIn: 'root' })
export class BookService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  getBooks(page: number, size: number, sort: string, title?: string): Observable<PaginatedResponse<Book>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    if (title?.trim()) {
      params = params.set('title', title.trim());
    }
    return this.http.get<PaginatedResponse<Book>>(`${this.base}/books`, { params });
  }

  getBook(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.base}/books/${id}`);
  }

  createBook(book: BookRequest): Observable<Book> {
    return this.http.post<Book>(`${this.base}/books/create`, book);
  }

  updateBook(id: number, book: BookRequest): Observable<Book> {
    return this.http.put<Book>(`${this.base}/books/${id}`, book);
  }

  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/books/${id}`);
  }
}
