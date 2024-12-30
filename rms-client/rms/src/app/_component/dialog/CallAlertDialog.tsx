'use client';

import {useOpenAlertDialogA, useSetMessageAlertDialogA, useSetRefresh} from "@/store/useAfterLoginAlertDialogStore";

export const CallAlertDialog = () => {
    const setShowAlertDialog = useOpenAlertDialogA();
    const setMessage = useSetMessageAlertDialogA();
    const setRefresh = useSetRefresh();

    return (message: string, refresh: boolean | null = false) => {
        setMessage(message);
        setShowAlertDialog(true);
        setRefresh(refresh ?? false);
    };
};