import style from '@/css/qna/qnaPage.module.css';
import React from "react";
import QnaWritingPage from "@/app/(afterLogin)/qna/_component/QnaWritingPage";

export default async function Page() {

    return(
        <div className={style.container}>
            <div className={style.innerBody}>
                <QnaWritingPage category={'Notice'}/>
            </div>
        </div>
    )
}