import React from 'react';
import style from "@/app/(afterLogin)/manager/_component/managerHeader.module.css";

type Props = {
    setManagerPage: (component: string) => void;
};

export default function ManagerHeader({ setManagerPage }: Props) {

    return (
        <div className={style.headerTag}>
            <div className={style.tag} onClick={() => setManagerPage('OrderDelete')}>Order Delete</div>
            <div className={style.tag} onClick={() => setManagerPage('UserHistory')}>User History</div>
            <div className={style.tag} onClick={() => setManagerPage('OldRequestOrder')}>Old Request Order</div>
        </div>
    );
}