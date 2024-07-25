import style from './page.module.css';
import React from "react";
import Question from "@/app/(afterLogin)/qna/question/_component/Question";

export default async function Page() {

    return(
        <div className={style.container}>
            <div className={style.innerBody}>
                <Question/>
            </div>
        </div>
    )
}