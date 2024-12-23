'use client';

import style from './changePassword.module.css';
import * as React from "react";
import {useState} from "react";
import InputBox from "@/app/_component/InputBox";
import GreenButton from "@/app/_component/GreenButton";
import {useSession} from "next-auth/react";
import {patchUser} from "@/app/(afterLogin)/user/_api/patchUser";
import {User} from "@/model/User";
import LoadingFullScreen from "@/app/_component/LoadingFullScreen";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

export default function ChangePassword() {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const { data: session } = useSession();
    const showAlert = CallAlertDialog();

    const handleSave = async () => {
        try {
            setIsLoading(true);
            if (!newPassword || !confirmPassword) {
                showAlert('Both New Password and Confirm Password are required.');
                return;
            }

            if (newPassword !== confirmPassword) {
                showAlert('New Password and Confirm Password do not match.');
                return;
            }
            const updatedUser: User = {
                id: session?.user.id!,
                password: newPassword
            };

            const response = await patchUser(updatedUser);

            if (response.status === 200) {
                showAlert('Password changed successfully.');
            } else {
                showAlert('Failed to change password. Please try again.');
            }
        } catch (error) {
            showAlert('An unexpected error occurred. Please try again later.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <>
            {isLoading && <LoadingFullScreen/>}
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