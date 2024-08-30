import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services/authentication.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnInit {
  signupForm: FormGroup;
  errorMsg: Array<string> = [];
  password: string = 'password';
  show: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthenticationService
  ) {
    this.signupForm = this.fb.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(12),
          Validators.pattern(
            '(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%^&*(),.?":|]).*'
          ),
        ],
      ],
    });
  }

  ngOnInit(): void {}

  login() {
    this.router.navigate(['login']);
  }

  register() {
    if (this.signupForm.invalid) {
      this.errorMsg = ['Please correct the errors in the form.'];
      return;
    }

    const registerRequest = {
      email: this.signupForm.get('email')?.value,
      firstname: this.signupForm.get('firstname')?.value,
      lastname: this.signupForm.get('lastname')?.value,
      password: this.signupForm.get('password')?.value,
    };

    this.errorMsg = [];

    this.authService.register({ body: registerRequest }).subscribe({
      next: () => {
        this.router.navigate(['activate-account']);
      },
      error: (err) => {
        this.errorMsg = err.error.validationErrors || [
          'An unexpected error occurred.',
        ];
      },
    });
  }
  onClick() {
    if (this.password === 'password') {
      this.password = 'text';
      this.show = true;
    } else {
      this.password = 'password';
      this.show = false;
    }
  }
}
