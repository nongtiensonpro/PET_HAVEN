import { defineStore } from "pinia";
import type User from "~/models/User";

interface UserState {
    users: User[];
}

export const useStore = defineStore("userStore", {
    state: (): UserState => ({
        users: [] as User[],
    }),
    actions: {
        async fetchUsers() {
            const token = localStorage.getItem("access_token");
            try {
                const response = await fetch("http://localhost:8080/api/all-users", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (!response.ok) {
                    throw new Error("Failed to fetch users");
                }
                const data = await response.json();
                console.log(data);
                this.users = data;
                return this.users;
            } catch (error) {
                console.error("Error fetching users:", error);
                return [];
            }
        },
    },
});