import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MerchantService, MerchantStats, Transaction } from '../../core/services/merchant.service';
import { AuthService } from '../../core/auth/auth.service';
import { TransactionDetailComponent } from '../modal/transaction-detail/transaction-detail.component';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, TransactionDetailComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private merchantService = inject(MerchantService);
  private auth = inject(AuthService);
  private router = inject(Router);

  selectedTransaction: any = null;
  showDetail = false;

  stats: MerchantStats | null = null;
  loadingStats = true;

  pagedTransactions: Transaction[] = [];
  loadingTx = true;
  page = 0;
  totalPages = 0;
  readonly pageSize = 5;

  get successRate(): number {
    if (!this.stats || this.stats.totalTransaksiHariIni === 0) return 0;
    return Math.round((this.stats.totalBerhasilHariIni / this.stats.totalTransaksiHariIni) * 100);
  }

  prevPage() { if (this.page > 0) { this.page--; this.loadTransactions(); } }
  nextPage() { if (this.page < this.totalPages - 1) { this.page++; this.loadTransactions(); } }
  goToPage(p: number) { this.page = p; this.loadTransactions(); }

  formatTime(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  private get today(): string {
    return new Date().toISOString().split('T')[0];
  }

  loadTransactions() {
    this.loadingTx = true;
    this.merchantService.getTransactions(this.page, this.pageSize, this.today, this.today).subscribe({
      next: (p) => { this.pagedTransactions = p.content; this.totalPages = p.totalPages; this.loadingTx = false; },
      error: () => { this.loadingTx = false; },
    });
  }

  ngOnInit() {
    if (this.auth.isAdmin()) {
      this.router.navigate(['/admin']);
      return;
    }

    this.merchantService.getMerchantStats().subscribe({
      next: (s) => { this.stats = s; this.loadingStats = false; },
      error: () => { this.loadingStats = false; },
    });

    this.loadTransactions();
  }

  openDetail(tx: Transaction) { this.selectedTransaction = tx; this.showDetail = true; }
  closeDetail() { this.showDetail = false; this.selectedTransaction = null; }
  goToBeli() { this.showDetail = false; this.router.navigate(['/beli-produk']); }
}
