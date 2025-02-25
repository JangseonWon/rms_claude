import React from 'react';
import style from "@/app/(afterLogin)/user/_component/userHeader.module.css";
import {useSession} from "next-auth/react";
import {Role} from "@/model/Role";

type Props = {
    setUserPage: (component: string) => void;
};

export default function UserHeader({ setUserPage }: Props) {
    const { data: session } = useSession();

    return (
        <div className={style.headerTag}>
            <div className={style.tag} onClick={() => setUserPage('Profile')}>Profile</div>
            <div className={style.tag} onClick={() => setUserPage('ChangePassword')}>Change Password</div>
            {session?.user?.role === Role.USER.valueOf() && (
                <div className={style.tag} onClick={() => setUserPage('Institutions')}>Institutions</div>
            )}
        </div>
    );
}