import React from "react";
import styles from "@/app/_component/dialog/confirmdialog.module.css";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";

interface ConfirmDialogProps {
    title: string;
    message: string;
    onConfirm: () => void;
    onCancel: () => void;
}

export default function ConfirmDialog({ title, message, onConfirm, onCancel }: ConfirmDialogProps) {
    return (
        <div className={styles.overlay}>
            <div className={styles.dialog}>
                <p>{title}</p>
                <hr/>
                <p className={styles.message}>
                    {message.split("\n").map((line, idx) => (
                        <React.Fragment key={idx}>
                            {line}
                            <br />
                        </React.Fragment>
                    ))}
                </p>
                <div className={styles.buttonGroup}>
                    <GreenButton name={'Cancel'} onClick={onCancel}/>
                    <BlueButton name={'OK'} onClick={onConfirm}/>
                </div>
            </div>
        </div>
    );
}