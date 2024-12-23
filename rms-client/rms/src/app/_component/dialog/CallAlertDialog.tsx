'use client';

import {useOpenAlertDialogA, useSetMessageAlertDialogA} from "@/store/useAfterLoginAlertDialogStore";

export const CallAlertDialog = () => {
    const setShowAlertDialog = useOpenAlertDialogA();
    const setMessage = useSetMessageAlertDialogA();

    return (message: string) => {
        setMessage(message);
        setShowAlertDialog(true);
    };
};