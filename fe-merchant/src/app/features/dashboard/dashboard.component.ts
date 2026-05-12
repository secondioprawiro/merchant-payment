import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MerchantService, MerchantStats, Transaction } from '../../core/services/merchant.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private merchantService = inject(MerchantService);

  stats: MerchantStats | null = null;
  loadingStats = true;

  transactions: Transaction[] = [];
  loadingTx = true;
  page = 0;
  readonly pageSize = 5;

  get successRate(): number {
    if (!this.stats || this.stats.totalTransaksiHariIni === 0) return 0;
    return Math.round((this.stats.totalBerhasilHariIni / this.stats.totalTransaksiHariIni) * 100);
  }

  get pagedTransactions(): Transaction[] {
    const start = this.page * this.pageSize;
    return this.transactions.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.transactions.length / this.pageSize);
  }

  prevPage() { if (this.page > 0) this.page--; }
  nextPage() { if (this.page < this.totalPages - 1) this.page++; }

  formatTime(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  ngOnInit() {
    this.merchantService.getMerchantStats().subscribe({
      next: (s) => { this.stats = s; this.loadingStats = false; },
      error: () => { this.loadingStats = false; },
    });

    this.merchantService.getTransactions().subscribe({
      next: (t) => { this.transactions = t; this.loadingTx = false; },
      error: () => { this.loadingTx = false; },
    });
  }
}
