import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, DecimalPipe } from '@angular/common';
import { ProductService } from '../../../core/services/product.service';

// import { TransactionService } from '../../services/transaction.service';

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
  imports: [NgFor, NgIf, DecimalPipe, FormsModule],
  templateUrl: './buy.component.html',
  styleUrls: ['./buy.component.css']
})
export class BuyComponent implements OnInit {
  selectedType: 'PULSA' | 'PLN' = 'PULSA';
  selectedProduct: Product | null = null;
  nomorTujuan: string = '';
  merchantName: string = 'Toko Berkah';

  products: Product[] = [];

  get filteredProducts(): Product[] {
    return this.products.filter(p => p.type === this.selectedType);
  }

  constructor(
    private productService: ProductService,
    // private transactionService: TransactionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.productService.getProducts().subscribe(data => {
      this.products = data;
      this.selectedProduct = this.filteredProducts[0] ?? null;
    });
  }

  selectType(type: 'PULSA' | 'PLN'): void {
    this.selectedType = type;
    this.selectedProduct = this.filteredProducts[0] ?? null;
    this.nomorTujuan = '';
  }

  selectProduct(product: Product): void {
    this.selectedProduct = product;
  }

  prosesTransaksi(): void {
    if (!this.selectedProduct || !this.nomorTujuan) return;

    const payload = {
      productId: this.selectedProduct.productId,
      nomorTujuan: this.nomorTujuan
    };

    // this.transactionService.process(payload).subscribe({
    //   next: (res) => {
    //     this.router.navigate(['/transactions/result'], {
    //       state: { result: res }
    //     });
    //   },
    //   error: (err) => {
    //     this.router.navigate(['/transactions/result'], {
    //       state: { result: { status: 'FAILED', failureReason: err.message } }
    //     });
    //   }
    // });
  }

  onInputNomor(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
    this.nomorTujuan = input.value;
  }
}
