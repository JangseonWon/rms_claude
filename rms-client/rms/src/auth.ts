import NextAuth, {DefaultSession} from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import forge from "node-forge"
import JWTParser, {Secret} from "jsonwebtoken"
import {JWT} from "@auth/core/jwt";
import {Session} from "@auth/core/types";
import Credentials from "next-auth/providers/credentials";
import {string} from "prop-types";

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
            role: string,
            state: string,
            type: string
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
            return session
        }
    },
    providers: [
        CredentialsProvider({
            async authorize(credentials) {
                const authResponse = await fetch(`${process.env.BASE_URL}/w-api/login`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        id: credentials.username,
                        password: credentials.password,
                    }),
                })
                if (!authResponse.ok) throw new Error('Authorization header not found or invalid format');
                const cookie = authResponse.headers.get('set-cookie')!

                const [authorizationHeader, ...remainingHeaders] = cookie?.split(';')
                const authorization = authorizationHeader.split('=')[1];
                return JWTParser.decode(authorization, {complete:true})?.payload
            }
        })
    ]
});