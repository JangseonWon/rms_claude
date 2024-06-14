import {create, SetState} from "zustand";

interface AlertDialogState {
    openAlertDialog: boolean;
    setOpenAlertDialog: (newOpenAlert: boolean) => void;
    messageAlertDialog: string;
    setMessageAlertDialog: (newMessage: string) => void;
    iconAlertDialog: 'warning' | 'good' | 'error';
    setIconAlertDialog: (newIcon: 'warning' | 'good' | 'error') => void;
}

const useAlertDialogStore = create<AlertDialogState>((set: SetState<AlertDialogState>) => ({
    openAlertDialog: false,
    setOpenAlertDialog: (newOpenAlert) => set({ openAlertDialog: newOpenAlert}),
    messageAlertDialog: 'warning',
    setMessageAlertDialog: (newMessage) => set({ messageAlertDialog: newMessage}),
    iconAlertDialog: 'warning',
    setIconAlertDialog: (newIcon) => set({ iconAlertDialog: newIcon})
}));

export const useAlertDialog = () => useAlertDialogStore((state) => state.openAlertDialog);
export const useOpenAlertDialog = () => useAlertDialogStore((state) => state.setOpenAlertDialog);
export const useMessageAlertDialog = () => useAlertDialogStore((state) => state.messageAlertDialog);
export const useSetMessageAlertDialog = () => useAlertDialogStore((state) => state.setMessageAlertDialog);
export const useIconAlertDialog = () => useAlertDialogStore((state) => state.iconAlertDialog);
export const useSetIconAlertDialog = () => useAlertDialogStore((state) => state.setIconAlertDialog);