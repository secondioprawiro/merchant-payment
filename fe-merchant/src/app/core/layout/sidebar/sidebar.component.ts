import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

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
export class SidebarComponent {
  menuItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
    { label: 'Beli produk', icon: 'bolt', route: '/beli-produk' },
    { label: 'Riwayat transaksi', icon: 'receipt_long', route: '/riwayat-transaksi' },
  ];

  accountItems: NavItem[] = [
    { label: 'Profil', icon: 'person', route: '/profil' },
    { label: 'Pengaturan', icon: 'settings', route: '/pengaturan' },
  ];

  merchant = {
    name: 'Toko Berkah',
    code: 'MCH-00421',
    initials: 'TB',
  };
}
