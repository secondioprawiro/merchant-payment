import { Component, OnInit, inject  } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, DecimalPipe } from '@angular/common';
import { ProductService } from '../../../core/services/product.service';
import {MerchantService} from '../../../core/services/merchant.service';
import {TransactionService} from '../../../core/services/transaction.service';
import {TransactionDetailComponent } from '../../modal/transaction-detail/transaction-detail.component';
import { MatDialog } from '@angular/material/dialog';
import {WarningDialogComponent } from '../../modal/warning-dialog/warning-dialog.component';


interface Product {
  productId: string;
  productName: string;
  nominal: number,
  price: number;
  type: 'PULSA' | 'PLN';
  status: string;
}

@Component({
  selector: 'app-buy',
  standalone: true,
  imports: [NgFor, NgIf, DecimalPipe, FormsModule, TransactionDetailComponent],
  templateUrl: './buy.component.html',
  styleUrls: ['./buy.component.css']
})
export class BuyComponent implements OnInit {
  selectedType: 'PULSA' | 'PLN' = 'PULSA';
  selectedProduct: Product | null = null;
  nomorTujuan: string = '';
  merchantName: string = '';
  products: Product[] = [];
  showModal: boolean = false;
  transactionDetail: any = null;
  isLoading: boolean = false;

  get filteredProducts(): Product[] {
    return this.products.filter(p => p.type === this.selectedType);
  }

  constructor(
    private productService: ProductService,
    private merchantService : MerchantService,
    private transactionService: TransactionService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.productService.getProducts().subscribe(data => {
      this.products = data;
      this.selectedProduct = this.filteredProducts[0] ?? null;
    });
    this.merchantService.getProfile().subscribe(res => {
      this.merchantName = res.namaMerchant;
      }
    )
  }

  selectType(type: 'PULSA' | 'PLN'): void {
    this.selectedType = type;
    this.selectedProduct = this.filteredProducts[0] ?? null;
    this.nomorTujuan = '';
  }

  selectProduct(product: Product): void {
    this.selectedProduct = product;
  }

  private showWarning(message: string) {
    this.dialog.open(WarningDialogComponent, {
      width: '360px',
      data: { message },
      disableClose: true
    });
  }

  prosesTransaksi(): void {
    if (!this.selectedProduct || !this.nomorTujuan) return;
    this.isLoading = true;
    this.transactionService.buyProduct(
      this.selectedProduct.productId,
      this.nomorTujuan
    ).subscribe({
      next: (res) => {
        console.log('Response transaksi:', res);
        this.isLoading = false;
        this.transactionDetail= res.data;
        this.showModal = true;
      },
      error: (err) => {
        const status = err.status;
        const message = err.error?.message ?? 'Terjadi kesalahan';

        if (status === 400 || status === 404) {
          this.showWarning(message);
        } else {
          this.showWarning('Gagal menghubungi server, coba lagi.');
        }
      }
    });
  }

  onInputNomor(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
    this.nomorTujuan = input.value;
  }

  closeModal(): void {
    this.showModal = false;
    this.transactionDetail = null;
  }

  transaksiBaru(): void {
    this.showModal = false;
    this.selectedProduct = null;
    this.nomorTujuan = '';
  }

}
