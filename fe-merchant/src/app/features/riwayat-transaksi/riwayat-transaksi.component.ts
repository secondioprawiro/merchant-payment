import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MerchantService, MerchantStats, Transaction } from '../../core/services/merchant.service';
import { AuthService } from '../../core/auth/auth.service';
import { TransactionDetailComponent } from '../modal/transaction-detail/transaction-detail.component';

@Component({
  selector: 'app-riwayat-transaksi',
  standalone: true,
  imports: [FormsModule, TransactionDetailComponent],
  templateUrl: './riwayat-transaksi.component.html',
  styleUrl: './riwayat-transaksi.component.css'
})
export class RiwayatTransaksiComponent implements OnInit {
  private merchantService = inject(MerchantService);
  private auth = inject(AuthService);
  private router = inject(Router);

  selectedTransaction: Transaction | null = null;
  showDetail = false;

  openDetail(tx: Transaction) { this.selectedTransaction = tx; this.showDetail = true; }
  closeDetail() { this.showDetail = false; this.selectedTransaction = null; }
  goToBeli() { this.showDetail = false; this.router.navigate(['/beli-produk']); }

  stats: MerchantStats | null = null;
  loadingStats = true;

  pagedTransactions: Transaction[] = [];
  loadingTx = true;
  page = 0;
  totalPages = 0;
  readonly pageSize = 10;

  startDate = '';
  endDate = '';

  get successRate(): number {
    if (!this.stats || this.stats.totalTransaksiKeseluruhan === 0) return 0;
    return Math.round((this.stats.totalBerhasilKeseluruhan / this.stats.totalTransaksiKeseluruhan) * 100);
  }

  prevPage() { if (this.page > 0) { this.page--; this.loadTransactions(); } }
  nextPage() { if (this.page < this.totalPages - 1) { this.page++; this.loadTransactions(); } }
  goToPage(p: number) { this.page = p; this.loadTransactions(); }

  applyFilter() { this.page = 0; this.loadTransactions(); }

  clearFilter() { this.startDate = ''; this.endDate = ''; this.page = 0; this.loadTransactions(); }

  get isFiltered(): boolean { return !!(this.startDate && this.endDate); }

  formatDateTime(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleString('id-ID', {
      day: '2-digit', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit', hour12: false,
    });
  }

  loadTransactions() {
    this.loadingTx = true;
    this.merchantService.getTransactions(this.page, this.pageSize, this.startDate || undefined, this.endDate || undefined).subscribe({
      next: (p) => { this.pagedTransactions = p.content; this.totalPages = p.totalPages; this.loadingTx = false; },
      error: () => { this.loadingTx = false; },
    });
  }

  ngOnInit() {
    const stats$ = this.auth.isAdmin()
      ? this.merchantService.getAdminStats()
      : this.merchantService.getMerchantStats();
    stats$.subscribe({
      next: (s) => { this.stats = s as MerchantStats; this.loadingStats = false; },
      error: () => { this.loadingStats = false; },
    });
    this.loadTransactions();
  }
}
