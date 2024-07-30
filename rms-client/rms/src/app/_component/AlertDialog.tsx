import style from './alertDialog.module.css';
import React from "react";
import {useOpenAlertDialog} from "@/store/useAlertDialogStore";

type Props = {
    message: string;
};

export default function AlertDialog({ message }: Props) {
    const setShowDialog = useOpenAlertDialog();

    const handleCloseDialog = () => {
        setShowDialog(false);
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
                <p>Notice</p>
                <hr />
                <p className={style.message}>{formattedMessage}</p>
                <button onClick={handleCloseDialog}>OK</button>
            </div>
        </div>
    );
}