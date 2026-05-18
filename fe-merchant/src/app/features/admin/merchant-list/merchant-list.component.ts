import { Component, inject, OnInit, OnDestroy, HostListener } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MerchantService, MerchantListItem, UpdateProfileRequest, AdminStats } from '../../../core/services/merchant.service';

@Component({
  selector: 'app-merchant-list',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './merchant-list.component.html',
  styleUrl: './merchant-list.component.css'
})
export class MerchantListComponent implements OnInit, OnDestroy {
  private merchantService = inject(MerchantService);

  stats: AdminStats | null = null;
  loadingStats = true;

  merchants: MerchantListItem[] = [];
  loading = true;
  error = '';

  activeTab: 'semua' | 'aktif' | 'nonaktif' | 'dihapus' = 'semua';

  get filteredMerchants(): MerchantListItem[] {
    switch (this.activeTab) {
      case 'aktif':    return this.merchants.filter(m => !m.isDeleted && m.status === 'ACTIVE');
      case 'nonaktif': return this.merchants.filter(m => !m.isDeleted && m.status !== 'ACTIVE');
      case 'dihapus':  return this.merchants.filter(m => m.isDeleted);
      default:         return this.merchants;
    }
  }

  get aktifRate(): number {
    if (!this.stats || this.stats.totalMerchant === 0) return 0;
    return Math.round((this.stats.totalMerchantAktif / this.stats.totalMerchant) * 100);
  }

  // Edit modal
  editTarget: MerchantListItem | null = null;
  editForm: UpdateProfileRequest = {};
  editLoading = false;
  editError = '';

  // Delete confirm
  deleteTarget: MerchantListItem | null = null;
  deleteLoading = false;

  get isModalOpen(): boolean {
    return !!this.editTarget || !!this.deleteTarget;
  }

  ngOnInit() {
    this.merchantService.getAdminStats().subscribe({
      next: (s) => { this.stats = s; this.loadingStats = false; },
      error: () => { this.loadingStats = false; },
    });
    this.loadMerchants();
  }

  ngOnDestroy() {
    document.body.style.overflow = '';
  }

  loadMerchants() {
    this.loading = true;
    this.error = '';
    this.merchantService.getAllMerchants().subscribe({
      next: (list) => { this.merchants = list; this.loading = false; },
      error: () => { this.error = 'Gagal memuat daftar merchant.'; this.loading = false; },
    });
  }

  openEdit(merchant: MerchantListItem) {
    this.editTarget = merchant;
    this.editForm = { namaMerchant: merchant.namaMerchant };
    this.editError = '';
    this.lockScroll();
  }

  closeEdit() {
    this.editTarget = null;
    this.editForm = {};
    this.editError = '';
    this.unlockScroll();
  }

  submitEdit() {
    if (!this.editTarget) return;
    const payload: UpdateProfileRequest = {};
    if (this.editForm.namaMerchant?.trim()) payload.namaMerchant = this.editForm.namaMerchant.trim();
    if (this.editForm.email?.trim()) payload.email = this.editForm.email.trim();
    if (this.editForm.password?.trim()) payload.password = this.editForm.password.trim();

    this.editLoading = true;
    this.editError = '';
    this.merchantService.updateProfile(this.editTarget.merchantId, payload).subscribe({
      next: () => {
        if (this.editTarget && payload.namaMerchant) {
          this.editTarget.namaMerchant = payload.namaMerchant;
        }
        this.editLoading = false;
        this.closeEdit();
      },
      error: () => { this.editError = 'Gagal menyimpan perubahan.'; this.editLoading = false; },
    });
  }

  openDelete(merchant: MerchantListItem) {
    this.deleteTarget = merchant;
    this.lockScroll();
  }

  closeDelete() {
    this.deleteTarget = null;
    this.unlockScroll();
  }

  confirmDelete() {
    if (!this.deleteTarget) return;
    this.deleteLoading = true;
    this.merchantService.deleteMerchant(this.deleteTarget.merchantId).subscribe({
      next: () => {
        this.merchants = this.merchants.filter(m => m.merchantId !== this.deleteTarget!.merchantId);
        this.deleteLoading = false;
        this.closeDelete();
      },
      error: () => { this.deleteLoading = false; },
    });
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    if (this.editTarget) this.closeEdit();
    else if (this.deleteTarget) this.closeDelete();
  }

  private lockScroll() { document.body.style.overflow = 'hidden'; }
  private unlockScroll() { document.body.style.overflow = ''; }
}
