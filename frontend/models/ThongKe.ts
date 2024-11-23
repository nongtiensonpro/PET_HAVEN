export interface ThongKeItem {
    date: string;
    amount: number;
}

export interface ThongKeResponse {
    data: ThongKeItem[];
    message: string;
}

export class ThongKe {
    date: Date;
    amount: number;

    constructor(date: string, amount: number) {
        this.date = new Date(date);
        this.amount = amount;
    }

    static fromResponse(response: ThongKeResponse): ThongKe[] {
        return response.data.map(([date, amount]) => new ThongKe(date, amount));
    }
}