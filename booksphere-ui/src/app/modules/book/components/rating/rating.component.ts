import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-rating',
  templateUrl: './rating.component.html',
  styleUrls: ['./rating.component.scss'],
})
export class RatingComponent {
  //el input y output en este caso nos sirve para pasarle la calificacion y para lanzar un evento al hacer click en las estrellas
  @Input() rating: number = 0;
  @Output() ratingClicked: EventEmitter<number> = new EventEmitter<number>();
  maxRating: number = 5;

  get fullStars(): number {
    return Math.floor(this.rating);
  }

  get hasHalfStar(): boolean {
    return this.rating % 1 !== 0;
  }

  get emptyStars(): number {
    return this.maxRating - Math.ceil(this.rating);
  }
}
