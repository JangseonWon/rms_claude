import style from './alertDialog.module.css';
import React from "react";
import {useOpenAlertDialogB} from "@/store/useBeforeLoginAlertDialogStore";
import {useOpenAlertDialogA} from "@/store/useAfterLoginAlertDialogStore";

type Props = {
    message: string;
};

export default function AlertDialog({ message }: Props) {
    const setShowDialogA = useOpenAlertDialogA();
    const setShowDialogB = useOpenAlertDialogB();

    const handleCloseDialog = () => {
        setShowDialogA(false);
        setShowDialogB(false);
    }

    const formattedMessage = message.split('\n').map((line, index) => (
        <React.Fragment key={index}>
            {line}
            <br />
        </React.Fragment>
    ));

    return (
        <div className={style.alertDialogContainer}>
            <div className={style.alertDialog}>
                <p>Warning</p>
                <hr />
                <p className={style.message}>{formattedMessage}</p>
                <button onClick={handleCloseDialog}>OK</button>
            </div>
        </div>
    );
}