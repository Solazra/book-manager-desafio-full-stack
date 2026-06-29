import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';

import { Book } from '../../../core/models/book.model';
import { BookService } from '../../../core/services/book.service';

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss',
})
export class BookListComponent implements OnInit {
  private bookService = inject(BookService);
  private destroyRef = inject(DestroyRef);

  books = signal<Book[]>([]);
  page = signal(0);
  size = signal(9);
  totalPages = signal(0);
  totalElements = signal(0);
  last = signal(true);
  loading = signal(false);

  searchControl = new FormControl('', { nonNullable: true });
  skeletonItems = [1, 2, 3, 4, 5, 6, 7, 8, 9];
  private sort = 'createdAt,desc';

  /** Generates a stable hue for the card cover based on the book title. */
  coverHue(book: Book): number {
    let hash = 0;
    for (const char of book.title) {
      hash = char.charCodeAt(0) + ((hash << 5) - hash);
    }
    return Math.abs(hash) % 360;
  }

  ngOnInit(): void {
    this.load();
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.page.set(0);
        this.load();
      });
  }

  load(): void {
    this.loading.set(true);
    this.bookService
      .getBooks(this.page(), this.size(), this.sort, this.searchControl.value)
      .subscribe({
        next: (res) => {
          this.books.set(res.content);
          this.totalPages.set(res.totalPages);
          this.totalElements.set(res.totalElements);
          this.last.set(res.last);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  nextPage(): void {
    if (!this.last()) {
      this.page.update((p) => p + 1);
      this.load();
    }
  }

  prevPage(): void {
    if (this.page() > 0) {
      this.page.update((p) => p - 1);
      this.load();
    }
  }

  remove(book: Book): void {
    if (!confirm(`Excluir "${book.title}"?`)) {
      return;
    }
    this.bookService.deleteBook(book.id).subscribe(() => this.load());
  }
}
