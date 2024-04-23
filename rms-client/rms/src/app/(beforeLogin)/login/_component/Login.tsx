"use client"

import {ChangeEventHandler, FormEventHandler, useState} from "react";
import {signIn} from "next-auth/react";
import {useRouter} from "next/navigation";

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
        <div>
            <form onSubmit={onSubmit}>
                <div>
                    <div>
                        <label htmlFor="id">아이디</label>
                        <input id="id" value={id} onChange={onChangeId} type="text" placeholder=""/>
                    </div>
                    <div>
                        <label htmlFor="password">비밀번호</label>
                        <input id="password" value={password} onChange={onChangePassword} type="password" placeholder=""/>
                    </div>
                </div>
                <div>
                    <button disabled={!id && !password}>로그인하기</button>
                </div>
            </form>
        </div>
    )
}