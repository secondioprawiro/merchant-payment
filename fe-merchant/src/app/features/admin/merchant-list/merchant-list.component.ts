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

  page = 0;
  pageSize = 5;
  totalPages = 0;
  totalElements = 0;

  activeTab: 'semua' | 'aktif' | 'nonaktif' | 'dihapus' = 'semua';

  get filteredMerchants(): MerchantListItem[] {
    return this.merchants;
  }

  get aktifRate(): number {
    if (!this.stats || this.stats.totalMerchant === 0) return 0;
    return Math.round((this.stats.totalMerchantAktif / this.stats.totalMerchant) * 100);
  }

  deletedCount = 0;

  get totalDihapus(): number {
    return this.deletedCount;
  }

  private getFilterParams(): { status?: string; isDeleted?: boolean } {
    switch (this.activeTab) {
      case 'aktif':    return { status: 'ACTIVE', isDeleted: false };
      case 'nonaktif': return { status: 'INACTIVE', isDeleted: false };
      case 'dihapus':  return { isDeleted: true };
      default:         return {};
    }
  }

  setTab(tab: 'semua' | 'aktif' | 'nonaktif' | 'dihapus') {
    this.activeTab = tab;
    this.page = 0;
    this.loadMerchants();
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
    this.merchantService.getAllMerchants(0, 1, undefined, true).subscribe({
      next: (res) => { this.deletedCount = res.totalElements; },
    });
    this.loadMerchants();
  }

  ngOnDestroy() {
    document.body.style.overflow = '';
  }

  refreshStats() {
    this.merchantService.getAdminStats().subscribe({
      next: (s) => { this.stats = s; },
    });
  }

  loadMerchants() {
    this.loading = true;
    this.error = '';
    const { status, isDeleted } = this.getFilterParams();
    this.merchantService.getAllMerchants(this.page, this.pageSize, status, isDeleted).subscribe({
      next: (res) => {
        this.merchants = res.content;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
        if (this.activeTab === 'dihapus') this.deletedCount = res.totalElements;
        this.loading = false;
      },
      error: () => { this.error = 'Gagal memuat daftar merchant.'; this.loading = false; },
    });
  }

  goToPage(p: number) {
    if (p < 0 || p >= this.totalPages) return;
    this.page = p;
    this.loadMerchants();
  }

  openEdit(merchant: MerchantListItem) {
    this.editTarget = merchant;
    this.editForm = { namaMerchant: merchant.namaMerchant, status: merchant.status as 'ACTIVE' | 'INACTIVE' };
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
    if (this.editForm.status) payload.status = this.editForm.status;

    this.editLoading = true;
    this.editError = '';
    this.merchantService.updateProfile(this.editTarget.merchantId, payload).subscribe({
      next: () => {
        if (this.editTarget) {
          if (payload.namaMerchant) this.editTarget.namaMerchant = payload.namaMerchant;
          if (payload.status) this.editTarget.status = payload.status;
        }
        this.editLoading = false;
        this.closeEdit();
        this.refreshStats();
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
        this.deletedCount++;
        this.deleteLoading = false;
        this.closeDelete();
        this.loadMerchants();
        this.refreshStats();
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
