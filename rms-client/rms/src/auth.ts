import NextAuth, {DefaultSession} from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import JWTParser from "jsonwebtoken"
import {cookies} from "next/headers";
import { parse } from "cookie";

declare module '@auth/core/types' {
    interface Session {
        user: {
            id: string,
            branch_name: string,
            branch_serial: string,
            email: string,
            name: string,
            phone_number: string,
            role: string,
            state: string,
            type: string
        } & DefaultSession['user']
    }
}

declare module '@auth/core/jwt' {
    interface JWT {
        user: {
            id: string,
            branch_name: string,
            branch_serial: string,
            email: string,
            name: string,
            phone_number: string,
            role: string,
            state: string,
            type: string
        }
    }
}

export const{handlers: {GET, POST}, auth} = NextAuth({
    trustHost: true,
    pages:{
        signIn: '/login'
    },
    session:{
        maxAge: 60 * 60 * 12
    },
    callbacks: {
        jwt({token, user}){
            return {...token, ...user}
        },
        session({ session, token}) {
            session.user.id = token.user.id
            session.user.branch_name = token.user.branch_name
            session.user.branch_serial = token.user.branch_serial
            session.user.email = token.user.email
            session.user.name = token.user.name
            session.user.role = token.user.role
            session.user.state = token.user.state
            session.user.type = token.user.type
            session.user.phone_number = token.user.phone_number
            return session
        }
    },
    providers: [
        CredentialsProvider({
            async authorize(credentials): Promise<any> {
                const authResponse = await fetch(`${process.env.BASE_URL}/w-api/login-service/login`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        id: credentials.username,
                        password: credentials.password,
                    }),
                })
                if (!authResponse.ok) {
                    return null
                }
                const setCookie = authResponse.headers.get('set-cookie')!
                if (!setCookie) {
                    throw new Error("No set-cookie header received.");
                }

                const parsed = parse(setCookie);
                const authorization = parsed['Authorization']
                const expires = parsed['Expires'];
                if (!authorization || !expires) {
                    throw new Error("Authorization or Expires cookie is missing.");
                }
                const expiresDate = new Date(expires);
                const maxAge = Math.floor((expiresDate.getTime() - Date.now()) / 1000);

                cookies().set('Authorization' as any, authorization as any, {maxAge} as any);
                return JWTParser.decode(authorization, {complete:true})?.payload

            }
        })
    ]
});