'use client';

import style from './changePassword.module.css';
import * as React from "react";
import {useState} from "react";
import InputBox from "@/app/_component/InputBox";
import GreenButton from "@/app/_component/GreenButton";
import {useSession} from "next-auth/react";
import {fetchUserPassword} from "@/app/(afterLogin)/user/_api/fetchUserPassword";
import {
    useOpenAlertDialogA,
    useSetIconAlertDialogA,
    useSetMessageAlertDialogA
} from "@/store/useAfterLoginAlertDialogStore";

export default function ChangePassword() {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const { data: session } = useSession();

    const setShowAlertDialog = useOpenAlertDialogA();
    const setMessage = useSetMessageAlertDialogA();
    const setIcon = useSetIconAlertDialogA();

    const handleSave = async () => {
        if (newPassword !== confirmPassword) {
            setIcon('warning');
            setMessage('New Password and Confirm Password do not match.');
            setShowAlertDialog(true);
            return;
        }

        await fetchUserPassword(session?.user.id!, newPassword, confirmPassword);
        setIcon('good');
        setMessage('Password changed successfully.');
        setShowAlertDialog(true);
    };

    return (
        <>
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
                        {/*<p>password rules...</p>*/}
                        {/*<p>password rules...</p>*/}
                        {/*<p>password rules...</p>*/}
                    </div>
                </section>
            </div>
        </>
    );
}