'use client';

import React, {ReactNode} from "react";
import style from "@/app/(beforeLogin)/layout.module.css";
import Header from "@/app/(beforeLogin)/_component/Header";
import Footer from "@/app/_component/Footer";
import AlertDialog from "@/app/_component/AlertDialog";
import {useAlertDialog, useMessageAlertDialog} from "@/store/useAlertDialogStore";

type Props = { children: ReactNode};
export default function Layout({ children }: Props) {
    const showAlertDialog = useAlertDialog();
    const message = useMessageAlertDialog();

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
