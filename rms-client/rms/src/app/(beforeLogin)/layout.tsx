'use client';

import React, {ReactNode, useEffect, useState} from "react";
import style from "@/app/(beforeLogin)/layout.module.css";
import Header from "@/app/(beforeLogin)/_component/Header";
import Footer from "@/app/_component/Footer";
import AlertDialog from "@/app/_component/dialog/AlertDialog";
import {useAlertDialogB, useMessageAlertDialogB} from "@/store/useBeforeLoginAlertDialogStore";
import MainLoading from "@/app/_component/MainLoading";

type Props = { children: ReactNode};
export default function Layout({ children }: Props) {
    const [isLoading, setIsLoading] = useState(true);
    const showAlertDialog = useAlertDialogB();
    const message = useMessageAlertDialogB();

    useEffect(() => {
        const timer = setTimeout(() => {
            setIsLoading(false);
        }, 1000);

        return () => clearTimeout(timer);
    }, []);

    useEffect(() => {
        if (!isLoading) {
            setIsLoading(false);
        }
    }, [children]);

    if (isLoading) {
        return <MainLoading />;
    }

    return (
        <div className={style.container}>
            {showAlertDialog && (<AlertDialog message={message}/>)}
            <section className={style.topSection}>
                <Header/>
            </section>
            <section className={style.bodySection}>
                {children}
            </section>
            <section className={style.bottomSection}>
                <Footer/>
            </section>
        </div>
    )
}
