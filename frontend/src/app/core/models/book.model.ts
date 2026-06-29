export interface Book {
  id: number;
  title: string;
  author: string;
  year?: number;
  description?: string;
}

export interface BookRequest {
  title: string;
  author: string;
  year?: number;
  description?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
