import { Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MerchantService, MerchantProfile } from '../../../core/services/merchant.service';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css',
})
export class ProfilComponent implements OnInit {
  private merchantService = inject(MerchantService);
  private fb = inject(FormBuilder);

  profile: MerchantProfile | null = null;
  merchantId = '';
  loading = true;
  editMode = false;
  saving = false;
  saveError = '';

  form = this.fb.group({
    namaMerchant: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: [''],
  });

  ngOnInit() {
    this.loadProfile();
  }

  private loadProfile(silent = false) {
    if (!silent) this.loading = !this.profile;
    this.merchantService.getProfile().subscribe({
      next: (p) => {
        this.profile = p;
        this.merchantId = p.merchantId;
        this.form.patchValue({ namaMerchant: p.namaMerchant, email: p.email });
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  get initials(): string {
    return this.profile?.namaMerchant.charAt(0).toUpperCase() ?? '?';
  }

  toggleEdit() {
    this.editMode = !this.editMode;
    this.saveError = '';
    if (!this.editMode) {
      // cancelled — restore form to current profile values without refetch flash
      this.form.patchValue({
        namaMerchant: this.profile?.namaMerchant ?? '',
        email: this.profile?.email ?? '',
        password: '',
      });
    }
  }

  submit() {
    if (this.form.invalid || this.saving) return;
    this.saving = true;
    this.saveError = '';

    const val = this.form.value;
    const req: Record<string, string> = {};
    if (val.namaMerchant) req['namaMerchant'] = val.namaMerchant;
    if (val.email) req['email'] = val.email;
    if (val.password) req['password'] = val.password;

    this.merchantService.updateProfile(this.merchantId, req).subscribe({
      next: (updated) => {
        this.profile = updated;
        this.saving = false;
        this.editMode = false;
        this.form.patchValue({ password: '' });
      },
      error: (err) => {
        this.saving = false;
        this.saveError = err?.error?.message ?? 'Gagal menyimpan perubahan';
      },
    });
  }
}
