import { Component, inject, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { MerchantService, MerchantProfile } from '../../services/merchant.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit {
  private auth = inject(AuthService);
  private merchantService = inject(MerchantService);

  menuItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
    { label: 'Beli produk', icon: 'bolt', route: '/beli-produk' },
    { label: 'Riwayat transaksi', icon: 'receipt_long', route: '/riwayat-transaksi' },
  ];

  accountItems: NavItem[] = [
    { label: 'Profil', icon: 'person', route: '/profil' },
    { label: 'Pengaturan', icon: 'settings', route: '/pengaturan' },
  ];

  profile: MerchantProfile | null = null;

  get initials(): string {
    return this.profile?.namaMerchant.charAt(0).toUpperCase() ?? '?';
  }

  ngOnInit() {
    this.merchantService.getProfile().subscribe({
      next: (p) => (this.profile = p),
    });
  }

  logout() {
    this.auth.logout();
  }
}
