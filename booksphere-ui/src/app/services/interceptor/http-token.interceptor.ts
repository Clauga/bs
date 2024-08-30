import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpHeaders,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenService } from '../token/token.service';

@Injectable()
export class HttpTokenInterceptor implements HttpInterceptor {
  constructor(private tokenService: TokenService) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const token = this.tokenService.token;
    if (token) {
      //esta intercepcion nos sirve para agregar el token a las peticiones que se hagan, o sea que vamos a ir a buscar el token y lo vamos a agregar a la peticion
      const authReq = request.clone({
        //usamos clone para clonar la peticion y agregarle el token
        headers: new HttpHeaders({
          Authorization: 'Bearer ' + token,
          //queremos como parametro de la cabecera el token y bearer es el tipo de token que estamos enviando
        }),
      });
      return next.handle(authReq);
    }
    return next.handle(request);
  }
}
