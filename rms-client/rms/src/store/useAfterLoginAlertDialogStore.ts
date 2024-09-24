import {create, SetState} from "zustand";

interface AfterAlertDialogState {
    openAlertDialogA: boolean;
    setOpenAlertDialogA: (newOpenAlert: boolean) => void;
    messageAlertDialogA: string;
    setMessageAlertDialogA: (newMessage: string) => void;
    iconAlertDialogA: 'warning' | 'good' | 'error';
    setIconAlertDialogA: (newIcon: 'warning' | 'good' | 'error') => void;
}

const useAfterAlertDialogStore = create<AfterAlertDialogState>((set: SetState<AfterAlertDialogState>) => ({
    openAlertDialogA: false,
    setOpenAlertDialogA: (newOpenAlert) => set({ openAlertDialogA: newOpenAlert}),
    messageAlertDialogA: 'warning',
    setMessageAlertDialogA: (newMessage) => set({ messageAlertDialogA: newMessage}),
    iconAlertDialogA: 'warning',
    setIconAlertDialogA: (newIcon) => set({ iconAlertDialogA: newIcon})
}));

export const useAlertDialogA = () => useAfterAlertDialogStore((state) => state.openAlertDialogA);
export const useOpenAlertDialogA = () => useAfterAlertDialogStore((state) => state.setOpenAlertDialogA);
export const useMessageAlertDialogA = () => useAfterAlertDialogStore((state) => state.messageAlertDialogA);
export const useSetMessageAlertDialogA = () => useAfterAlertDialogStore((state) => state.setMessageAlertDialogA);
export const useIconAlertDialogA = () => useAfterAlertDialogStore((state) => state.iconAlertDialogA);
export const useSetIconAlertDialogA = () => useAfterAlertDialogStore((state) => state.setIconAlertDialogA);