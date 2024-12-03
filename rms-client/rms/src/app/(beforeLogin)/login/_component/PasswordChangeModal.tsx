'use client';

import React, {useState} from "react";
import style from './passwordChangeModal.module.css';
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import BlueButton from "@/app/_component/BlueButton";
import {User} from "@/model/User";
import {postUserPassword} from "@/app/(beforeLogin)/_api/postUserPassword";


type Props = {
    closeModal: () => void;
}

export default function PasswordChangeModal({closeModal}: Props) {
    const [user, setUser] = useState<User>();
    const [message, setMessage] = useState<string | null>(null);

    const handleChange = (path: string, value: string) => {
        setUser(prevState => ({
            ...prevState,
            ...setUserKeyValue({ ...prevState }, path, value)
        }));
    };

    const setUserKeyValue = (obj: any, path: string, value: any) => {
        const newObj = { ...obj };
        newObj[path] = value;
        return newObj;
    };

    const getMail = async () => {
        if (!user) {
            setMessage("Please correct the word");
            return;
        }
        const res = await postUserPassword(user)
        if (res.status === 200) {
            alert("success!");
            closeModal();
        } else {
            setMessage("Please correct the word");
        }
    }

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <section className={style.modalHeader}>
                    <div className={style.modalClose} onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </div>
                </section>
                <section className={style.modalBody}>
                    <InputBox
                        label={"User Id"}
                        // value={}
                        onChange={(value) => handleChange('id', value)}
                    />
                    <InputBox
                        label={"E-Mail"}
                        // value={}
                        onChange={(value) => handleChange('email', value)}
                    />
                    <BlueButton name={"Send Mail"} onClick={getMail}/>
                    <div>
                        <label className={style.modalText}>
                            Enter your user account and email
                            <br/>
                            and click the button.
                        </label>
                    </div>
                    {message && <div className={style.message}>{message}</div>}
                </section>
            </div>
        </div>
)
}