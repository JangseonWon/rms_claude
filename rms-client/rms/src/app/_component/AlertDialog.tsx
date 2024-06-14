import style from './alertDialog.module.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {faTriangleExclamation, faCircleCheck, faExclamation} from "@fortawesome/free-solid-svg-icons";
import {useOpenAlertDialog} from "@/store/useAlertDialogStore";

type Props = {
    icon: 'warning' | 'good' | 'error';
    message: string;
};

export default function AlertDialog({ icon, message}: Props) {
    const setShowDialog = useOpenAlertDialog();

    let selectedIcon;
    switch (icon) {
        case 'warning':
            selectedIcon = faTriangleExclamation;
            break;
        case 'good':
            selectedIcon = faCircleCheck;
            break;
        case 'error':
            selectedIcon = faExclamation;
            break;
        default:
            selectedIcon = faTriangleExclamation;
    }
    const handleCloseDialog = () => {
        setShowDialog(false);
    }

    return (
        <div className={style.alertDialogContainer}>
            <div className={style.alertDialog}>
                <FontAwesomeIcon className={style.icon} icon={selectedIcon} />
                <p>Alert Dialog</p>
                <hr />
                <p className={style.message}>{message}</p>
                <button onClick={handleCloseDialog}>OK</button>
            </div>
        </div>
    );
}