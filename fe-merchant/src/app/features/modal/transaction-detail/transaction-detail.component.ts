import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgIf, DecimalPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'app-transaction-detail',
  standalone: true,
  imports: [NgIf, DecimalPipe, DatePipe],
  templateUrl: './transaction-detail.component.html',
  styleUrls: ['./transaction-detail.component.css']
})
export class TransactionDetailComponent {
  @Input() detail: any = null;
  @Input() showModal: boolean = false;
  @Output() onClose = new EventEmitter<void>();
  @Output() onTransaksiBaru = new EventEmitter<void>();

  close(): void {
    this.onClose.emit();
  }

  transaksiBaru(): void {
    this.onTransaksiBaru.emit();
  }
}
