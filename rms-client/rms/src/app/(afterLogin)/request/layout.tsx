'use client';

import {ReactNode} from "react";
import NavMenu from "@/app/(afterLogin)/_component/NavMenu";
import style from "@/app/(afterLogin)/request/layout.module.css"
import AlertDialog from "@/app/_component/dialog/AlertDialog";
import {useAlertDialogB, useMessageAlertDialogB} from "@/store/useBeforeLoginAlertDialogStore";
import NoticeDialog from "@/app/_component/dialog/NoticeDialog";
import {useMessageNoticeDialog, useNoticeDialog} from "@/store/useNoticeDialogStore";

type Props = {
    children: ReactNode;
};

export default function Layout({ children}: Props) {
    const showAlertDialog = useAlertDialogB();
    const alertMessage = useMessageAlertDialogB();
    const showNoticeDialog = useNoticeDialog();
    const noticeMessage = useMessageNoticeDialog();

    return (
        <div className={style.container}>
            {showAlertDialog && (<AlertDialog message={alertMessage}/>)}
            {showNoticeDialog && (<NoticeDialog message={noticeMessage}/>)}
            <div className={style.leftSection}>
                <NavMenu/>
            </div>
            <div className={style.rightSection}>
                {children}
            </div>
        </div>
    )
}
