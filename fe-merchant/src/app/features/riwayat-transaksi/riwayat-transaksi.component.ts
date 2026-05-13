import { Component, inject, OnInit } from '@angular/core';
import { MerchantService, MerchantStats, Transaction } from '../../core/services/merchant.service';

@Component({
  selector: 'app-riwayat-transaksi',
  standalone: true,
  imports: [],
  templateUrl: './riwayat-transaksi.component.html',
  styleUrl: './riwayat-transaksi.component.css'
})
export class RiwayatTransaksiComponent implements OnInit {
  private merchantService = inject(MerchantService);

  stats: MerchantStats | null = null;
  loadingStats = true;

  pagedTransactions: Transaction[] = [];
  loadingTx = true;
  page = 0;
  totalPages = 0;
  readonly pageSize = 10;

  get successRate(): number {
    if (!this.stats || this.stats.totalTransaksiKeseluruhan === 0) return 0;
    return Math.round((this.stats.totalBerhasilKeseluruhan / this.stats.totalTransaksiKeseluruhan) * 100);
  }

  prevPage() { if (this.page > 0) { this.page--; this.loadTransactions(); } }
  nextPage() { if (this.page < this.totalPages - 1) { this.page++; this.loadTransactions(); } }
  goToPage(p: number) { this.page = p; this.loadTransactions(); }

  formatDateTime(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleString('id-ID', {
      day: '2-digit', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit', hour12: false,
    });
  }

  loadTransactions() {
    this.loadingTx = true;
    this.merchantService.getTransactions(this.page, this.pageSize).subscribe({
      next: (p) => { this.pagedTransactions = p.content; this.totalPages = p.totalPages; this.loadingTx = false; },
      error: () => { this.loadingTx = false; },
    });
  }

  ngOnInit() {
    this.merchantService.getMerchantStats().subscribe({
      next: (s) => { this.stats = s; this.loadingStats = false; },
      error: () => { this.loadingStats = false; },
    });
    this.loadTransactions();
  }
}
