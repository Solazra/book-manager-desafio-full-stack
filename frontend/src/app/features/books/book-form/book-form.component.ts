import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BookRequest } from '../../../core/models/book.model';
import { BookService } from '../../../core/services/book.service';

@Component({
  selector: 'app-book-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './book-form.component.html',
  styleUrl: './book-form.component.scss',
})
export class BookFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private bookService = inject(BookService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  bookId = signal<number | null>(null);
  isEdit = computed(() => this.bookId() !== null);
  loading = signal(false);
  errorMsg = signal<string | null>(null);

  form = this.fb.group({
    title: ['', [Validators.required]],
    author: ['', [Validators.required]],
    year: [null as number | null, [Validators.min(1000), Validators.max(2100)]],
    description: [''],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.bookId.set(Number(idParam));
      this.bookService.getBook(this.bookId()!).subscribe((b) => this.form.patchValue(b));
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.errorMsg.set(null);
    const raw = this.form.getRawValue();
    const payload: BookRequest = {
      title: raw.title!,
      author: raw.author!,
      year: raw.year ?? undefined,
      description: raw.description || undefined,
    };
    const req$ = this.isEdit()
      ? this.bookService.updateBook(this.bookId()!, payload)
      : this.bookService.createBook(payload);
    req$.subscribe({
      next: () => this.router.navigate(['/books']),
      error: (err) => {
        this.errorMsg.set(err.error?.message ?? 'Erro ao salvar');
        this.loading.set(false);
      },
    });
  }
}
