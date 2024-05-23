import React from 'react';
import style from "@/app/(afterLogin)/user/_component/userHeader.module.css";

type Props = {
    setUserPage: (component: string) => void;
};

export default function UserHeader({ setUserPage }: Props) {
    return (
        <div className={style.headerTag}>
            <div className={style.firstTag} onClick={() => setUserPage('Profile')}>Profile</div>
            <div className={style.middleTag} onClick={() => setUserPage('ChangePassword')}>Change Password</div>
            <div className={style.shortTag} onClick={() => setUserPage('Institutions')}>Institutions</div>
        </div>
    );
}