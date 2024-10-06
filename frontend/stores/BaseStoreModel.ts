export default class BaseStore<T> {
    items: T[];
    pageable: Pageable;
    loading: boolean;
    error: string | null;
    fetchItems: (endpoint: string, params?: Record<string, any>) => Promise<void>;
    addItem: (newItem: T) => Promise<void>;
    updateItem: (updatedItem: T) => Promise<void>;
    deleteItem: (itemId: number) => Promise<void>;
    cleanPayload: (payload: any) => any;
}

export default class Pageable {
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}