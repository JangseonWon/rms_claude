import style from './page.module.css';
import React from "react";
import NoticePage from "@/app/(afterLogin)/qna/notice/[id]/_component/NoticePage";

export default async function Page() {

    return(
        <div className={style.container}>
            <div className={style.innerBody}>
                <NoticePage/>
            </div>
        </div>
    )
}