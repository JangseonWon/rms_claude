"use client"

import React, { ChangeEventHandler, FormEventHandler, useState } from "react";
import { signIn } from "next-auth/react";
import { useRouter } from "next/navigation";
import style from "@/app/(beforeLogin)/login/_component/login.module.css";
import { useSetLoginUser } from "@/store/LoginUser";
import Image from "next/image";
import loginImg from "@/../public/login-img.png";
import logoImg from "@/../public/gc-logo.png";
import AlertDialog from "@/app/_component/AlertDialog";

export default function Login() {
    const [id, setId] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(false);
    const [showAlertDialog, setShowAlertDialog] = useState(false);
    const router = useRouter();
    const setUserId = useSetLoginUser();

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
                throw new Error('아이디나 비밀번호가 일치하지 않습니다.');
            } else {
                setUserId(id);
                router.replace('/home');
            }
        } catch (err) {
            console.error(err);
            setMessage('아이디나 비밀번호가 일치하지 않습니다.');
            console.log(message);
            setShowAlertDialog(true);
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

    const handleCloseAlert = () => {
        setShowAlertDialog(false);
        setMessage('');
    };

    return (
        <div className={style.container}>
            {showAlertDialog && (
                <AlertDialog icon="error" message={message} onClose={handleCloseAlert} />
            )}
            <div className={style.left}>
                <Image src={loginImg} alt="img" />
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
                        <button className={style.healthcareButton}>Not a Healthcare Provider?</button>
                    </div>
                </form>
            </div>
        </div>
    );
}
