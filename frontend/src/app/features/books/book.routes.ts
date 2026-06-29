import { Routes } from '@angular/router';

export const BOOK_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./book-list/book-list.component').then((m) => m.BookListComponent),
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./book-form/book-form.component').then((m) => m.BookFormComponent),
  },
  {
    path: ':id/edit',
    loadComponent: () =>
      import('./book-form/book-form.component').then((m) => m.BookFormComponent),
  },
];
