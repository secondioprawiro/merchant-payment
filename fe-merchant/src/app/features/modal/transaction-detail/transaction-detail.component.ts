import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgIf, DecimalPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-transaction-detail',
  standalone: true,
  imports: [NgIf, DecimalPipe, DatePipe, RouterLink],
  templateUrl: './transaction-detail.component.html',
  styleUrls: ['./transaction-detail.component.css']
})
export class TransactionDetailComponent {
  @Input() detail: any = null;
  @Input() showModal: boolean = false;
  @Input() hideActions: boolean = false;
  @Output() onClose = new EventEmitter<void>();
  @Output() onTransaksiBaru = new EventEmitter<void>();

  close(): void {
    this.onClose.emit();
  }

  transaksiBaru(): void {
    this.onTransaksiBaru.emit();
  }

  failureReasonMap: Record<string, string> = {
    'PROVIDER_TIMEOUT': 'Koneksi Provider Timeout',
    'DESTINATION_INACTIVE': 'Nomor Tujuan Tidak Aktif',
    'INSUFFICIENT_STOCK': 'Stok Produk Tidak Tersedia',
    'PROVIDER_NETWORK_ERROR': 'Gangguan Jaringan Provider'
  }

  getFailureReason(reason: string): string {
    return this.failureReasonMap[reason] || reason
  }
}
