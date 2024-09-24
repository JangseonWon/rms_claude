import {create, SetState} from "zustand";

interface BeforeAlertDialogState {
    openAlertDialogB: boolean;
    setOpenAlertDialogB: (newOpenAlert: boolean) => void;
    messageAlertDialogB: string;
    setMessageAlertDialogB: (newMessage: string) => void;
    iconAlertDialogB: 'warning' | 'good' | 'error';
    setIconAlertDialogB: (newIcon: 'warning' | 'good' | 'error') => void;
}

const useBeforeLoginAlertDialogStore = create<BeforeAlertDialogState>((set: SetState<BeforeAlertDialogState>) => ({
    openAlertDialogB: false,
    setOpenAlertDialogB: (newOpenAlert) => set({ openAlertDialogB: newOpenAlert}),
    messageAlertDialogB: 'warning',
    setMessageAlertDialogB: (newMessage) => set({ messageAlertDialogB: newMessage}),
    iconAlertDialogB: 'warning',
    setIconAlertDialogB: (newIcon) => set({ iconAlertDialogB: newIcon})
}));

export const useAlertDialogB = () => useBeforeLoginAlertDialogStore((state) => state.openAlertDialogB);
export const useOpenAlertDialogB = () => useBeforeLoginAlertDialogStore((state) => state.setOpenAlertDialogB);
export const useMessageAlertDialogB = () => useBeforeLoginAlertDialogStore((state) => state.messageAlertDialogB);
export const useSetMessageAlertDialogB = () => useBeforeLoginAlertDialogStore((state) => state.setMessageAlertDialogB);
export const useIconAlertDialogB = () => useBeforeLoginAlertDialogStore((state) => state.iconAlertDialogB);
export const useSetIconAlertDialogB = () => useBeforeLoginAlertDialogStore((state) => state.setIconAlertDialogB);
//