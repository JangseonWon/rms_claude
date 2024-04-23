import React, {ReactNode} from "react";
import Header from "@/app/(afterLogin)/_component/Header";
import {auth} from "@/auth";

type Props = { children: ReactNode};
export default async function Layout({ children }: Props) {
    const session = await auth();
    return (
        <>
            <Header session={session}/>
            {children}
        </>
    )
}
