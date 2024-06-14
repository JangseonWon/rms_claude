'use client';

import {ReactNode} from "react";
import NavMenu from "@/app/(afterLogin)/request/_component/NavMenu";
import style from "@/app/(afterLogin)/request/layout.module.css"
import AlertDialog from "@/app/_component/AlertDialog";
import {useAlertDialog, useIconAlertDialog, useMessageAlertDialog} from "@/store/useAlertDialogStore";

type Props = { children: ReactNode, modal: ReactNode }
export default function Layout({ children, modal }: Props) {
    const showAlertDialog = useAlertDialog();
    const message = useMessageAlertDialog();
    const icon = useIconAlertDialog();

    return (
        <div className={style.container}>
            {showAlertDialog && (<AlertDialog icon={icon} message={message}/>)}
            <div className={style.leftSection}>
                <NavMenu/>
            </div>
            <div className={style.rightSection}>
                {modal}
                {children}
            </div>
        </div>

    )
}
