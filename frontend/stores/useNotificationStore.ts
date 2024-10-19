import { defineStore } from 'pinia';

export const useNotificationStore = defineStore('notification', {
    state: () => ({
        notifications: [] as Array<{ message: string; type: string }>,
    }),
    actions: {
        addNotification(message: string, type: string = 'success') {
            this.notifications.push({ message, type });
        },
        removeNotification(index: number) {
            this.notifications.splice(index, 1);
        },
        removeAllNotifications() {
            this.notifications = [];
        }
    },
});
