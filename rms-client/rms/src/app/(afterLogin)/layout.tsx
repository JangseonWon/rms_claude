'use client';

import React, {ReactNode} from "react";
import Header from "@/app/(afterLogin)/_component/Header";
import '@fortawesome/fontawesome-svg-core/styles.css';
import {config} from '@fortawesome/fontawesome-svg-core';
import style from "@/app/(afterLogin)/layout.module.css";
import Footer from "@/app/_component/Footer";
import {useAlertDialogA, useMessageAlertDialogA} from "@/store/useAfterLoginAlertDialogStore";
import AlertDialog from "@/app/_component/dialog/AlertDialog";

config.autoAddCss = false;

type Props = { children: ReactNode};
export default function Layout({ children }: Props) {
    const showAlertDialog = useAlertDialogA();
    const message = useMessageAlertDialogA();

    return (
        <div className={style.layout}>
            {showAlertDialog && (<AlertDialog message={message}/>)}
                <div className={style.header}>
                    <Header/>
                </div>
                <div className={style.content}>
                    {children}
                </div>
                <div className={style.footer}>
                    <Footer/>
                </div>
        </div>
    )
}
