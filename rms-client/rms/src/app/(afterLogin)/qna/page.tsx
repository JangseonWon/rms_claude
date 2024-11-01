import style from './page.module.css';
import React from "react";
import QnaMainPage from "@/app/(afterLogin)/qna/_component/QnaMainPage";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.innerBody}>
                <QnaMainPage/>
            </div>
        </div>
    )
}