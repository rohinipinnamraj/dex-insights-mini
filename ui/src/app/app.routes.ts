import { Routes } from '@angular/router';
import { StoresComponent } from './pages/stores/stores.component';
import { StoreDetailComponent } from './pages/store-detail/store-detail.component';
import { InsightsComponent } from './pages/insights/insights.component';
import { ChatComponent } from './pages/chat/chat.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'stores',
    pathMatch: 'full'
  },
  {
    path: 'stores',
    component: StoresComponent
  },
  {
    path: 'stores/:storeId',
    component: StoreDetailComponent
  },
  {
    path: 'insights',
    component: InsightsComponent
  },
  {
    path: 'chat',
    component: ChatComponent
  }
];