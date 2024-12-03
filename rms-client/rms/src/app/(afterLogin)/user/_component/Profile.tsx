"use client"

import style from "@/app/(afterLogin)/user/_component/profile.module.css";
import * as React from "react";
import {useCallback, useEffect, useState} from "react";
import GreenButton from "@/app/_component/GreenButton";
import InputBox from "@/app/_component/InputBox";
import {getUser} from "@/app/(afterLogin)/user/_api/getUser";
import {User} from "@/model/User";
import {useSession} from "next-auth/react";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {patchUser} from "@/app/(afterLogin)/user/_api/patchUser";

export default function Profile() {
    const {data: session, status} = useSession();
    const [user, setUser] = useState<User>();

    const fetchUser = useCallback( async () => {
        if(session?.user?.id && status === "authenticated"){
            const res = await getUser(session.user.id)
            if(res.ok) {
                const data = await res.json()
                setUser(data as User)
            }
            else alert("fail")
        }
    },[session?.user.id, status])
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
    const handleOnClickSave = async () => {
        if (!user) {
            alert("Please correct the word");
            return;
        }
        const res = await patchUser(user)
        if (res.status === 200) {
            alert("success!");
        } else {
            alert("fail!");
        }
    }

    useEffect(() => {
        fetchUser();
    }, [fetchUser]);

    return (
        <>
            {session?.user? (
                <div className={style.container}>
                    <div className={style.header}>
                        User setting
                    </div>
                    <section className={style.section}>
                        <div className={style.subTitle}>
                            Sub Title
                        </div>
                        <div className={style.input}>
                            <div className={style.inputBox}>
                                <InputBox disabled={true} label={"ID"} value={user?.id}/>
                            </div>
                            <div>
                                <InputBox label={"NAME"}
                                          value={user?.name || ""}
                                          onChange={(value) => handleChange('name', value)}/>
                            </div>
                        </div>
                    </section>
                    <section className={style.middleSection}>
                        <div className={style.subTitle}>
                            Sub Title
                        </div>
                        <div className={style.input}>
                            <div className={style.inputBox}>
                                <InputBox label={"EMAIL"} value={user?.email || ""}
                                          onChange={(value) => handleChange('email', value)}/>
                            </div>
                            <div>
                                <InputBox label={"PHONE-NUMBER"} value={user?.phone_number || ""}
                                          onChange={(value) => handleChange('phone_number', value)}/>
                            </div>
                        </div>
                    </section>
                    <section className={style.section}>
                        <div className={style.detailSentence}>
                            <div className={style.detailFName}>
                                To update your Personal details, including FName and LName, contact our
                            </div>
                            <div className={style.detailSupport}>
                                &nbsp;support team.
                            </div>
                        </div>
                        <div className={style.saveButton}>
                            <GreenButton name={"Save"} onClick={handleOnClickSave}/>
                        </div>
                    </section>
                </div>
            ) : (
                <div className={style.loading}>
                    <Loading/>
                </div>
            )}
        </>
    );
}
