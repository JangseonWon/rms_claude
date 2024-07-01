import style from './noticeDialog.module.css';
import {useOpenNoticeDialog, useSetOkNotice} from "@/store/useNoticeDialogStore";

type Props = { message: string; };

export default function NoticeDialog({ message }: Props) {
    const setShowDialog = useOpenNoticeDialog();
    const setOkDialog = useSetOkNotice();

    const handleCloseDialog = () => {
        setShowDialog(false);
    }

    const handleOkDialog = () => {
        setOkDialog(true);
        setShowDialog(false);
    }

    return (
        <div className={style.noticeDialogContainer}>
            <div className={style.noticeDialog}>
                <p>Notice</p>
                <hr/>
                <p className={style.message}>{message}</p>
                <button className={style.cancelButton} onClick={handleCloseDialog}>Cancel</button>
                <button className={style.okButton} onClick={handleOkDialog}>OK</button>
            </div>
        </div>
    );
}