export interface Budget {
  id?: number;
  category: string;
  monthlyLimit: number;
  month: number;
  year: number;
  userId: number;
}
