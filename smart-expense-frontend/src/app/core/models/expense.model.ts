export interface Expense {
  id?: number;
  title: string;
  amount: number;
  category: string;
  description?: string;
  date: string;
  userId: number;
}
