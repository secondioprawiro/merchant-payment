import { Routes } from '@angular/router';
import { ShellComponent } from './core/layout/shell/shell.component';
import { authGuard } from './core/auth/auth.guard';
import { adminGuard } from './core/auth/admin.guard';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
      },
      {
        path: 'profil',
        loadComponent: () =>
          import('./features/profil/profil/profil.component').then(m => m.ProfilComponent),
      },
      {
        path: 'riwayat-transaksi',
        loadComponent: () =>
          import('./features/riwayat-transaksi/riwayat-transaksi.component').then(m => m.RiwayatTransaksiComponent),
      },
      {
        path: 'admin',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/admin/merchant-list/merchant-list.component').then(m => m.MerchantListComponent),
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'beli-produk',
        loadComponent: () =>
          import('./features/products/buy/buy.component').then(m => m.BuyComponent),
      }
    ],
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(m => m.RegisterComponent),
  },
];
