// server/api/auth/[...].ts
import { NuxtAuthHandler } from '#auth'

export default NuxtAuthHandler({
    secret: useRuntimeConfig().authSecret,
    providers: [
        {
            id: 'keycloak',
            name: 'Keycloak',
            type: 'oauth',
            clientId: process.env.KEYCLOAK_CLIENT_ID,
            clientSecret: process.env.KEYCLOAK_CLIENT_SECRET,
            issuer: process.env.KEYCLOAK_ISSUER,
            authorization: process.env.AUTH_AUTHORIZATION_ENDPOINT,
            token: process.env.AUTH_TOKEN_ENDPOINT,
            userinfo: process.env.AUTH_USERINFO_ENDPOINT,
            profile(profile) {
                return {
                    id: profile.sub,
                    name: profile.name,
                    email: profile.email,
                    image: profile.picture,
                }
            },
        }
    ]
})