import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'fiche/:slug',
    loadComponent: () => import('./pages/fiche/fiche.component').then(m => m.FicheComponent)
  },
  {
    path: 'questions',
    loadComponent: () => import('./pages/questions/questions.component').then(m => m.QuestionsComponent)
  },
  {
    path: 'callback',
    loadComponent: () => import('./pages/callback/callback.component').then(m => m.CallbackComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
