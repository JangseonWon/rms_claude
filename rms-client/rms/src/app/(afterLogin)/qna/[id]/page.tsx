import style from './page.module.css';
import React from "react";
import Answer from "@/app/(afterLogin)/qna/[id]/_component/Answer";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.innerBody}>
                <Answer/>
            </div>
        </div>
    )
}