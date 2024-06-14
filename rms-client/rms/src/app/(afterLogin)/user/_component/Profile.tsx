import style from "@/app/(afterLogin)/user/_component/profile.module.css";
import * as React from "react";
import {useEffect, useState} from "react";
import GreenButton from "@/app/_component/GreenButton";
import InputBox from "@/app/_component/InputBox";
import {fetchUser} from "@/app/(afterLogin)/user/_api/fetchUser";
import {User} from "@/model/User";
import {useSession} from "next-auth/react";
import {fetchUserUpdate} from "@/app/(afterLogin)/user/_api/fetchUserUpdate";

export default function Profile() {
    const { data: session, status } = useSession();
    const [user, setUser] = useState<User>();
    const userBody: User = {};
    const userId: string | undefined = user?.id;

    useEffect(() => {
        if (status === "authenticated" && session?.user?.id) {
            const userId = session.user.id;
            fetchUser(userId)
                .then((data) => {
                    setUser(data);
                })
                .catch((error) => {
                    console.error('Error fetching user:', error);
                });
        }
    }, [status, session]);

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
        try {
            await fetchUserUpdate(userBody, userId);
            alert("test 성공")
        } catch (error) {
            alert("error 실패")
        }
    }

    return (
        <>
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
                            <InputBox disabled={true} label={"ID"} value={userId || ""} />
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
                        <GreenButton name={"Save"} onClick={handleOnClickSave} />
                    </div>
                </section>
            </div>
        </>
    );
}
