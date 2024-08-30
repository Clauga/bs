import { Component, OnInit } from '@angular/core';
import { BookService } from '../../../../services/services/book.service';
import { PageResponseBookResponse } from '../../../../services/models/page-response-book-response';
import { BookResponse } from '../../../../services/models/book-response';
import { Router } from '@angular/router';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss'],
})
export class BookListComponent implements OnInit {
  bookResponse: PageResponseBookResponse = {};
  //bookResponse corresponde a la respuesta que se obtiene de la peticion de los libros y lo unicializo como un valor vacio
  page = 0;
  size = 10;
  pages: any = [];
  message = '';
  level: 'success' | 'error' = 'success';

  constructor(private bookService: BookService, private router: Router) {}
  //para cargar todos los libros cuando abro la pagina
  ngOnInit(): void {
    this.findAllBooks();
  }

  private findAllBooks() {
    //este metodo se encarga de traer todos los libros
    this.bookService
      .findAllBooks({
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: (books) => {
          this.bookResponse = books;
          this.pages = Array(this.bookResponse.totalPages)
            .fill(0)
            .map((x, i) => i);
        },
      });
  }

  gotToPage(page: number) {
    this.page = page;
    this.findAllBooks();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBooks();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBooks();
  }

  goToLastPage() {
    this.page = (this.bookResponse.totalPages as number) - 1;
    this.findAllBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllBooks();
  }

  get isLastPage() {
    return this.page === (this.bookResponse.totalPages as number) - 1;
  }
  //metodo para agregar un libro a la lista de libros prestados, funciona con el id del libro, y cuando se agrega se muestra un mensaje de exito
  borrowBook(book: BookResponse) {
    this.message = '';
    this.level = 'success';
    this.bookService
      .borrowBook({
        'book-id': book.id as number,
      })
      .subscribe({
        next: () => {
          this.level = 'success';
          this.message = 'Book successfully added to your list';
        },
        error: (err) => {
          console.log(err);
          this.level = 'error';
          this.message = err.error.error;
        },
      });
  }

  displayBookDetails(book: BookResponse) {
    this.router.navigate(['books', 'details', book.id]);
  }
}
