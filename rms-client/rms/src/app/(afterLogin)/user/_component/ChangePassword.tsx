'use client';

import style from './changePassword.module.css';
import * as React from "react";
import {useState} from "react";
import InputBox from "@/app/_component/InputBox";
import GreenButton from "@/app/_component/GreenButton";
import {useSession} from "next-auth/react";
import {patchUser} from "@/app/(afterLogin)/user/_api/patchUser";
import {
    useOpenAlertDialogA,
    useSetIconAlertDialogA,
    useSetMessageAlertDialogA
} from "@/store/useAfterLoginAlertDialogStore";
import {User} from "@/model/User";

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
        const updatedUser: User = {
            id: session?.user.id!,
            password: newPassword
        };

        try {
            const response = await patchUser(updatedUser);

            if (response.status === 200) {
                setIcon('good');
                setMessage('Password changed successfully.');
            } else {
                setIcon('error');
                setMessage('Failed to change password. Please try again.');
            }
        } catch (error) {
            setIcon('error');
            setMessage('An unexpected error occurred. Please try again later.');
        } finally {
            setShowAlertDialog(true);
        }
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
                        <p>Password must be at least 10 characters long.</p>
                        <p>Password must include both uppercase and lowercase letters.</p>
                        <p>Password must contain at least one special character (e.g., !, @, #, $, etc.).</p>
                    </div>
                </section>
            </div>
        </>
    );
}