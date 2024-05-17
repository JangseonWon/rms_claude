import React, {ReactNode} from "react";
import Header from "@/app/(afterLogin)/_component/Header";
import {auth} from "@/auth";
import '@fortawesome/fontawesome-svg-core/styles.css';
import {config} from '@fortawesome/fontawesome-svg-core';
import style from "@/app/(afterLogin)/layout.module.css";
import Footer from "@/app/_component/Footer";

config.autoAddCss = false;

type Props = { children: ReactNode};
export default async function Layout({ children }: Props) {
    const session = await auth();
    return (
        <div className={style.layout}>
            <div className={style.header}>
                <Header session={session}/>
            </div>
            <div className={style.content}>
                {children}
                <Footer/>
            </div>
        </div>
    )
}
