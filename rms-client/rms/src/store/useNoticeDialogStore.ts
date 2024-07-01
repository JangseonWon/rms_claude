import {create, SetState} from "zustand";

interface NoticeDialogStore {
    openNoticeDialog: boolean;
    setOpenNoticeDialog: (newOpenNotice: boolean) => void;
    okNotice: boolean;
    setOkNotice: (newOkNotice: boolean) => void;
    messageNoticeDialog: string;
    setMessageNoticeDialog: (newMessage: string) => void;
}

const useNoticeDialogStore = create<NoticeDialogStore>((set: SetState<NoticeDialogStore>) => ({
    openNoticeDialog: false,
    setOpenNoticeDialog: (newOpenAlert) => set({ openNoticeDialog: newOpenAlert}),
    okNotice: false,
    setOkNotice: (newOkNotice) => set({ okNotice: newOkNotice}),
    messageNoticeDialog: 'warning',
    setMessageNoticeDialog: (newMessage) => set({ messageNoticeDialog: newMessage})
}));

export const useNoticeDialog = () => useNoticeDialogStore((state) => state.openNoticeDialog);
export const useOpenNoticeDialog = () => useNoticeDialogStore((state) => state.setOpenNoticeDialog);
export const useOkNotice = () => useNoticeDialogStore((state) => state.okNotice);
export const useSetOkNotice = () => useNoticeDialogStore((state) => state.setOkNotice);
export const useMessageNoticeDialog = () => useNoticeDialogStore((state) => state.messageNoticeDialog);
export const useSetMessageNoticeDialog = () => useNoticeDialogStore((state) => state.setMessageNoticeDialog);