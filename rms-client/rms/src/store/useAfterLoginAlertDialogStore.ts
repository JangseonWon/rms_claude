import {create, SetState} from "zustand";

interface AfterAlertDialogState {
    openAlertDialogA: boolean;
    setOpenAlertDialogA: (newOpenAlert: boolean) => void;
    messageAlertDialogA: string;
    setMessageAlertDialogA: (newMessage: string) => void;
    refresh: boolean;
    setRefresh: (newRefresh: boolean) => void;
}

const useAfterAlertDialogStore = create<AfterAlertDialogState>((set: SetState<AfterAlertDialogState>) => ({
    openAlertDialogA: false,
    setOpenAlertDialogA: (newOpenAlert) => set({ openAlertDialogA: newOpenAlert}),
    messageAlertDialogA: 'warning',
    setMessageAlertDialogA: (newMessage) => set({ messageAlertDialogA: newMessage}),
    refresh: false,
    setRefresh: (newRefresh) => set({ refresh: newRefresh}),
}));

export const useAlertDialogA = () => useAfterAlertDialogStore((state) => state.openAlertDialogA);
export const useOpenAlertDialogA = () => useAfterAlertDialogStore((state) => state.setOpenAlertDialogA);
export const useMessageAlertDialogA = () => useAfterAlertDialogStore((state) => state.messageAlertDialogA);
export const useSetMessageAlertDialogA = () => useAfterAlertDialogStore((state) => state.setMessageAlertDialogA);
export const useRefresh = () => useAfterAlertDialogStore((state) => state.refresh);
export const useSetRefresh = () => useAfterAlertDialogStore((state) => state.setRefresh);