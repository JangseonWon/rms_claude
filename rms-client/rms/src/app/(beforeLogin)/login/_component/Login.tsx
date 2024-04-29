"use client"

import React, {ChangeEventHandler, FormEventHandler, useState} from "react";
import {signIn} from "next-auth/react";
import {useRouter} from "next/navigation";
import style from "@/app/(beforeLogin)/login/_component/login.module.css"

export default function Login() {
    const [id, setId] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const router = useRouter();

    const onSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
        e.preventDefault();
        try {
            const response = await signIn("credentials", {
                username: id,
                password,
                redirect: false,
            })
            if (!response?.ok) {
                setMessage('아이디와 비밀번호가 일치하지 않습니다.');
            } else {
                router.replace('/home');
            }
        } catch (err) {
            console.error(err);
            setMessage('아이디와 비밀번호가 일치하지 않습니다.');
        }
    };
    const onChangeId: ChangeEventHandler<HTMLInputElement> = (e) => {
        setId(e.target.value);
    };
    const onChangePassword: ChangeEventHandler<HTMLInputElement> = (e) => {
        setPassword(e.target.value);
    };

    return (
        <div className={style.container}>
            <div className={style.left}>
                <img src='/login-img.png' alt="img"/>
            </div>
            <div className={style.right}>
                <div className={style.horizontalLogo}>
                    <img className={style.logo} src='/gc-logo.png' alt="img"/>
                    <div className={style.labelContainer}>
                        <label className={style.mainLabel}>Healthcare Provider</label>
                        <label className={style.subLabel}>Welcome to GC Genome Corp.</label>
                    </div>
                </div>
                <form onSubmit={onSubmit}>
                    <div className={style.login}>
                        <label className={style.loginLabel} htmlFor="id">Username</label>
                        <input className={style.loginInput} id="id" value={id} onChange={onChangeId}
                               type="text" placeholder=""/>
                    </div>
                    <div className={style.login}>
                        <label className={style.loginLabel} htmlFor="password">Password</label>
                        <input className={style.loginInput} id="password" value={password}
                               onChange={onChangePassword}
                               type="password" placeholder=""/>
                    </div>
                    <div className={style.login}>
                        <button className={style.loginButton} disabled={!id && !password}>Log In</button>
                        <button className={style.healthcareButton}>Not a Healthcare Provider?</button>
                    </div>
                </form>
            </div>
        </div>
    )
}