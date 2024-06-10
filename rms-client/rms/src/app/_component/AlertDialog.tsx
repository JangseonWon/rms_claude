import style from './alertDialog.module.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {faTriangleExclamation, faCircleCheck, faExclamation} from "@fortawesome/free-solid-svg-icons";

type Props = {
    icon: 'warning' | 'good' | 'error';
    message: string;
    onClose: () => void;
};

export default function AlertDialog({ icon, message, onClose }: Props) {
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

    return (
        <div className={style.alertDialogContainer}>
            <div className={style.alertDialog}>
                <FontAwesomeIcon className={style.icon} icon={selectedIcon} />
                <p>Alert Dialog</p>
                <hr />
                <p className={style.message}>{message}</p>
                <button onClick={onClose}>OK</button>
            </div>
        </div>
    );
}