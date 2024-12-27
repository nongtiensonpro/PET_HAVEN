import { defineStore } from "pinia";
import type ThuCungKhachHang from "~/models/ThuCungKhachHang";
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
                this.users = data;
                return this.users;
            } catch (error) {
                console.error("Error fetching users:", error);
                return [];
            }
        },
        async addPet(pet: ThuCungKhachHang) {
            const token = localStorage.getItem("access_token");
            try {
                const response = await fetch("http://localhost:8080/api/thu-cung/add", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify(pet),
                });
                if (!response.ok) {
                    throw new Error("Failed to add pet");
                }
                const data = await response.json();
                this.fetchUsers();
                return data;
            } catch (error) {
                console.error("Error adding pet:", error);
                return null;
            }
        },
        async updatePet(pet: ThuCungKhachHang) {
            const token = localStorage.getItem("access_token");
            try {
                const response = await fetch("http://localhost:8080/api/thu-cung/update", {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify(pet),
                });
                if (!response.ok) {
                    throw new Error("Failed to add pet");
                }
                const data = await response.json();
                this.fetchUsers();
                return data;
            } catch (error) {
                console.error("Error adding pet:", error);
                return null;
            }
        }
    },
});