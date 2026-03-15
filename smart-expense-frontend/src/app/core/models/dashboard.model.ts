export interface Dashboard {
  totalSpentThisMonth: number;
  spentByCategory: { [key: string]: number };
  budgetByCategory: { [key: string]: number };
  budgetAlerts: { [key: string]: string };
}
