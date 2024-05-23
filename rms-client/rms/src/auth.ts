import NextAuth, {DefaultSession} from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import JWTParser from "jsonwebtoken"
import {cookies} from "next/headers";
import cookie from 'cookie'
import {UUID} from "node:crypto";

declare module 'next-auth' {
    interface Session {
        user: {
            id: string,
            branch_name: string,
            branch_serial: string,
            email: string,
            name: string,
            role: string,
            state: string,
            type: string,
            phone_number: string,
            key: UUID
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
            role: string,
            state: string,
            type: string,
            phone_number: string,
            key: UUID
        }
    }
}

export const{
    handlers: {GET, POST},
    auth,
    signIn
} = NextAuth({
    pages:{
        signIn: '/login'
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
            session.user.key = token.user.key
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

                const [authorizationHeader, ...remainingHeaders] = setCookie?.split(';')
                const authorization = authorizationHeader.split('=')[1];
                if (setCookie) {
                    const parsed = cookie.parse(setCookie);
                    cookies().set('Authorization', parsed['Authorization'], parsed); // 브라우저에 쿠키를 심어주는 것
                }

                return JWTParser.decode(authorization, {complete:true})?.payload
            }
        })
    ]
});