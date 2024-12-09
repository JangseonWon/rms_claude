"use client"

import React, {ChangeEventHandler, FormEventHandler, useState} from "react";
import {signIn} from "next-auth/react";
import {useRouter} from "next/navigation";
import style from "@/app/(beforeLogin)/login/_component/login.module.css";
import Image from "next/image";
import loginImg from "@/../public/login-img.png";
import logoImg from "@/../public/gc-logo.png";
import {
    useOpenAlertDialogB,
    useSetIconAlertDialogB,
    useSetMessageAlertDialogB
} from "@/store/useBeforeLoginAlertDialogStore";
import PasswordChangeModal from "@/app/(beforeLogin)/login/_component/PasswordChangeModal";

export default function Login() {
    const [id, setId] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [passwordModalOpen, setPasswordModalOpen] = useState<boolean>(false);
    const setShowAlertDialog = useOpenAlertDialogB();
    const setMessage = useSetMessageAlertDialogB();
    const setIcon = useSetIconAlertDialogB();

    const router = useRouter();

    const onSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await signIn("credentials", {
                username: id,
                password,
                redirect: false,
            });
            if (response?.error !== null || !response.ok) {
                setMessage(
                    'This portal is accessible only to those who have an established contractual relationship with GC Genome.\n'+
                    ' If you are a healthcare professional or distributor, please contact us at the email address below.\n'+
                    '\n'+
                    'info@gcgenome.com\n');
                setShowAlertDialog(true);
                setIcon('error');
            } else {
                router.replace('/home');
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const onChangeId: ChangeEventHandler<HTMLInputElement> = (e) => {
        setId(e.target.value);
    };

    const onChangePassword: ChangeEventHandler<HTMLInputElement> = (e) => {
        setPassword(e.target.value);
    };

    const openModal = () => {
        setPasswordModalOpen(true);
    }

    const closeModal = () => {
        setPasswordModalOpen(false);
    }

    return (
        <div className={style.container}>
            <div className={style.left}>
                <Image src={loginImg} fill alt="img" style={{objectFit: 'cover'}}/>
            </div>
            <div className={style.right}>
                <div className={style.horizontalLogo}>
                    <Image className={style.logo} src={logoImg} alt="img" />
                    <div className={style.labelContainer}>
                        <label className={style.mainLabel}>Healthcare Provider</label>
                        <label className={style.subLabel}>Welcome to GC Genome Corp.</label>
                    </div>
                </div>
                <form onSubmit={onSubmit}>
                    <div className={style.login}>
                        <label className={style.loginLabel} htmlFor="id">Username</label>
                        <input className={style.loginInput} id="id" value={id} onChange={onChangeId}
                               type="text" placeholder="" />
                    </div>
                    <div className={style.login}>
                        <label className={style.loginLabel} htmlFor="password">Password</label>
                        <input className={style.loginInput} id="password" value={password}
                               onChange={onChangePassword}
                               type="password" placeholder="" />
                    </div>
                    <div className={style.login}>
                        <button className={style.loginButton} disabled={!id || !password || loading}>
                            {loading ? <div className={style.spinner}></div> : 'Log In'}
                        </button>
                        <label className={style.changePassword} onClick={openModal}>Password reissue</label>
                    </div>
                </form>
                {passwordModalOpen && (
                    <PasswordChangeModal closeModal={closeModal}/>
                )}
            </div>
        </div>
    );
}
