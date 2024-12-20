export const useRestUser = defineStore('restUser', () => {
    const userStore = useUserStore();

    const refreshAccessToken = async () => {
        const refreshToken = localStorage.getItem('refresh_token');
        if (!refreshToken) {
            console.error('No refresh token available');
            userStore.setLoggedIn(false);
            return null;
        }

        const url = 'http://localhost:9082/realms/spring/protocol/openid-connect/token';

        const params = new URLSearchParams({
            grant_type: 'refresh_token',
            refresh_token: refreshToken,
            client_id: 'PetHaven',
            client_secret: 'GuFIaAADNfBUpuahqxLvMPWzqt6g8fRL',
        });

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: params.toString()
            });

            if (!response.ok) {
                const errorData = await response.json();
                console.error(`Error refreshing token: ${errorData.error_description}`);
                return null;
            }

            const data = await response.json();
            localStorage.setItem('access_token', data.access_token);
            localStorage.setItem('refresh_token', data.refresh_token);
            console.log('New access token:', data.access_token);
            return data.access_token;
        } catch (error) {
            localStorage.setItem('viewRole', '0');
            console.error('Error refreshing token:', error);
            return null;
        }
    };

    const fetchUserInfo = async (accessToken: string) => {
        const url = 'http://localhost:8080/api/user';
        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 401) {
                console.log('Access token expired, refreshing token...');
                const newAccessToken = await refreshAccessToken();
                if (newAccessToken) {
                    return fetchUserInfo(newAccessToken);
                } else {
                    console.error('Unable to refresh access token');
                    userStore.setLoggedIn(false);
                    return;
                }
            }

            if (!response.ok) {
                const errorData = await response.json();
                console.error(`Error fetching user info: ${errorData.error_description}`);
                return;
            }

            const userInfoData = await response.json();
            console.log('User Info:', userInfoData);

            const roles = userInfoData.roles || [];
            userStore.setUserInfo({
                name: userInfoData.username || '',
                role: roles || '',
                listThuCung: userInfoData.listThuCung || ''
            });

            userStore.setLoggedIn(true);

            if (userInfoData.username) {
                console.log('User info refreshed successfully');
            }
        } catch (error) {
            localStorage.setItem('viewRole', '0');
            console.error('Error fetching user info:', error);
            userStore.setLoggedIn(false);
        }
    };

    return {
        refreshAccessToken,
        fetchUserInfo
    };
});