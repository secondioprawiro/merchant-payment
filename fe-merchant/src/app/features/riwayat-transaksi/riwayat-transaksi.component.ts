import { Component, inject, OnInit } from '@angular/core';
import { MerchantService, Transaction } from '../../core/services/merchant.service';

@Component({
  selector: 'app-riwayat-transaksi',
  standalone: true,
  imports: [],
  templateUrl: './riwayat-transaksi.component.html',
  styleUrl: './riwayat-transaksi.component.css'
})
export class RiwayatTransaksiComponent implements OnInit {
  private merchantService = inject(MerchantService);

  transactions: Transaction[] = [];
  loading = true;
  page = 0;
  readonly pageSize = 10;

  get totalKeseluruhan(): number { return this.transactions.length; }
  get totalBerhasil(): number { return this.transactions.filter(t => t.status === 'SUCCESS').length; }
  get totalGagal(): number { return this.transactions.filter(t => t.status === 'FAILED').length; }

  get successRate(): number {
    if (this.totalKeseluruhan === 0) return 0;
    return Math.round((this.totalBerhasil / this.totalKeseluruhan) * 100);
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

  formatDateTime(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleString('id-ID', {
      day: '2-digit', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit', hour12: false,
    });
  }

  ngOnInit() {
    this.merchantService.getTransactions().subscribe({
      next: (t) => { this.transactions = t; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }
}
