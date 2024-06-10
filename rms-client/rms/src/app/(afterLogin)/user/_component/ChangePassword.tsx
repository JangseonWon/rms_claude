import style from './changePassword.module.css';
import AlertDialog from "@/app/_component/AlertDialog";
import * as React from "react";
import {useState} from "react";
import InputBox from "@/app/_component/InputBox";
import GreenButton from "@/app/_component/GreenButton";
import {useSession} from "next-auth/react";
import {fetchUserPassword} from "@/app/(afterLogin)/user/_api/fetchUserPassword";

export default function ChangePassword() {
    const [showAlertDialog, setShowAlertDialog] = useState(false);
    const [alertMessage, setAlertMessage] = useState('');
    const [icon, setIcon] = useState<'good' | 'error' | 'warning'>('good');
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const { data: session, status } = useSession();

    const closeAlertDialog = () => {
        setShowAlertDialog(false);
    };

    const handleSave = async () => {
        if (newPassword !== confirmPassword) {
            setIcon('warning');
            setAlertMessage('New Password and Confirm Password do not match.');
            setShowAlertDialog(true);
            return;
        }

        await fetchUserPassword(session?.user.id!, newPassword, confirmPassword);
        setIcon('good');
        setAlertMessage('Password changed successfully.');
        setShowAlertDialog(true);
    };

    return (
        <>
            {showAlertDialog && (
                <AlertDialog icon={icon} message={alertMessage} onClose={closeAlertDialog} />
            )}
            <div className={style.container}>
                <div className={style.header}>
                    Change Password
                </div>
                <div className={style.mainSection}>
                    <div className={style.inputBox}>
                        <InputBox
                            label={"Old Password"}
                            value={oldPassword}
                            onChange={setOldPassword}
                            type="password"
                        />
                        <InputBox
                            label={"New Password"}
                            value={newPassword}
                            onChange={setNewPassword}
                            type="password"
                        />
                        <InputBox
                            label={"New Password Check"}
                            value={confirmPassword}
                            onChange={setConfirmPassword}
                            type="password"
                        />
                        <GreenButton name={'SAVE'} onClick={handleSave} />
                    </div>
                </div>
                <section className={style.subSection}>
                    <div className={style.explanation}>
                        <p>password rules...</p>
                        <p>password rules...</p>
                        <p>password rules...</p>
                    </div>
                </section>
            </div>
        </>
    );
}